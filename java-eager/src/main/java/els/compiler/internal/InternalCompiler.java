// Forked from JavacTaskImpl
/*
 * Copyright (c) 2015, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package els.compiler.internal;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.api.MultiTaskListener;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.*;
import com.sun.tools.javac.main.Arguments;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DefinedBy;
import com.sun.tools.javac.util.DefinedBy.Api;
import com.sun.tools.javac.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.*;

/**
 * A pool of reusable JavacTasks. When a task is no valid anymore, it is returned to the pool, and its Context may be
 * reused for future processing in some cases. The reuse is achieved by replacing some components (most notably
 * JavaCompiler and Log) with reusable counterparts, and by cleaning up leftovers from previous compilation.
 *
 * <p>For each combination of options, a separate task/context is created and kept, as most option values are cached
 * inside components themselves.
 *
 * <p>When the compilation redefines sensitive classes (e.g. classes in the the java.* packages), the task/context is
 * not reused.
 *
 * <p>When the task is reused, then packages that were already listed won't be listed again.
 *
 * <p>Care must be taken to only return tasks that won't be used by the original caller.
 *
 * <p>Care must also be taken when custom components are installed, as those are not cleaned when the task/context is
 * reused, and subsequent getTask may return a task based on a context with these custom components.
 *
 * <p><b>This is NOT part of any supported API. If you write code that depends on this, you do so at your own risk. This
 * code and its internal interfaces are subject to change or deletion without notice.</b>
 */
public class InternalCompiler {
    private static final Logger logger = LoggerFactory.getLogger(InternalCompiler.class);
    private static final JavacTool javaCompiler = JavacTool.create();

    private List<String> currentOptions;
    private ReusableContext currentContext;
    protected boolean checkedOut;

    public InternalCompiler() {
    }

    public CompileTask checkoutCompileTask(
            CompileParameter parameter
            ) {
        if (checkedOut) {
            throw new RuntimeException("The ReusableCompiler is already in-use!");
        }
        checkedOut = true;
        var diagnostics = new ArrayList<Diagnostic<? extends JavaFileObject>>();
        currentOptions = parameter.options;
        currentContext = new ReusableContext(currentOptions);
        JavacTaskImpl task = (JavacTaskImpl) javaCompiler.getTask(null,
                parameter.fileManager,
                diagnostics::add,
                currentOptions,
                List.of(),
                parameter.files,
                currentContext);
        task.addTaskListener(currentContext);
        return new CompileTask(
                parameter,
                diagnostics,
                task,
                currentContext,
                () -> checkedOut = false,
                currentOptions);
    }


    protected static class ReusableContext extends Context implements TaskListener {

        List<String> arguments;

        ReusableContext(List<String> arguments) {
            super();
            this.arguments = arguments;
            put(Log.logKey, ReusableLog.factory);
            put(JavaCompiler.compilerKey, ReusableJavaCompiler.factory);
        }

        void clear() {
            drop(Arguments.argsKey);
            drop(DiagnosticListener.class);
            drop(Log.outKey);
            drop(Log.errKey);
            drop(JavaFileManager.class);
            drop(JavacTask.class);
            drop(JavacTrees.class);
            drop(JavacElements.class);

            if (ht.get(Log.logKey) instanceof ReusableLog) {
                // log already inited - not first round
                ((ReusableLog) Log.instance(this)).clear();
                Enter.instance(this).newRound();
                ((ReusableJavaCompiler) ReusableJavaCompiler.instance(this)).clear();
                Types.instance(this).newRound();
                Check.instance(this).newRound();
                Modules.instance(this).newRound();
                Annotate.instance(this).newRound();
                CompileStates.instance(this).clear();
                MultiTaskListener.instance(this).clear();
            }
        }

        @Override
        @DefinedBy(Api.COMPILER_TREE)
        public void finished(TaskEvent e) {
            // do nothing
        }

        @Override
        @DefinedBy(Api.COMPILER_TREE)
        public void started(TaskEvent e) {
            // do nothing
        }

        <T> void drop(Key<T> k) {
            ht.remove(k);
        }

        <T> void drop(Class<T> c) {
            ht.remove(key(c));
        }

        /**
         * Reusable JavaCompiler; exposes a method to clean up the component from leftovers associated with previous
         * compilations.
         */
        static class ReusableJavaCompiler extends JavaCompiler {

            static final Factory<JavaCompiler> factory = ReusableJavaCompiler::new;

            ReusableJavaCompiler(Context context) {
                super(context);
            }

            @Override
            public void close() {
                // do nothing
            }

            void clear() {
                newRound();
            }

            @Override
            protected void checkReusable() {
                // do nothing - it's ok to reuse the compiler
            }
        }

        /**
         * Reusable Log; exposes a method to clean up the component from leftovers associated with previous
         * compilations.
         */
        static class ReusableLog extends Log {

            static final Factory<Log> factory = ReusableLog::new;

            Context context;

            ReusableLog(Context context) {
                super(context);
                this.context = context;
            }

            void clear() {
                recorded.clear();
                sourceMap.clear();
                nerrors = 0;
                nwarnings = 0;
                // Set a fake listener that will lazily lookup the context for the 'real' listener. Since
                // this field is never updated when a new task is created, we cannot simply reset the field
                // or keep old value. This is a hack to workaround the limitations in the current infrastructure.
                diagListener =
                        new DiagnosticListener<>() {
                            DiagnosticListener<JavaFileObject> cachedListener;

                            @Override
                            @DefinedBy(Api.COMPILER)
                            @SuppressWarnings("unchecked")
                            public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                                if (cachedListener == null) {
                                    cachedListener = context.get(DiagnosticListener.class);
                                }
                                cachedListener.report(diagnostic);
                            }
                        };
            }
        }
    }
}

package org.javacs.example;

import java.util.List;
import java.io.File;
import com.google.gson.*;
import org.javacs.action.TestAddImport;
import A.B.C;

public class AutocompleteMemberFixed {
    public static TestAddImport fieldStatic;
    public C field;
    private static AutocompleteMemberFixed fieldStaticPrivate;
    private static List<String> list;
    private Gson fieldPrivate;
    private int fieldPrivate2;
    private Gson[] fieldPrivate3;

    private static String methodStaticPrivate(File file) {
        return "foo";
    }
    private String methodPrivate() {
        return "foo";
    }
    public void test() {
        this.field;
    }
    public static String methodStatic() {
        return "foo";
    }
    public String method() throws Exception {
        return "foo";
    }

}

class A{

}
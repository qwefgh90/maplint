package mybatis.project;

import com.sun.tools.javac.code.Symbol;
import els.type.MemberMethod;

public class AmbiguousMemberMethod extends MemberMethod {
    public AmbiguousMemberMethod(MemberMethod memberMethod) {
        super(memberMethod.name, memberMethod.deprecated, memberMethod.location, memberMethod.containerName, memberMethod.returnType, memberMethod.paramters, (Symbol.MethodSymbol) memberMethod.originalSymbol);
    }
}

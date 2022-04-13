package mybatis.parser.keygen;

/**
 * @author qwefgh90
 */
public class ParameterMappingSignatureGenerator {
    public static String generateNumberSignature(String sql){
        String signature = getNumber();
        while(true){
            if(sql.contains(signature))
                signature = getNumber();
            else
                break;
        }
        return signature;
    }

    private static String getNumber(){
        return String.valueOf((int)(Math.random()*1000000));
    }

}

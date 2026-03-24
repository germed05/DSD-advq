
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String CLAVE = "1234567890123456"; // 16 caracteres

    public static String cifrar(String texto) throws Exception {
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cifrado = cipher.doFinal(texto.getBytes());

        return Base64.getEncoder().encodeToString(cifrado);
    }

    public static String descifrar(String textoCifrado) throws Exception {
        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));

        return new String(original);
    }
}

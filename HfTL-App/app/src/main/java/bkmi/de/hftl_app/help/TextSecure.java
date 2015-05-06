package bkmi.de.hftl_app.help;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by georg on 01.05.15.
 * Klasse zum Verschlüsseln
 */
public class TextSecure {

    private static final String ALGO = "AES";
    Context context;
    private Cipher cipher;
    private Key key;
    private AlgorithmParameterSpec spec;

    public TextSecure(Context context) throws Exception
    {
        this.context=context;
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(getIMEI(context).getBytes("UTF-8"));
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    public AlgorithmParameterSpec getIV()
    {
        byte bla[]= getIMEI(context).getBytes();
        byte[] iv;
        iv = java.util.Arrays.copyOf(bla, 16);
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);

        return ivParameterSpec;
    }

    public String encrypt(String plainText) throws Exception
    {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedText = new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");

        return encryptedText;
    }

    public String decrypt(String cryptedText) throws Exception
    {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        String decryptedText = new String(decrypted, "UTF-8");

        return decryptedText;
    }

    public static String getIMEI(Context context) {

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return  mngr.getDeviceId();

    }
}

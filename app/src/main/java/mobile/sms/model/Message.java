package mobile.sms.model;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Message {

    private Contact author;
    private Contact receiver;
    private Date sentAt;
    private String text;
    private String encryptedText;

    private static final int IV_SIZE = 16;

    public Message() {}

    public Message(String text) {
        this.text = text;
        this.encryptedText = "0x1F4";
    }

    public void setReceiver(Contact receiver) {
        this.receiver = receiver;
    }

    public void setAuthor(Contact author) {
        this.author = author;
    }

//  Found on: https://gist.github.com/itarato/abef95871756970a9dad

    public void encryptText() {
        if (receiver == null) {
            return;
        }

        if (receiver.getPrivateKey() == null) {
            return;
        }

        byte[] clean = text.getBytes();
        byte[] key = hexStringToByteArray(receiver.getPrivateKey());
        byte[] iv = hexStringToByteArray(receiver.getIv());

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(clean);

            byte[] encryptedIVAndText = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedIVAndText, iv.length, encrypted.length);

            encryptedText = byteArrayToHexString(encryptedIVAndText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void decryptText() {
        if (author == null) {
            return;
        }

        if (author.getPrivateKey() == null) {
            return;
        }

        byte[] key = hexStringToByteArray(receiver.getPrivateKey());
        byte[] iv = hexStringToByteArray(receiver.getIv());

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] original = cipher.doFinal(Base64.decodeBase64(this.encryptedText));

            text = new String(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getText() { return text; }
    public void setText(String textMessage) { text = textMessage; }

    public String getEncryptedText() {
        if (this.receiver.getPrivateKey() == null) {
            return text;
        }

        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) { this.encryptedText = encryptedText; }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length*2];
        int v;

        for(int j=0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v>>>4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}

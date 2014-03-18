package aes;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	static String IV = "AAAAAAAAAAAAAAAA";
	static byte[] encryptionKey;
	static final String SALT = "The quick brown fox jumps over the lazy dog";
	static final String ALGORITHM = "AES";
	static final String CIPHER = "AES/CBC/PKCS5Padding";
	public static void main(String[] args) {
		
		encryptionKey = "87A5CF97F3B6ABCDA92A4B3CE0994DC3C68858798381ED1DFA2A1BFCCAE804C3".getBytes();
		String plaintext = "test text 123\0\0\0";
		
		try {

			System.out.println("==Java==");
			System.out.println("plain:   " + plaintext);

			byte[] cipher = encrypt(plaintext, encryptionKey);

			System.out.print("cipher:  ");
			for (int i = 0; i < cipher.length; i++)
				System.out.print(new Integer(cipher[i]) + " ");
			System.out.println("");

			String decrypted = decrypt(cipher, encryptionKey);

			System.out.println("decrypt: " + decrypted);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] encrypt(String plainText, byte[] encryptionKey)
			throws Exception {

		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		encryptionKey = sha.digest(encryptionKey);
		final SecretKey key = new SecretKeySpec(encryptionKey, AES.ALGORITHM);
		final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		final Cipher cipher = Cipher.getInstance(AES.CIPHER);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		
		String saltedText = plainText.concat(AES.SALT);
		final byte[] cipherData = cipher.doFinal(saltedText.getBytes());
		return cipherData;

	}

	public static String decrypt(byte[] cipherText, byte[] encryptionKey)
			throws Exception {

		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		encryptionKey = sha.digest(encryptionKey);
		final SecretKey key = new SecretKeySpec(encryptionKey, AES.ALGORITHM);
		final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		final Cipher cipher = Cipher.getInstance(AES.CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		
		System.out.println("cipher text: "+new String(cipherText));
		final byte[] cipherData = cipher.doFinal(cipherText);
		
		String decryptedMsg = new String(cipherData);
		String unsaltedMsg = decryptedMsg.substring(0, decryptedMsg.length() - AES.SALT.length());
		return unsaltedMsg;
	}
}

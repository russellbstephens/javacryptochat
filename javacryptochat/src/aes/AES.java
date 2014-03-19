package aes;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	static String IV = "AAAAAAAAAAAAAAAA";
	static byte[] encryptionKey = "87A5CF97F3B6ABCDA92A4B3CE0994DC3C68858798381ED1DFA2A1BFCCAE804C3"
			.getBytes();
	static final String SALT = "The quick brown fox jumps over the lazy dog";
	static final String ALGORITHM = "AES";
	static final String CIPHER = "AES/CBC/PKCS5Padding";

	public static void main(String[] args) {

		String plaintext = "test text 123\0\0\0";

		try {

			System.out.println("==Java==");
			System.out.println("plain:   " + plaintext);

			byte[] cipher = encrypt(plaintext);

			System.out.print("cipher:  ");
			for (int i = 0; i < cipher.length; i++)
				System.out.print(new Integer(cipher[i]) + " ");
			System.out.println("");

			String decrypted = decrypt(cipher);

			System.out.println("decrypt: " + decrypted);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] encrypt(String plainText) throws Exception {

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

	public static String removeSalt(String str) {
		int saltIndex = str.indexOf(AES.SALT);
		return str.substring(0, saltIndex);
	}
	public static String decrypt(byte[] cipherText) throws Exception {

		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		encryptionKey = sha.digest(encryptionKey);
		final SecretKey key = new SecretKeySpec(encryptionKey, AES.ALGORITHM);
		final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		final Cipher cipher = Cipher.getInstance(AES.CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);

		System.out.println("cipher text: " + new String(cipherText));
//		final byte[] cipherData1 = cipher.update(cipherText);
		final byte[] cipherData1 = cipher.doFinal(cipherText);

		String decryptedMsg = new String(cipherData1);
		String unsaltedMsg = removeSalt(decryptedMsg);
		return unsaltedMsg;
	}
}

package in.nic.ashwini.ldap.utility;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	// @Value("${aes.secretkey}")
//	private static String secretKey = "6FE42F7635BF56CF2E7B7C9E5B789FE4";
	
	public static String encrypt(String content, String secretKey) {
		return aes(content, secretKey, Cipher.ENCRYPT_MODE);
	}
	
	public static String decrypt(String content, String secretKey) {
		System.out.println("AESEncryptUtil.decrypt() secretKey: "+secretKey);
		return aes(content, secretKey, Cipher.DECRYPT_MODE);
	}

	private static String aes(String content, String secretKey, int type) {
		try {
			byte[] iv = { 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33, 72, 101, 111, 32 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);
	        SecretKey tmp = createAESKey(secretKey); 
	        SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(type, secretKeySpec, ivspec);
	        if (type == Cipher.ENCRYPT_MODE) {
	        	byte[] byteContent = content.getBytes("utf-8");
				return Hex2Util.parseByte2HexStr(cipher.doFinal(byteContent));
	        } else {
				byte[] byteContent = Hex2Util.parseHexStr2Byte(content);
				return new String(cipher.doFinal(byteContent));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}
	
	private static SecretKey createAESKey(String secretKey) throws Exception {
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES");

		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		random.setSeed(secretKey.getBytes());
		
		keygenerator.init(256, random);
		SecretKey key = keygenerator.generateKey();

		return key;
	}
}
package security;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Security {

    public static final String KEY_ALGORITHM = "DES";  
    public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";  
    public static final String PRIVATE_KEY = "foreverJoy"; 
  
    private static Key toKey(byte[] key) throws Exception {  
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        return secretKey;  
    }  
      
	public static String encrypt(byte[] data) {  
		try {
	        Key k = toKey(PRIVATE_KEY.getBytes());  
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);  
	        cipher.init(Cipher.ENCRYPT_MODE, k);
	        return Base64.encode(cipher.doFinal(data));  
		}
		catch (Exception ex) {
			return null;
		}
    }  
	
	public static String encrypt(String s) {  
		return encrypt(s.getBytes());
	}
	
	public static byte[] decrypty(byte[] data) {
		try {
			Key k = toKey(PRIVATE_KEY.getBytes());  
	        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);  
	        cipher.init(Cipher.DECRYPT_MODE, k);
	        return cipher.doFinal(Base64.decode(data)); 
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	public static byte[] decrypty(String s) {  
		return decrypty(s.getBytes());
	}
	
	public static String decryptyString(String s) {  
		return new String(decrypty(s.getBytes()));
	}
	
	public static String hashString(String s) {
		char[] hash = md5(s).toCharArray();
		int n = 0;
		for(char c : hash) {
			try {
				n += (int) c;
			}
			catch (Exception ex) {
				
			}
		}
		return String.format(Locale.US, "%08d", n);
	}
	
	public static String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    }
	    catch (NoSuchAlgorithmException e) {

	    }
	    return "";
	}
        
//        public static void main(String[] args){
//            String str = encrypt("960311");
//            System.out.println(encrypt("admin"));
//        }
}

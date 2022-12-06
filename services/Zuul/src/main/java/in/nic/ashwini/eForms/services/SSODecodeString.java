package in.nic.ashwini.eForms.services;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SSODecodeString {

    public static String decyText(String encrytext, String stringResOfSecKey) throws Exception {
        byte[] cipherTextFromHex = hexStringToByteArray(encrytext);
        SecretKey secretKeyConvertedFromStringKey = getSecretKeyFromString(stringResOfSecKey);
        String decryptedText="";
        try{
         decryptedText = decryptText(cipherTextFromHex, secretKeyConvertedFromStringKey);
        }
        catch(Exception e){
            
        decryptedText="";
        }
        System.out.println("decyText() method of AESEncryption class return String :  " + decryptedText);
        return decryptedText;
    }

    public static SecretKey getSecretKeyFromString(String stringKey) {
        byte[] encodedKey = stringKey.getBytes();
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        System.out.println("getSecretKeyFromString() method of AESEncryption class return SecretKey :  " + secretKey);
        return secretKey;
    }

    public static byte[] hexStringToByteArray(String s) {
        System.out.println("hexStringToByteArray() method of AESEncryption class called");
        System.out.println("get parameters  : " + s);
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        System.out.println("hexStringToByteArray() method of AESEncryption class return byte[] :  " + data);
        return data;
    }

    public static String decryptText(byte[] byteCipherText, SecretKey secKey) throws Exception {
        System.out.println("decryptText() method of AESEncryption class called");
        System.out.println("get parameters byteCipherText : " + byteCipherText + " and secKey : " + secKey);
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        System.out.println("decryptText() method of AESEncryption class return string :  " + bytePlainText);
        return new String(bytePlainText);
    }

//    public static void main(String[] args) {
//        String response = "B33B279D96B565AA6BC8161D017244C2958B4D865A141B972C37F3C3FA97A262652010A85A2AE4594867850A52193E891378EB948BCE7B5B3FD386333D5AAC611A8DCFEB555F656FB3C380779A3B66A1279E959D98D88F4A78448C109611EC020D1C7D5C8B2757BFEC52355D346F3611DCE19E1F4DB1E41DDD7EEBFFD1201723997E7071B4224234D1BD68D1660A7300AA49D54C4BF36244C2D5B55A74ED7C06A4FF33B85976DFD05091BE981831EFC8CB3AEA125B8D9A5CCB875B123E2795F273248581DE1B8A46F2380E45EF8AB3AA90B1EF766398E067BC7AAA80CDE39194852D832008C576E9122D02EC7656E617C8F27E058DC72379DD555ED6B9D3CA613D4846CECBBEDB18B132F7A6454022DBB450C9E0579831787BFD1E6626F51F49C31007DF7C7747B63D8D75E84C3EF4FB33C113C71C0C042A14C7C0C3AEACD5C44934D6896524FD59B7354626EBA1FA8F0774D101F6152D8057F74A673F4640DBAD2648537A113EF93935480AC81AB08883EB22328371B54CB07CA99552514B5A7D20EAE75BCE2DB51FA9A63869DD888E8B5BD21F9592E735ADB9AE71FE9CA60914A918029B553FCEDFBC0A0491B33F290900CF608325F4EC06B5E38CE05B4ACAB34E33AE554D51444D4CDF6D113E2CA556E0D6EBFA483ACE02D415A3B628C66D6F43371C8DAF3D06E3A59032D67CC98424C69E2DEA1C8BB40568FA005A1A930F598A30FFAD415734F2ABF8A673156BF0512BED1B8BCAF53B38A9C149360408B3C20880C4187486229C1E1DB0CA6919DC51411D5B3A4501A415A7D1B0798BB15AB6D923CE096EF6D086B6C0FD0C33C881281BEBECCCEBF162338E70521C793B2AA40BAA1D2F0ED38204EE8FF704BF2D4FCDC74B6D0BFBD8398B7B65A14876391C37D53E5D7185AAFDDFF9525D62077B0EBACB80D20FE50918FA72DCD29E40EAF6CCC924FF5BE75FE6EEB17C958B986F925536B02F576F71AA325153C3187803C92461944E351F0B90653191F3723AE19EBB51FAAE38288F1C2FE0D7405A99D24E3F5D1C3FB3D6136D5A5BC7A981AD9AF346C862068B9C1123558F01C03E7207C2DBA4B3A639F154D84D22F6AFE8D8A4B784F8448ABF37CA38D20FC534935423DD";
//        try {
//            System.out.println("DDD" + decyText(response, "LSWacKy+rwqMtcCrrmbCvTdWPK0Agtkn"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}


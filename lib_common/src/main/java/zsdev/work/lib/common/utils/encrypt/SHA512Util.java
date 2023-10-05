package zsdev.work.lib.common.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created: by 2023-09-26 10:15
 * Description: SHA加密 不可逆
 * Author: 张松
 */
public class SHA512Util {

    private SHA512Util() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * SHA-512 加密
     * StringBuilder不支持并发操作，线性不安全的，不适合多线程中使用。但其在单线程中的性能比StringBuffer高。
     *
     * @param str
     * @return
     */
    public static String encryptSHAForBuilder(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(str.getBytes());
            byte[] cipher = digest.digest();

            for (byte b : cipher) {
                String hexStr = Integer.toHexString(b & 0xff);
                builder.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * SHA-512 加密
     * StringBuffer支持并发操作，线性安全的，适合多线程中使用。
     *
     * @param str
     * @return
     */
    public static String encryptSHAForBuffer(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(str.getBytes());
            byte[] cipher = digest.digest();

            for (byte b : cipher) {
                String hexStr = Integer.toHexString(b & 0xff);
                buffer.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}

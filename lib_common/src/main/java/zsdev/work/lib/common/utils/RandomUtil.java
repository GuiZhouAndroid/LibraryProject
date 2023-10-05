package zsdev.work.lib.common.utils;

import java.util.Random;

/**
 * Created: by 2023-08-07 23:56
 * Description: 随机数工具类
 * Author: 张松
 */
public class RandomUtil {
    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS = "0123456789";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    private RandomUtil() {
        throw new AssertionError();
    }

    /**
     * 获取一个固定长度的随机字符串，它是大小写字母和数字的混合体
     *
     * @param length 长度
     * @return 随机字符串
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    /**
     * 得到一个固定长度的随机字符串，它是数字的混合
     *
     * @param length 长度
     * @return 随机字符串
     * @see RandomUtil#getRandom(String source, int length)
     */
    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    /**
     * 获取一个固定长度的随机字符串，它是大小写字母的混合体
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    /**
     * 获取一个固定长度的随机字符串，它是大写字母的混合物
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }

    /**
     * 获取一个固定长度的随机字符串，它是小写字母的混合物
     *
     * @param length 长度
     * @return 机字符串
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**
     * 获取一个固定长度的随机字符串，它是源中字符的混合
     *
     * @param length 长度
     * @param source 源
     * @return 如果源为null或空，则返回null
     */
    public static String getRandom(String source, int length) {
        return source == null ? null : getRandom(source.toCharArray(), length);
    }

    /**
     * 获取一个固定长度的随机字符串，它是sourceChar中字符的混合
     *
     * @param sourceChar 资源数组
     * @param length     长度
     * @return 如果sourceChar为null或空，则返回null 如果长度小于0，则返回null
     */
    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * 获取0和max之间的随机int
     *
     * @param max 最大值
     * @return 如果max<= 0 ， 则返回0 返回0到max之间的随机int
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }

    /**
     * 获取最小值和最大值之间的随机int
     *
     * @param max 最大值
     * @param min 最小值
     * @return 如果min>max，则返回0 如果min==max，
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        //则返回min 返回介于min和max之间的随机int
        return min + new Random().nextInt(max - min);
    }

    /**
     * Shuffling算法，使用默认的随机源随机排列指定的数组
     */
    public static boolean shuffle(Object[] objArray) {
        if (objArray == null) {
            return false;
        }
        return shuffle(objArray, getRandom(objArray.length));
    }

    /**
     * Shuffling算法，随机排列指定的数组
     */
    public static boolean shuffle(Object[] objArray, int shuffleCount) {
        int length;
        if (objArray == null || shuffleCount < 0 || (length = objArray.length) < shuffleCount) {
            return false;
        }

        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            Object temp = objArray[length - i];
            objArray[length - i] = objArray[random];
            objArray[random] = temp;
        }
        return true;
    }

    /**
     * Shuffling算法，使用默认随机源随机排列指定的int数组
     */
    public static int[] shuffle(int[] intArray) {
        if (intArray == null) {
            return null;
        }

        return shuffle(intArray, getRandom(intArray.length));
    }

    /**
     * Shuffling算法，随机排列指定的int数组
     */
    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray == null || shuffleCount < 0 || (length = intArray.length) < shuffleCount) {
            return null;
        }

        int[] out = new int[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            out[i - 1] = intArray[random];
            int temp = intArray[length - i];
            intArray[length - i] = intArray[random];
            intArray[random] = temp;
        }
        return out;
    }
}

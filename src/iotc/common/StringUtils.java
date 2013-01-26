package iotc.common;

/**
 * String utilities
 * User: atsushi-o
 * Date: 13/01/27
 * Time: 4:40
 */
public class StringUtils {
    private StringUtils(){};

    /**
     * Stringオブジェクトがnull値かどうかチェックする
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return str == null ? true : false;
    }

    /**
     * Stringオブジェクトがnullもしくは空白であるかチェックする
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return (isNull(str) || str.length() == 0);
    }
}

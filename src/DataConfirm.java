import org.jetbrains.annotations.NotNull;

/**
 * @Title: DataConfirm
 * @Description: To confirm the data input from users is legal
 * @Author: YuYoung
 * @Date: 2021/7/16 15:41
 */
public class DataConfirm {
    /**
     * 判断五个字符串是否都为空
     * @param str1
     * @param str2
     * @param str3
     * @param str4
     * @param str5
     * @return true when all str not empty else false
     */
    boolean isNotEmpty5( String str1, String str2, String str3, String str4, String str5) {
        return !str1.isEmpty() && !str2.isEmpty() && !str3.isEmpty() && !str4.isEmpty() && !str5.isEmpty();
    }


}

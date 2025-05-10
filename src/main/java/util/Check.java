package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check {
    public static boolean checkInt(String str) {
        Pattern r = Pattern.compile("[\\\\d]+");
        Matcher m = r.matcher(str);
        return m.matches();
    }

    public static boolean checkDouble(String str) {
        Pattern r = Pattern.compile("[+-]?([0-9]*[.])?[0-9]+");
        Matcher m = r.matcher(str);
        return m.matches();
    }

    public static boolean checkString(String str) {
        Pattern r = Pattern.compile("[a-zA-Z]+");
        Matcher m = r.matcher(str);
        return m.matches();
    }
}

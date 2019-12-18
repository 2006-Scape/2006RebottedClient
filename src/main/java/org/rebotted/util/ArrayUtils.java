package org.rebotted.util;

public class ArrayUtils {
    public static boolean inArray(String string, String[] strings) {
        if (string == null)
            return false;
        for (String s : strings) {
            if (s != null && s.equalsIgnoreCase(string))
                return true;
        }
        return false;
    }

    public static boolean inArray(int i, int[] array) {
        for (int j : array) {
            if (j == i)
                return true;
        }
        return false;
    }
}

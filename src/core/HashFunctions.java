package core;

import java.util.LinkedList;
import java.util.List;

public class HashFunctions {

    public static String hash(int key) {
        String value = Integer.toBinaryString(key % 255);
        for (int i = value.length(); i < ExtendibleHashing.MAX_GLOBAL; i++) {
            value = "0" + value;
        }
        return value;
    }

    public static List<String> getGlobalArray(int global) {
        int n = (int) Math.pow(2, global);
        List<String> arr = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            String address = Integer.toBinaryString(i);
            for (int j = address.length(); j < global; j++) {
                address = "0" + address;
            }

            arr.add(address);
        }

        return arr;
    }

}

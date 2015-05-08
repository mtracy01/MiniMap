package map.minimap.frameworks.coreResources;

import android.util.Log;

public class IDCipher {

    public IDCipher() {}

    // makes a poor attempt to obscure the user ID
    public static String toCipher(String id) {
        StringBuilder build = new StringBuilder(21);
        char character;
        int newValue;
        for(int i = 0; i < id.length(); i++) {
            character = id.charAt(i);
            newValue = ((Character.getNumericValue(character) + i) + 4) % 10;
            build.append(newValue);
        }
        return build.toString();
    }

    // unobscures user ID
    public static String unCipher(String id) {
        StringBuilder build = new StringBuilder(21);
        char character;
        int rawValue;
        int newValue;

        for(int i = 0; i < id.length(); i++) {
            character = id.charAt(i);
            rawValue = Character.getNumericValue(character) - 4 - i;
            newValue = rawValue >= 0 ? (rawValue) % 10 : (rawValue + 50) % 10;
            build.append(newValue);
        }
        Log.v("Unencrypt: ", (build.toString()));
        return build.toString();
    }

    public static String unCipherGroups(String groups) {
        StringBuilder build = new StringBuilder();
        char character;
        int newValue;
        int rawValue;
        String [] groupArray = groups.split(":");
        int j = 0;
        int groupNameEnd;
        for(String group : groupArray) {
            groupNameEnd = group.indexOf(',') + 1;
            for (int i = 0; i < group.length(); i++) {
                if(i < groupNameEnd) {
                    build.append(group.charAt(i));
                } else if (group.charAt(i) == ',') {
                    j = 0;
                    build.append(',');
                } else {
                    character = group.charAt(i);
                    rawValue = Character.getNumericValue(character) - 4 - j;
                    newValue = rawValue >= 0 ? (rawValue) % 10 : (rawValue + 50) % 10;
                    build.append(newValue);
                    j++;
                }
            }
        }
        return build.toString();
    }
}
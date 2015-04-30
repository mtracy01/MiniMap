package map.minimap.frameworks.coreResources;

import java.lang.StringBuilder;

public class IDCipher {

    public IDCipher() {

    }

    // makes a poor attempt to obscure the user ID
    public static String toCipher(String id) {
        StringBuilder build = new StringBuilder(21);
        char character = 0;
        int newValue = 0;
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
        char character = 0;
        int newValue = 0;
        for(int i = 0; i < id.length(); i++) {
            character = id.charAt(i);
            newValue = ((Character.getNumericValue(character) - i) - 4) % 10;
            build.append(newValue);
        }
        return build.toString();
    }

    public static String unCipherGroups(String groups) {
        StringBuilder build = new StringBuilder();
        char character = 0;
        int newValue = 0;
        String [] groupArray = groups.split(":");
        int j = 0;
        int groupNameEnd = 0;
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
                    newValue = ((Character.getNumericValue(character) - j) - 4) % 10;
                    build.append(newValue);
                    j++;
                }
            }
        }
        return build.toString();
    }
}
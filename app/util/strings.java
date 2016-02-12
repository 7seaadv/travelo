package util;

public class strings {

    public static String format(String template, Object... replace) {
        template = template.replace("%s%s", "%s %s");
        String[] splits = template.split("%s");
        String result = "";
        int i = 0;
        while (i < splits.length && i < replace.length) {
            String split = splits[i];
            result = result + split + (replace[i] == null ? "NULL" : replace[i].toString());
            i++;
        }
        if (i < splits.length) {
            result = result + splits[i];
        }
        int index = result.indexOf("%t");
        if (index == -1) {
            return result;
        }
        splits = result.split("%t");
        String pendNumber = "";
        for (int j = 0; j < splits[1].toCharArray().length; j++) {
            Character cha = splits[1].toCharArray()[j];
            if (cha.isDigit(cha)) {
                pendNumber = pendNumber + cha;
            } else {
                break;
            }
        }
        int spaceNumber = Integer.parseInt(pendNumber);
        int originalLenght = splits[0].length();
        if (originalLenght < spaceNumber) {
            for (int j = 0; j < spaceNumber - originalLenght - 1; j++) {
                splits[0] = splits[0] + " ";
            }
        }
        return splits[0] + splits[1].substring(pendNumber.length(), splits[1].length());
    }

}

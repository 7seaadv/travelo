package util;

public interface HasDisplayName {
    public String getDisplayName();
    static class Utils {
        public static <T extends HasDisplayName> T getValueOf(String input, T[] values) {
            for (T activeUser : values) {
                if (activeUser.getDisplayName().equals(input))
                    return activeUser;
            }
            return values[0];
        }
    }
}

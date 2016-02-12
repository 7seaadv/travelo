package util;

public class WidHei {
    public final int width;
    public final int height;

    public WidHei(int width, int height, int limit) {
        if (width <= limit && height <= limit) {
            this.width = width;
            this.height = height;
        } else {
            double wScale = width / new Double(limit);
            double hScale = height / new Double(limit);
            double finalScale = wScale;
            if (wScale < hScale) {
                finalScale = hScale;
            }
            this.width = (int) (width / finalScale);
            this.height = (int) (height / finalScale);
        }
    }
}
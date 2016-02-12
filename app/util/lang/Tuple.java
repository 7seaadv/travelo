package util.lang;

public class Tuple {
    public static <T, R> T2 t(T ob1, R ob2) {
        return new T2<T, R>(ob1, ob2);
    }

    public static <T, R, S> T3 t(T ob1, R ob2, S ob3) {
        return new T3<T, R, S>(ob1, ob2, ob3);
    }

}

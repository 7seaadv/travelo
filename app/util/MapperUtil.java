package util;


import java.util.ArrayList;
import java.util.Collection;


public class MapperUtil {

    public static <T, R> ArrayList<R> map(Collection<T> list, Mapper<T, R> mapper) {
        ArrayList<R> result = new ArrayList<R>();
        if (list == null) {
            return result;
        }
        for (T target : list) {
            result.add(mapper.map(target));
        }
        return result;
    }

    public static <T, R> ArrayList<R> fillAndMap(Collection<T> list, Mapper<T, R> mapper, Filler<T> filler) {
        ArrayList<R> result = new ArrayList<R>();
        if (list == null) {
            return result;
        }
        for (T target : list) {
            if (filler.valid(target)) {
                result.add(mapper.map(target));
            }
        }
        return result;
    }


    public static <T, R> ArrayList<R> map(T[] list, Mapper<T, R> mapper) {
        ArrayList<R> result = new ArrayList<R>();
        if (list == null) {
            return result;
        }
        for (T target : list) {
            result.add(mapper.map(target));
        }
        return result;
    }

    public static Filler<Long> largerThen(final long input) {
        return new Filler<Long>() {
            public boolean valid(Long target) {
                return target > input;
            }
        };
    }

    public static Filler<Object> equal(final Object input) {
        return new Filler<Object>() {
            public boolean valid(Object target) {
                return target.equals(input);
            }
        };
    }


    public static Filler<Object> isClassFiller(final Class classz) {
        return new Filler<Object>() {
            public boolean valid(Object target) {
                return classz.toString().equals(target.getClass().toString());
            }
        };
    }


}
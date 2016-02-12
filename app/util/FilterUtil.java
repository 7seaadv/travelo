package util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterUtil {

	public static <T> ArrayList<T> retain(Collection<T> list, Filler<T> filler) {
        ArrayList result = new ArrayList();
        if (list == null) {
            return result;
        }
        for (T target : list) {
            if (filler.valid(target)) {
                result.add(target);
            }
        }
        return result;
    }

    public static <T> T retainOne(Collection<T> list, Filler<T> filler) {
        if (list == null) {
            return null;
        }
        for (T target : list) {
            if (filler.valid(target)) {
                return target;
            }
        }
        return null;
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


    public static <T> List<T> isClass(List<T> event, Class classz) {
        return new ArrayList(retain(event, (Filler<T>) isClassFiller(classz)));
    }

    public static Filler<Object> isClassFiller(final Class classz) {
        return new Filler<Object>() {
            public boolean valid(Object target) {
                return classz.toString().equals(target.getClass().toString());
            }
        };
    }


}
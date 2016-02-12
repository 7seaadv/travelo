package util;

public interface Mapper<T, R> {

    public R map(T input);

}
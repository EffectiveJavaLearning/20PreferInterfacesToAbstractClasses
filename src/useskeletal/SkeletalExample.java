package useskeletal;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * 一个使用骨架实现的例子
 *
 * @author LightDance
 */
public class SkeletalExample {

    public static List<Integer> intArrayAsList(int[] a){
        Objects.requireNonNull(a);
        //在Java 9和更高版本中，<>操作符才是合法的，因为Java9有了厉害一些的类型推断，
        //钻石操作符终于能够在匿名类中使用了。
        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return a[index];
            }

            @Override
            public int size() {
                return a.length;
            }

            @Override
            public Integer set(int index, Integer element) {
                int oldVal = a[index];
                //使用了自动装箱拆箱
                a[index] = element;
                return oldVal;
            }
        };
    }
}

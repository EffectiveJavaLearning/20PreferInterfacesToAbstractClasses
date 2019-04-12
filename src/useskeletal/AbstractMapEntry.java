package useskeletal;

import java.util.Map;
import java.util.Objects;

/**
 * {@link java.util.Map.Entry}的骨架实现，用于展示设计骨架实现的一般步骤
 * 这个类是抽象类，其原语方法留给使用者实现。
 *
 * 这里将接口中无法实现的几个Object类中的方法实现掉了，实现时应当自习参考接口中的文档说明。
 *
 * @author LightDance
 */
public abstract class AbstractMapEntry <K,V> implements Map.Entry<K,V> {

    @Override
    public V setValue(V value) {
        // Entries可修改的map中必须修改并实现这个方法
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey())
            ^ Objects.hashCode(getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?,?> e = (Map.Entry) obj;
        return Objects.equals(e.getKey(), getKey())
                && Objects.equals(e.getValue(), getValue());
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}

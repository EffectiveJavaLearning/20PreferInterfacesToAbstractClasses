package unsuitablehierarchies;


/**
 * 作曲家
 *
 * @author LightDance
 */
public interface Composer {
    /**模拟作曲行为*/
    void compose();

    @Override
    int hashCode();
}

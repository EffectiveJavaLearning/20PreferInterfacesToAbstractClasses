import useskeletal.SkeletalExample;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 这里解释为什么优先考虑使用接口而非抽象类。
 *
 * Java中为多个实现准备了两种定义方式：接口Interface和抽象类Abstract Class. 原本两种区别之一是，
 * Interface中不可以实现方法，但这一区别在Java8引入 default关键字之后也没有了。
 * 现在两者的主要区别在于，抽象类只允许单继承，而Interface则可以一个子类implements多个接口。
 *
 *
 * 在定义时，只要其中声明了需要子类实现的方法，并提供了相关的文档说明，不论其在类中的层次如何(顶层or嵌套)，
 * 那么它就是一个接口。
 *
 * 已有的类可以很容易地新增一个要实现的接口，只要在类声明的地方加一下implements XX接口，
 * 然后在类中实现接口中声明的方法(如果没有default方法的话)就好，比如已经编写好的数据类要实现Compareable
 * 接口，那么就把类的声明语句改成 class XX implements Compareable ，然后在类中实现compareTo()即可。
 * 但抽象类就不能随便新增，需要加一层继承关系才行，但这会造成其所有继承类无论适不适合都不得不子类化。
 *
 * 接口很适合定义mixin(混合类型)。mixin类型是一种 “主类型”之外，存放其他附加的、可选的功能的接口。
 * 比如{@link Comparable}，程序员可以选择是否让自己编写的类具有比较功能，于是Comparable就可称mixin类型，
 * 因为它使得可选功能能够与主要功能“混合”。而抽象类就不太合适，因为类的层次结构里面没有适合的位置加这么一层。
 *
 * 接口可以实现一些无视层次结构的功能。
 * 虽然层次结构有利于组织代码，但并不是任何地方都完全合适。比如{@link unsuitablehierarchies.Singer}
 * 和{@link unsuitablehierarchies.Composer}，一个人完全可以既是歌手又是作曲家，所以应该用接口定义二者。
 * 虽然我们完全可以再定义一级SingerComposer来表示二者兼备的人，虽然完全没必要，但如果非要这么干，
 * 那么接口就帮你避免了臃肿的类层次。否则的话，假设需要组合n个这样的父类，就需要定义2^n个新的组合出来，
 * 导致“组合爆炸”。
 *
 * 接口能够帮我们定义强大的包装类(wrapper-class,见item18)，而继承则会降低生成类的安全性。
 *
 * 当接口方法的实现方式非常显而易见时，可以考虑通过default方法来为想继承它的程序员减少负担。
 * 这在某种程度上也是对Java8之前“骨架实现”(即定义接口若干，通过abstract类实现它们，然后子类继承抽象类)
 * 的简洁版代替方案，比如Java8中新加入的{@link java.util.Collection#removeIf(Predicate)}
 * 不过如果要提供default方法，记得在@ImplSpec标签的说明中记录一下。
 *
 * 但这种默认方法的提供也有一点限制。首先不可以在接口中指定Object类中方法的default实现，比如equals这些。
 * 而且不能在接口中定义成员变量或者非public的static型成员(with the exception of private static
 * methods ???)。最后，不可以向无法控制的接口中添加默认方法。
 *
 * 然后正式介绍一下骨架实现(skeletal implementation)，它结合了二者的优势，接口定义类型，
 * 也可能提供一些默认的方法；实现这些or这个接口的抽象类则把剩下接口方法中需要实现的实现掉，
 * 把需要使用者自己编写的方法留下。实际上往往是编写接口的大部分时间都用来搞定这些骨架实现了。
 * 这也被称为“样板方法模式”(Template Method pattern)
 *
 * 一般骨架实现的命名方式为“AbstractInterface”，Interface代表它实现的接口名字，比如
 * {@link java.util.AbstractList},{@link java.util.AbstractMap}这些。没有用"SkeletalList"
 * 是因为大家都已经习惯前面一种命名方式，成了规范了。这种方式可以帮助我们非常容易地为设计的接口提供实现。
 * 比如这个适配器的例子{@link useskeletal.SkeletalExample#intArrayAsList(int[])}中，
 * 我们只需要实现几个比较重要的或者所需要的方法，其他可以一律使用AbstractList的默认实现。
 * 注意这里采用了匿名类的实现形式(见item24)
 *
 * 骨架实现的优雅之处在于，它能在为使用者提供辅助的同时，又不强加“抽象类被用作类型定义”时的严格限制。
 * 对于大多数接口，实现时继承骨架实现无疑要方便得多，但如果无法继承骨架实现，那么仍然可以手动实现接口，
 * 此时接口的default方法仍然有效。另外，即使无法直接继承骨架实现，也可以通过item18中的复合+转发方式，
 * 对其进行扩展。但这里把这种机制称为“模拟多重继承”，它既提供了多重继承的很多优势，又避免了c++
 * 等语言中的很多坏处。
 *
 * 编写骨架实现虽然繁琐，但并没有什么难度。
 * 首先要研究接口，确定接口中哪些方法是“原语”(primitives),就是那些会被其他方法调用的基础的方法，
 * 把这些方法留给使用者实现；
 * 然后给接口中基于原语实现的方法声明提供default方法，不过就像上面提过的，这一步骤无法为Object
 * 类自带的方法提供default实现。
 * 如果这一步骤结束时，接口中的方法声明全都是原语方法或者default方法，那么任务就已经完成了，
 * 没有必要再加一个骨架实现；否则要编写一个叫“Abstract[InterfaceName]”并implements需实现的接口的类，
 * 然后在其中将其余方法实现掉，这时候可以根据任务需求，适当添加非public型的方法或者成员变量。
 * 比如这个{@link java.util.Map.Entry}中，原语显而易见是{@link Map.Entry#getKey()},
 * {@link Map.Entry#getValue()}和(可选){@link java.util.Map.Entry#setValue(Object)}；
 * 虽然不能为hashCode()等方法设置default实现，却可以在文档中对其规范进行说明，然后在骨架实现中完成
 * {@link useskeletal.AbstractMapEntry},这个类是专门设计以供继承用的，虽为省事这里省略了文档注释，
 * 但真正设计时应有的文档说明和注释是必不可少的，应当满足所有与继承相关的说明与文档规范（见item19）。
 *
 * 骨架实现的一个常用变体是simple implementation(简单实现)，例如
 * {@link java.util.AbstractMap.SimpleEntry},同样为继承而设计，同样实现了一个接口，
 * 但不同之处是它并非Abstract型。不过它里面装的都是非常简单的方法。我们可以根据实际情况使用它，
 * 或者对其进行子类化。
 *
 * 最后总结一下，对于存在多个可能实现的情况下，应当优先考虑使用接口；如果接口比较复杂、要实现的方法很多，
 * 则应当提供一个骨架实现(skeletal implementation)以便使用，并尽可能地提供default方法和抽象类中实现；
 * 也就是说，尽量通过骨架实现(抽象类)的形式，提供某接口的规范和约束。
 *
 * @author LightDance
 */
public class PreferInterfacesToAbstractClasses {

    public static void main(String[] args) {
        int []a = {354,90,5,3,7};
        List<Integer> integerList = SkeletalExample.intArrayAsList(a);
        System.out.println(integerList.get(1));
    }
}

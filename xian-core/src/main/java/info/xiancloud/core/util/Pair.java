package info.xiancloud.core.util;

import java.util.Objects;


/**
 * 由于jdk tool.jar包不会直接被引入到运行时的classpath，因此这里
 * 从tool.jar包抄过来的,见com.sun.tools.javac.util.Pair
 *
 * @author created by happyyangyuan. copied from jdk1.8 tool.jar
 */
public class Pair<A, B> {
    public A fst;//not final any more.
    public B snd;//not final any more.

    public Pair(A var1, B var2) {
        this.fst = var1;
        this.snd = var2;
    }

    public String toString() {
        return "Pair[" + this.fst + "," + this.snd + "]";
    }

    public boolean equals(Object var1) {
        return var1 instanceof Pair && Objects.equals(this.fst, ((Pair) var1).fst) && Objects.equals(this.snd, ((Pair) var1).snd);
    }

    public int hashCode() {
        if (this.fst == null) {
            return this.snd == null ? 0 : this.snd.hashCode() + 1;
        } else {
            return this.snd == null ? this.fst.hashCode() + 2 : this.fst.hashCode() * 17 + this.snd.hashCode();
        }
    }

    public static <A, B> Pair<A, B> of(A var0, B var1) {
        return new Pair<>(var0, var1);
    }
}


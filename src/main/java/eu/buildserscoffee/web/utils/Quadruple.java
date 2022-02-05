package eu.buildserscoffee.web.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

@Getter
@Setter
public class Quadruple<T1, T2, T3, T4> {

    private T1 one;
    private T2 two;
    private T3 three;
    private T4 four;

    public Quadruple(T1 one, T2 two, T3 three, T4 four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public static <T1, T2, T3, T4>  Quadruple<T1, T2, T3, T4> of(T1 one, T2 two, T3 three, T4 four){
        return new Quadruple<>(one, two, three, four);
    }

    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new ImmutableTriple(left, middle, right);
    }
}

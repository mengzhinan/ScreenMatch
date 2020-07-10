package com.duke.screenmatch.utils;

import java.util.Objects;

/**
 * copy from android.util.Pair
 *
 * @param <F>
 * @param <S>
 */
public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair<?, ?> p = (Pair)o;
            return Objects.equals(p.first, this.first) && Objects.equals(p.second, this.second);
        }
    }

    public int hashCode() {
        return (this.first == null ? 0 : this.first.hashCode()) ^ (this.second == null ? 0 : this.second.hashCode());
    }

    public String toString() {
        return "Pair{" + String.valueOf(this.first) + " " + this.second + "}";
    }

    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair(a, b);
    }
}

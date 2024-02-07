package com.smartnote.server.util;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionalUtils {
    public static <T, U> U use(T t, Function<T, U> tu) {
        return tu.apply(t);
    }

    public static <T, U, V> V use(T t, U u, BiFunction<T, U, V> tuv) {
        return tuv.apply(t, u);
    }

    public static <T, U> U eval(Supplier<T> ts, Function<T, U> tu) {
        return tu.apply(ts.get());
    }

    public static <T, U> Supplier<U> thunk(Supplier<T> ts, Function<T, U> tu) {
        return () -> eval(ts, tu);
    }

    public static <T, U> Function<T, Supplier<U>> curry(Function<T, U> tu) {
        return t -> () -> tu.apply(t);
    }

    public static <T, U> Function<U, Supplier<T>> flip(Supplier<T> ts, BiFunction<T, U, T> ut) {
        return u -> thunk(ts, t -> ut.apply(t, u));
    }

    public static <T, U, V> Function<T, V> compose(Function<T, U> tu, Function<U, V> uv) {
        return t -> uv.apply(tu.apply(t));
    }

    public static <T> Supplier<T> memoize(Supplier<T> ts) {
        return use(new AtomicReference<Optional<T>>(), r -> () -> use(v -> v.or(thunk(ts, Optional::of)), r::updateAndGet).get());
    }
}

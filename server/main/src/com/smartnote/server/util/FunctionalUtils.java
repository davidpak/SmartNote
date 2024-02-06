package com.smartnote.server.util;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionalUtils {
    public static <T, U> U eval(Supplier<T> supplier, Function<T, U> tu) {
        return tu.apply(supplier.get());
    }

    public static <T, U> Supplier<U> thunk(Supplier<T> supplier, Function<T, U> tu) {
        return () -> eval(supplier, tu);
    }

    public static <T, U> Function<T, Supplier<U>> curry(Function<T, U> tu) {
        return t -> () -> tu.apply(t);
    }

    public static <T, U> Function<U, Supplier<T>> flip(Supplier<T> supplier, BiFunction<T, U, T> ut) {
        return u -> thunk(supplier, t -> ut.apply(t, u));
    }

    public static <T, U, V> Function<T, V> compose(Function<T, U> tu, Function<U, V> uv) {
        return t -> uv.apply(tu.apply(t));
    }

    public static <T> Supplier<T> once(Supplier<T> supplier) {
        return new Promise<>(supplier);
    }

    private static class Promise<T> implements Supplier<T> {
        private Supplier<T> supplier;
        private Optional<T> result;

        Promise(Supplier<T> supplier) {
            this.supplier = supplier;
            this.result = Optional.empty();
        }

        @Override
        public T get() {
            return result.orElseGet(supplier);
        }
    }
}

package com.smartnote.server.util;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * <p>
 * Contains utility functions for functional programming.
 * </p>
 * 
 * @author Ethan Vrhel
 */
public class FunctionalUtils {

    /**
     * Evaluates a function. Equivalent to <code>tu.apply(t)</code>.
     * 
     * @param <T> The type of the value to evaluate
     *            <code>tu</code> with
     * @param <U> The type of the result
     * @param t   The value to evaluate <code>tu</code> with
     * @param tu  The function to evaluate
     * @return The result of evaluating <code>tu</code> with
     *         <code>t</code>
     */
    public static <T, U> U now(T t, Function<T, U> tu) {
        return tu.apply(t);
    }

    /**
     * Evaluates a function. Equivalent to
     * <code>tuv.apply(t, u)</code>.
     * 
     * @param <T> The type of the first value to evaluate
     *            <code>tuv</code> with
     * @param <U> The type of the second value to evaluate
     *            <code>tuv</code> with
     * @param <V> The type of the result
     * @param t   The first value to evaluate <code>tuv</code> with
     * @param u   The second value to evaluate <code>tuv</code> with
     * @param tuv The function to evaluate
     * @return The result of evaluating <code>tuv</code> with
     *         <code>t</code> and <code>u</code>
     */
    public static <T, U, V> V now(T t, U u, BiFunction<T, U, V> tuv) {
        return tuv.apply(t, u);
    }

    /**
     * Delays the evaluation of a function.
     * 
     * @param <T> The type of the value to evaluate <code>tu</code>
     *            with
     * @param <U> The type of the result
     * @param t   The value to evaluate <code>tu</code> with
     * @param tu  The function to evaluate
     * @return A supplier that will evaluate <code>tu</code> with
     *         <code>t</code>
     */
    public static <T, U> Supplier<U> later(T t, Function<T, U> tu) {
        return () -> tu.apply(t);
    }

    /**
     * Delays the evaluation of a function.
     * 
     * @param <T> The type of the first value to evaluate
     *            <code>tuv</code> with
     * @param <U> The type of the second value to evaluate
     *            <code>tuv</code> with
     * @param <V> The type of the result
     * @param t   The first value to evaluate <code>tuv</code> with
     * @param u   The second value to evaluate <code>tuv</code> with
     * @param tuv The function to evaluate
     * @return A supplier that will evaluate <code>tuv</code> with
     *         <code>t</code> and <code>u</code>.
     */
    public static <T, U, V> Supplier<V> later(T t, U u, BiFunction<T, U, V> tuv) {
        return () -> tuv.apply(t, u);
    }

    /**
     * Evaluates a supplier then applies it to a function.
     * 
     * @param <T> The type of value produced by the supplier
     * @param <U> The type of the result
     * @param ts  A supplier whose return value is used to evaluate
     *            <code>tu</code>
     * @param tu  The function to evaluate
     * @return The result of evaluating <code>tu</code> with the
     *         value value supplied by <code>ts</code>
     */
    public static <T, U> U eval(Supplier<T> ts, Function<T, U> tu) {
        return tu.apply(ts.get());
    }

    public static <T, U> Supplier<U> thunk(Supplier<T> ts, Function<T, U> tu) {
        return () -> tu.apply(ts.get());
    }

    public static <T, U, V> Function<T, V> partial(U u, BiFunction<T, U, V> tuv) {
        return t -> tuv.apply(t, u);
    }

    /**
     * Curries a function, separating it into a series of functions
     * each taking one argument.
     * 
     * @param <T> The type of the first argument
     * @param <U> The type of the second argument
     * @param <V> The type of the result
     * @param tuv The function to curry
     * @return The curried function
     */
    public static <T, U, V> Function<T, Function<U, V>> curry(BiFunction<T, U, V> tuv) {
        return t -> u -> tuv.apply(t, u);
    }

    public static <T, U, V> Supplier<V> defer(Supplier<T> ts, U u, BiFunction<T, U, V> tuv) {
        return thunk(ts, partial(u, tuv));
    }

    public static <T, U, V> Function<U, Supplier<V>> defer(Supplier<T> ts, BiFunction<T, U, V> tuv) {
        return u -> thunk(ts, partial(u, tuv));
    }

    /**
     * Composes two functions, creating a new function that
     * applies the second function to the result of the first.
     * 
     * @param <T> The type of the first argument
     * @param <U> The type of the second argument and whose
     *            result will be the first argument of
     *            <code>uv</code>
     * @param <V> The type of the result
     * @param tu  The first function
     * @param uv  The second function
     * @return The composed function
     */
    public static <T, U, V> Function<T, V> compose(Function<T, U> tu, Function<U, V> uv) {
        return t -> uv.apply(tu.apply(t));
    }

    public static <T> UnaryOperator<T> unary(UnaryOperator<T> t) {
        return t;
    }

    /**
     * Memoizes a supplier, meaning it will only be evaluated once.
     * The returned value will evaluate <code>ts</code> once and
     * store its result for any future evaluations.
     * 
     * @param <T> The type produced by the supplier
     * @param ts  The supplier to memoize
     * @return A memoized supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> ts) {
        AtomicReference<Optional<T>> value = new AtomicReference<>();
        UnaryOperator<Optional<T>> updater = v -> v.or(() -> Optional.of(ts.get()));
        return () -> value.updateAndGet(updater).get();
    }
}

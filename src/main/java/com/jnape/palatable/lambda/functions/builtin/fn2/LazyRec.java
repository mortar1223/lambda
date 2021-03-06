package com.jnape.palatable.lambda.functions.builtin.fn2;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functor.builtin.Lazy;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.jnape.palatable.lambda.functor.builtin.Lazy.lazy;
import static com.jnape.palatable.lambda.monad.Monad.join;

/**
 * Given a {@link BiFunction} that receives a recursive function and an input and yields a {@link Lazy lazy} result, and
 * an input, produce a {@link Lazy lazy} result that, when forced, will recursively invoke the function until it
 * terminates in a stack-safe way.
 * <p>
 * Example:
 * <pre>
 * <code>
 * Lazy<BigInteger> lazyFactorial = lazyRec((fact, x) -> x.equals(ONE)
 *                                                               ? lazy(x)
 *                                                               : fact.apply(x.subtract(ONE)).fmap(y -> y.multiply(x)),
 *                                                  BigInteger.valueOf(50_000));
 * BigInteger value = lazyFactorial.value(); // 3.34732050959714483691547609407148647791277322381045 × 10^213236
 * </code>
 * </pre>
 *
 * @param <A> the input type
 * @param <B> the output type
 */
public final class LazyRec<A, B> implements
        Fn2<BiFunction<Function<? super A, ? extends Lazy<B>>, A, Lazy<B>>, A, Lazy<B>> {

    private static final LazyRec<?, ?> INSTANCE = new LazyRec<>();

    private LazyRec() {
    }

    @Override
    public Lazy<B> apply(BiFunction<Function<? super A, ? extends Lazy<B>>, A, Lazy<B>> fn, A a) {
        return join(lazy(() -> fn.apply(nextA -> apply(fn, nextA), a)));
    }

    @SuppressWarnings("unchecked")
    public static <A, B> LazyRec<A, B> lazyRec() {
        return (LazyRec<A, B>) INSTANCE;
    }

    public static <A, B> Fn1<A, Lazy<B>> lazyRec(BiFunction<Function<? super A, ? extends Lazy<B>>, A, Lazy<B>> fn) {
        return LazyRec.<A, B>lazyRec().apply(fn);
    }

    public static <A, B> Lazy<B> lazyRec(BiFunction<Function<? super A, ? extends Lazy<B>>, A, Lazy<B>> fn, A a) {
        return lazyRec(fn).apply(a);
    }
}

package coldsrc.coldlib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Pipeline for creating and initializing
 * an instance of type {@code R} with options
 * of type {@code O}.
 *
 * @param <I> The intermediate type.
 * @param <T> The result type (finalized).
 * @param <O> The options type.
 */
public class Assembly<I, T, O> {

    // semi-full constructor
    // called by the builder
    @SuppressWarnings("unchecked")
    Assembly(
            final Class<I> intermediateType,
            final Class<T> resultType,
            final Class<O> optionsType,

            Supplier<O> defaultOptionProvider,
            InstanceFactory<I, O> instanceFactory,
            Line<I, O> intermediateLine,
            Function<I, T> finalizer,
            Line<T, O> resultLine
    ) {
        this.intermediateType = intermediateType;
        this.resultType = resultType;
        this.optionsType = optionsType;

        Objects.requireNonNull(instanceFactory, "Intermediate instance factory can not be null");

        this.defaultOptionProvider = defaultOptionProvider;
        this.instanceFactory = instanceFactory;
        this.intermediateLine = intermediateLine;
        this.resultLine = resultLine;
        this.finalizer = Objects.requireNonNullElse(finalizer, i -> (T) i);
    }

    // runtime types
    final Class<I> intermediateType;
    final Class<T> resultType;
    final Class<O> optionsType;

    /**
     * The function which provides the default
     * options object. This can be null or return
     * null, in that case null will be used as
     * the default options.
     */
    protected final Supplier<O> defaultOptionProvider;

    /**
     * The instance factory responsible for
     * creating the first instance.
     */
    protected final InstanceFactory<I, O> instanceFactory;

    /**
     * The pipeline to transform the intermediate
     * instance.
     */
    protected final Line<I, O> intermediateLine;

    /**
     * The finalizer function.
     */
    protected final Function<I, T> finalizer;

    /**
     * The pipeline to transform the
     * result instance.
     */
    protected final Line<T, O> resultLine;

    /**
     * Get the default options if defined.
     *
     * @return The default options or null (also valid).
     */
    protected O getDefaultOptions() {
        return defaultOptionProvider == null ? null : defaultOptionProvider.get();
    }

    /**
     * Assembles (creates, initializes, prepares) a new
     * instance of type {@code T} with the default options.
     *
     * @see Assembly#build(Object)
     * @return The built instance.
     */
    public T build() {
        return build(getDefaultOptions());
    }

    /**
     * Assembles (creates, initializes, prepares) a new
     * instance of type {@code T} with the provided
     * options (nullable).
     *
     * @param options The options to assemble with. Can be null.
     * @return The built instance.
     */
    public T build(O options) {
        // call instance factory for intermediate
        I intermediate = instanceFactory.create(options);

        // transform intermediate instance
        if (intermediateLine != null)
            intermediate = intermediateLine.push(intermediate, options);

        // finalize intermediate into result
        T result = finalizer.apply(intermediate);

        // transform finalized instance
        if (resultLine != null)
            result = resultLine.push(result, options);

        // return result
        return result;
    }

    /**
     * Creates a new builder with the
     * properties of this instance set.
     *
     * @return The builder instance.
     */
    public Builder<I, T, O> toBuilder() {
        // create builder
        Builder<I, T, O> builder = builder(
                intermediateType,
                resultType,
                optionsType
        );

        // set properties
        builder.defaultOptionsProvider = defaultOptionProvider;
        builder.instanceFactory = instanceFactory;
        builder.intermediateTransformers = new ArrayList<>(intermediateLine.transformers);
        builder.finalizer = finalizer;
        builder.resultTransformers = new ArrayList<>(resultLine.transformers);

        // return builder
        return builder;
    }

    /* Getters */

    public Class<I> getIntermediateType() {
        return intermediateType;
    }

    public Class<T> getResultType() {
        return resultType;
    }

    public Class<O> getOptionsType() {
        return optionsType;
    }

    /////////////////////////////////////////////////////

    /**
     * Create a new builder instance.
     *
     * @param iClass The intermediate type.
     * @param tClass The result type.
     * @param oClass The options type.
     * @param <I> The intermediate type.
     * @param <T> The result type.
     * @param <O> The options type.
     * @return The builder instance.
     */
    public static <I, T, O> Builder<I, T, O> builder(Class<I> iClass,
                                                     Class<T> tClass,
                                                     Class<O> oClass) {
        Objects.requireNonNull(iClass);
        Objects.requireNonNull(tClass);
        return new Builder<>(
                iClass,
                tClass,
                oClass
        );
    }

    /**
     * Builder for an assembly.
     *
     * @param <I> The intermediate instance type.
     * @param <T> The result instance type.
     * @param <O> The options type.
     */
    public static class Builder<I, T, O> {

        @SuppressWarnings("unchecked")
        Builder(
                final Class<I> intermediateType,
                final Class<T> resultType,
                Class<O> optionsType
        ) {
            Objects.requireNonNull(intermediateType, "Intermediate type can not be null");
            Objects.requireNonNull(resultType, "Result type can not be null");
            if (optionsType == null)
                optionsType = (Class<O>) Object.class;

            this.intermediateType = intermediateType;
            this.resultType = resultType;
            this.optionsType = optionsType;
        }

        // types
        final Class<I> intermediateType;
        final Class<T> resultType;
        final Class<O> optionsType;

        // properties
        Supplier<O> defaultOptionsProvider;
        Function<I, T> finalizer;
        InstanceFactory<I, O> instanceFactory;
        List<Transformer<I, O>> intermediateTransformers = new ArrayList<>();
        List<Transformer<T, O>> resultTransformers = new ArrayList<>();

        /**
         * Builds a new assembly instance with the
         * specified properties.
         *
         * @return The assembly instance.
         */
        public Assembly<I, T, O> build() {
            return new Assembly<>(
                    intermediateType,
                    resultType,
                    optionsType,

                    defaultOptionsProvider,
                    instanceFactory,
                    new Line<>(intermediateTransformers),
                    finalizer,
                    new Line<>(resultTransformers)
            );
        }

        private <V> Transformer<V, O> makeTransformer(BiConsumer<V, O> consumer) {
            return (in, options) -> {
                consumer.accept(in, options);
                return in;
            };
        }

        /* Getters and Setters */

        public Supplier<O> getDefaultOptionsProvider() {
            return defaultOptionsProvider;
        }

        public Builder<I, T, O> setDefaultOptionsProvider(Supplier<O> defaultOptionsProvider) {
            this.defaultOptionsProvider = defaultOptionsProvider;
            return this;
        }

        public Builder<I, T, O> setDefaultOptions(final O options) {
            return setDefaultOptionsProvider(() -> options);
        }

        public Function<I, T> getFinalizer() {
            return finalizer;
        }

        public Builder<I, T, O> setFinalizer(Function<I, T> finalizer) {
            this.finalizer = finalizer;
            return this;
        }

        public InstanceFactory<I, O> getInstanceFactory() {
            return instanceFactory;
        }

        public Builder<I, T, O> setInstanceFactory(InstanceFactory<I, O> instanceFactory) {
            this.instanceFactory = instanceFactory;
            return this;
        }

        public List<Transformer<I, O>> getIntermediateTransformers() {
            return intermediateTransformers;
        }

        public Builder<I, T, O> setIntermediateTransformers(List<Transformer<I, O>> intermediateTransformers) {
            this.intermediateTransformers = intermediateTransformers;
            return this;
        }

        public Builder<I, T, O> addIntermediateTransformer(Transformer<I, O> transformer) {
            intermediateTransformers.add(transformer);
            return this;
        }

        public Builder<I, T, O> addIntermediateConsumer(BiConsumer<I, O> transformer) {
            return addIntermediateTransformer(makeTransformer(transformer));
        }

        public List<Transformer<T, O>> getResultTransformers() {
            return resultTransformers;
        }

        public Builder<I, T, O> setResultTransformers(List<Transformer<T, O>> resultTransformers) {
            this.resultTransformers = resultTransformers;
            return this;
        }

        public Builder<I, T, O> addResultTransformer(Transformer<T, O> transformer) {
            resultTransformers.add(transformer);
            return this;
        }

        public Builder<I, T, O> addResultConsumer(BiConsumer<T, O> transformer) {
            return addResultTransformer(makeTransformer(transformer));
        }

    }

    /////////////////////////////////////////////////////

    /**
     * One transformer/handler in a line.
     * Responsible for transforming the instance
     * and potentially returning a new one.
     *
     * @param <T> The instance type.
     * @param <O> The options type.
     */
    public interface Transformer<T, O> {

        /**
         * Transforms the given instance, may either
         * return the instance back or an entirely
         * new instance.
         *
         * @param in The input instance.
         * @param options The options.
         * @return The output instance, will be the same as {@code in} in most cases.
         */
        T transform(T in, O options);

    }

    /**
     * A pipeline or assembly line which will
     * transform the instance of type {@code T}
     * by passing it through a line of {@link Transformer}s.
     *
     * @param <T> The instance type.
     * @param <O> The options type.
     */
    public static class Line<T, O> {

        /**
         * Create a new assembly line.
         *
         * @param transformers The transformers.
         */
        public Line(List<Transformer<T, O>> transformers) {
            if (transformers == null)
                transformers = new ArrayList<>(0);
            this.transformers = transformers;
        }

        /**
         * The transformers in the assembly line.
         */
        protected List<Transformer<T, O>> transformers;

        /**
         * Pushes the given instance through the
         * line transforming it and giving an
         * output instance.
         *
         * @param in The input instance.
         * @param options The input options.
         * @return The output instance.
         */
        public T push(T in, O options) {
            // pass through all transformers
            T current = in;
            final int l = transformers.size();
            for (int i = 0; i < l; i++) {
                Transformer<T, O> transformer = transformers.get(i);
                current = transformer.transform(current, options);
            }

            // return result
            return current;
        }

    }

    /**
     * Responsible for creating an instance,
     * taking in account the provided options.
     *
     * @param <T> The instance type.
     * @param <O> The options type.
     */
    @FunctionalInterface
    public interface InstanceFactory<T, O> {

        /**
         * Creates an instance of type {@code T}
         * with the given options.
         *
         * @throws IllegalArgumentException If the options are invalid.
         * @param options The options to use.
         * @return The new instance.
         */
        T create(O options);

    }

}

package net.orbyfied.coldlib.util;

/**
 * Interface for stream lining self returning
 * with inherited classes, without complex generics.
 *
 * @param <T> The instance type of {@code this}.
 */
public interface Self<T> {

    /**
     * Get {@code this} casted to type {@code T}.
     * Will throw a {@link ClassCastException} if
     * this is not an instance of type {@code T}.
     *
     * @throws ClassCastException If this is not an instance of type {@code T}.
     * @return The instance.
     */
    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }

}

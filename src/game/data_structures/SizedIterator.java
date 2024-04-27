package game.data_structures;

/**
 * This interface represents an iterator over a collection of elements of type E with a set size
 * @param <E> the type of elements in the collection
 */
public interface SizedIterator<E> extends Iterator<E> {
    /**
     * @return Size of the underlying collection
     */
    int size();
}

import java.util.Comparator;

public interface CompareEdge<E> extends Comparator<E> {
    int compareSrc(E o1, E o2);

    int compareConnections(E o1, E o2);

}


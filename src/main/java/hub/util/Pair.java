package hub.util;

import java.io.Serializable;

public class Pair<F, S> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6456868757843307113L;

    public F object1 = null;

    public S object2 = null;

    public Pair(F object1, S object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    public static <F, S> Pair<F, S> create(F f, S s) {
        return new Pair<F, S>(f, s);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((object1 == null) ? 0 : object1.hashCode());
        result = PRIME * result + ((object2 == null) ? 0 : object2.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (object1 == null) {
            if (other.object1 != null) return false;
        } else if (!object1.equals(other.object1)) return false;
        if (object2 == null) {
            if (other.object2 != null) return false;
        } else if (!object2.equals(other.object2)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", object1.toString(), object2.toString());
    }

    public F getFirst() {
        return object1;
    }

    public S getSecond() {
        return object2;
    }

}
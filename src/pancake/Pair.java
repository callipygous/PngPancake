package pancake;

/**
 * Created with IntelliJ IDEA.
 * User: Owner
 * Date: 1/8/14
 * Time: 12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pair<T1, T2> {

    public final T1 _1;
    public final T2 _2;

    public Pair(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @SuppressWarnings("unchecked")
    public boolean equals( final Object otherObj ) {
        if( otherObj == null || otherObj.getClass() != Pair.class ) {
            return false;
        }

        final Pair other = (Pair) otherObj;
        return other._1.equals( this._1 ) && other._2.equals( this._2 );
    }

    public int hashCode() {
        return (31 * this._1.hashCode()) + (31 * this._2.hashCode());
    }

    public String toString() {
        return "Pair( " + _1.toString() + ", " + _2.toString() + " )";
    }
}

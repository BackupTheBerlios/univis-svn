package unikn.dbis.univis.pivot.data;

/**
 * Dynamic String Array
 *
 * @author Christian Gruen
 */
public class Strings {
    private int cap;

    private String[] strings;

    // number of entries
    public int size;

    /**
     * empty constructor
     */
    public Strings() {
        this(10);
    }

    /**
     * constructor, specifing initial capacity
     *
     * @param capacity initial capacity
     */
    public Strings(int capacity) {
        strings = new String[capacity];
        cap = capacity;
    }

    public void add(String value) {
        if (size == cap) {
            cap <<= 1;
            String[] str = new String[cap];
            System.arraycopy(strings, 0, str, 0, size);
            strings = str;
        }
        strings[size++] = value;
    }

    /**
     * return specified node
     */
    public String get(int i) {
        return strings[i];
    }
}

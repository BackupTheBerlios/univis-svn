package unikn.dbis.univis.pivot.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Marion Herb
 *         <p/>
 *         Some utils
 */
public class UniUtil {

    /**
     * show String[] content as List
     *
     * @param array
     * @return Returns String representation of array
     */
    public static String getStringRepOfArray(String[] array) {
        if (array == null) {
            return null;
        }
        return Arrays.asList(array).toString();
    }

    /**
     * show String[][] content as List
     *
     * @param array
     * @return Returns String representation of array
     */
    public static String getStringRepOfArrayArray(String[][] array) {
        if (array == null) {
            return null;
        }

        List newList = new ArrayList();
        List list = Arrays.asList(array);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String[] element = (String[]) iter.next();
            newList.add(getStringRepOfArray(element));
        }
        return newList.toString();
    }

}

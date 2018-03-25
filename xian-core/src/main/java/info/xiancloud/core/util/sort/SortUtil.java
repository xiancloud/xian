package info.xiancloud.core.util.sort;

import java.util.*;

/**
 * @author happyyangyuan
 */
public class SortUtil {
    public static void main(String[] args) {

        Map<String, Integer> unsortMap = new HashMap<>();
        unsortMap.put("z", 10);
        unsortMap.put("b", 6);
        unsortMap.put("a", 6);
        unsortMap.put("c", 20);
        unsortMap.put("d", 1);
        unsortMap.put("e", 7);
        unsortMap.put("y", 8);
        unsortMap.put("n", 99);
        unsortMap.put("j", 50);
        unsortMap.put("m", 2);
        unsortMap.put("f", 9);

        System.out.println("Unsort Map......");
        printMap(unsortMap);

        System.out.println("\nSorted Map......");
        Map<String, Integer> sortedMap = orderByValue(unsortMap);
        printMap(sortedMap);

    }

    /**
     * 根据map的value进行排序
     */
    public static Map orderByValue(Map unsortMap, Comparator... comparators) {
        // Convert Map to List
        List<Map.Entry> list = new LinkedList<>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry>() {
            public int compare(Map.Entry o1, Map.Entry o2) {
                if (comparators.length == 0) {
                    if (o1.getValue() instanceof Comparable/* && o2.getValue() instanceof Comparable*/) {
                        Comparable ov1 = (Comparable) o1.getValue()/*, ov2 = (Comparable)o2.getValue()*/;
                        return ov1.compareTo(o2.getValue());
                    } else {
                        throw new IllegalArgumentException(o1.getValue().getClass() + " 必须是comparable,否则无法进行比较排序");
                    }
                } else {
                    return comparators[0].compare(o1.getValue(), o2.getValue());
                }
            }
        });

        // Convert sorted map back to a Map
        Map sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @SuppressWarnings("unchecked")
    public static void printMap(Map map) {
        map.entrySet().forEach(e -> {
            Map.Entry entry = (Map.Entry) e;
            System.out.println("[Key] : " + entry.getKey() + " [Value] : " + entry.getValue());
        });
    }
}

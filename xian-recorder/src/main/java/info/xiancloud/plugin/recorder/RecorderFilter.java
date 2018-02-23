package info.xiancloud.plugin.recorder;

import info.xiancloud.plugin.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * filter for the recorder.
 * Not fully tested.
 */
public class RecorderFilter {

    public static final List<String> filterList = new ArrayList<String>() {{
        add("recorder");
    }};

    /**
     * 过滤业务是否应该被监控
     */
    public static boolean filter(Unit unit) {
        String groupName = unit.getGroup().getName();
        for (String str : filterList) {
            return groupName.contains(str);
        }
        return false;
    }

}

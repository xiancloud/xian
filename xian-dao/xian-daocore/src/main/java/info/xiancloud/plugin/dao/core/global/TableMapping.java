package info.xiancloud.plugin.dao.core.global;

import java.util.HashMap;
import java.util.Map;

/**
 * TableMapping save the mapping between model class and table.
 */
public class TableMapping {

	private final Map<String, Table> tableMap = new HashMap<String, Table>();

	private static TableMapping me = new TableMapping();

	private TableMapping() {
	}

	public static TableMapping me() {
		return me;
	}

	public void putTable(String name, Table table) {
		tableMap.put(name, table);
	}

	public Table getTable(String name) {
		return tableMap.get(name);
	}

	public Map<String, Table> getMappings() {
		return tableMap;
	}
}

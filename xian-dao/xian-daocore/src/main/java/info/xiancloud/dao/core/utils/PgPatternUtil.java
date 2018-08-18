package info.xiancloud.dao.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * postgresql pattern util
 *
 * @author happyyangyuan
 */
public class PgPatternUtil {

    private static final String PATTERN_STRING = "\\{[^}]*}";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

    /**
     * Get pg prepared sql.
     * Postgresql prepared sql eg.
     * <p>
     * <code>
     * INSERT INTO USERS (id, name) VALUES ($1, $2)
     * </code>
     * </p>
     *
     * @param patternSql xian pattern sql
     * @return pg prepared sql
     */
    public static String getPreparedSql(String patternSql) {
        Matcher matcher = PATTERN.matcher(patternSql);
        StringBuilder preparedSql = new StringBuilder();
        int i = 0;
        int start = 0;
        while (matcher.find()) {
            i++;
            preparedSql.append(patternSql.substring(start, matcher.start())).append(" $").append(i).append(" ");
            start = matcher.end();
        }
        return preparedSql.toString();
    }

}

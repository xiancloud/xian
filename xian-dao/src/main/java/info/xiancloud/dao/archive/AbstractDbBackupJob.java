package info.xiancloud.dao.archive;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.SyncXian;
import info.xiancloud.core.support.cache.api.CacheObjectUtil;
import info.xiancloud.core.support.quartz.AbstractJobBean;
import info.xiancloud.core.support.quartz.SchedulingMetaInfo;
import info.xiancloud.core.support.quartz.SchedulingMetaInfoBuilder;
import info.xiancloud.core.util.StringUtil;
import org.quartz.JobExecutionContext;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 数据库归档父类模板，本类适用于定义了时间字段的大表的归档
 *
 * @author happyyangyuan
 */
public abstract class AbstractDbBackupJob extends AbstractJobBean {

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) {
        if (StringUtil.isEmpty(lockName())) {
            invokeDbBackupUnit();
        } else
            try {
                CacheObjectUtil.set(lockName(), "locked", 60 * 60);
                invokeDbBackupUnit();
            } finally {
                CacheObjectUtil.remove(lockName());
            }
    }

    private void invokeDbBackupUnit() {
        SyncXian.call(dbBackupUnit(), new JSONObject() {{
            Calendar yesterday = new GregorianCalendar();
            yesterday.add(Calendar.DATE, 0 - daysAgo());
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);
            put("backupDateTime", yesterday);
        }}, Long.MAX_VALUE);
    }

    /**
     * @return 指定归档几天前的数据
     */
    protected abstract int daysAgo();

    /**
     * @return 定义归档sql语句归档的db unit
     */
    protected abstract Class<? extends Unit> dbBackupUnit();

    @Override
    public SchedulingMetaInfo[] getMetaInfos() {
        return new SchedulingMetaInfo[]{SchedulingMetaInfoBuilder.newCronSchedulingMetaInfo(this.getClass(),
                "Cron_" + this.getClass().getSimpleName(), cronExpression())
        };
    }

    /**
     * 默认每晚五点；子类覆盖以实现自定义时间
     */
    protected String cronExpression() {
        return "0 0 05 * * ?";
    }

    /**
     * lock name for the business side to make a label of the current backup;
     * defaults to null which means no label is used.
     */
    protected String lockName() {
        return null;
    }

}

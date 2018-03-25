package info.xiancloud.core.support.quartz;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz任务会吞掉非JobExecutionException异常，所有自己记录异常日志。
 * 2016-07-08 by yy: 每个定时任务,都使用自有统一msgId,该msgId在graylog可查!
 *
 * @author explorerlong
 */
public abstract class AbstractJobBean implements JobBean {

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        try {
            MsgIdHolder.init();
            doExecute(context);
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            SchedulingMetaInfo[] metas = this.getMetaInfos();
            for (SchedulingMetaInfo meta : metas) {
                msg.append(String.format("定时任务执行失败,任务标识(jobName,jobGroup,triggerGroup):%s,%s,%s",
                        meta.getName(), meta.getJobGroup(), meta.getTriggerGroup())
                        + "\n");
            }
            LOG.error(msg.toString(), e);
        } finally {
            MsgIdHolder.clear();
        }

    }

    abstract protected void doExecute(JobExecutionContext context);
}

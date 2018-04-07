package info.xiancloud.core.support.quartz;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.thread.MsgIdHolder;
import org.quartz.JobExecutionContext;

/**
 * Quartz任务会吞掉非JobExecutionException异常，所以自己记录异常日志。
 * 为了使用quartz的阻塞模式，这里只能将异步阻塞为同步。
 *
 * @author explorerlong, happyyangyuan @2016-07-08: 每个定时任务,都使用自有统一msgId,该msgId在graylog可查!
 */
public abstract class AbstractJobBean implements JobBean {

    @Override
    public void execute(JobExecutionContext context) {
        try {
            MsgIdHolder.init();
            blockingExecute(context);
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            SchedulingMetaInfo[] metas = this.getMetaInfos();
            for (SchedulingMetaInfo meta : metas) {
                msg.append(String.format("定时任务执行失败,任务标识(jobName,jobGroup,triggerGroup):%s,%s,%s",
                        meta.getName(), meta.getJobGroup(), meta.getTriggerGroup())).append("\n");
            }
            LOG.error(msg.toString(), e);
        } finally {
            MsgIdHolder.clear();
        }

    }

    /**
     * blocking execution
     *
     * @param context the job execution context
     */
    abstract protected void blockingExecute(JobExecutionContext context);
}

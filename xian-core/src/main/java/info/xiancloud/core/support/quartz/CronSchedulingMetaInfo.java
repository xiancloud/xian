package info.xiancloud.core.support.quartz;

/**
 * Cron 任务元数据描述信息。
 * 
 * @author explorerlong
 *
 */
public interface CronSchedulingMetaInfo extends SchedulingMetaInfo{
	public String getCronExpression();
}

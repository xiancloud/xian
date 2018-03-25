package info.xiancloud.core.support.quartz;

/**
 * 任务描述元数据。
 * 
 * @author explorerlong
 *
 */
public interface SchedulingMetaInfo {
	public String getName();
	public Class<? extends JobBean> getJobClass();
	public String getJobGroup();
	public String getTriggerGroup();
}

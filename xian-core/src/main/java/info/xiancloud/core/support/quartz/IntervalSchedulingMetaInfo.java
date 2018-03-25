package info.xiancloud.core.support.quartz;

/**
 * Interval 任务元数据描述信息。
 * 
 * @author explorerlong
 *
 */
public interface IntervalSchedulingMetaInfo extends SchedulingMetaInfo{
	/**
	 * job第一次执行不计算在内
	 * @return
	 */
	public int getRepeatCount();
	public long getInterval();
	public long getDelay();
}

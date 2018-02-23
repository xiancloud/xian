package info.xiancloud.plugin.quartz;

/**
 * 任务描述元数据Builder.
 * 
 * @author explorerlong
 *
 */
public class SchedulingMetaInfoBuilder {
	private static final String DEFAULT_TRIGGER_GROUP = "trigger1";
	public static final String DEFAULT_JOB_GROUP = "pay_quartz_job_group";
	
	
	public static <T extends JobBean> SchedulingMetaInfo newCronSchedulingMetaInfo(Class<T> jobClass, 
					String jobName, String cronExpression) {
		
		return newCronSchedulingMetaInfo(jobClass, jobName, DEFAULT_JOB_GROUP, DEFAULT_TRIGGER_GROUP, cronExpression);
		
	}
	public static <T extends JobBean> SchedulingMetaInfo newCronSchedulingMetaInfo(Class<T> jobClass, 
			String jobName, String jobGroup, String triggerGroup,String cronExpression) {

		
		return createCronMetaInfo(jobClass, jobName, jobGroup, triggerGroup, cronExpression);
	
	}
	/**
	 * 默认延迟40s开始执行。
	 */
	public static <T extends JobBean> SchedulingMetaInfo newIntervalSchedulingMetaInfo(Class<T> jobClass, 
			String jobName, long interval) {

		return newIntervalSchedulingMetaInfo(jobClass, jobName, DEFAULT_JOB_GROUP, DEFAULT_TRIGGER_GROUP, interval, -1);
	
	}
	/**
	 * 默认延迟40s开始执行。
	 */
	public static <T extends JobBean> SchedulingMetaInfo newIntervalSchedulingMetaInfo(Class<T> jobClass, 
			String jobName, long interval, int repeatCount) {

		return newIntervalSchedulingMetaInfo(jobClass, jobName, DEFAULT_JOB_GROUP, DEFAULT_TRIGGER_GROUP, interval, repeatCount);
	
	}
	/**
	 * 默认延迟40s开始执行。
	 */
	public static <T extends JobBean> SchedulingMetaInfo newIntervalSchedulingMetaInfo(Class<T> jobClass, 
			String jobName, String jobGroup, String triggerGroup, long interval, int repeatCount) {

		return newIntervalSchedulingMetaInfo(jobClass, jobName, jobGroup, triggerGroup, interval, repeatCount, 40 * 1000);
	
	}
	public static <T extends JobBean> SchedulingMetaInfo newIntervalSchedulingMetaInfo(Class<T> jobClass, 
			String jobName, long interval, int repeatCount, long delay) {
		
		return newIntervalSchedulingMetaInfo(jobClass, jobName, DEFAULT_JOB_GROUP, DEFAULT_TRIGGER_GROUP, interval, repeatCount, delay);
		
	}
	public static <T extends JobBean> SchedulingMetaInfo newIntervalSchedulingMetaInfo(Class<T> jobClass, 
			String jobName, String jobGroup, String triggerGroup, long interval, int repeatCount, long delay) {

		return createIntervalMetaInfo(jobClass, jobName, jobGroup, triggerGroup, interval, repeatCount, delay);
	
	}
	
	private static <T extends JobBean> SchedulingMetaInfo createCronMetaInfo(final Class<T> jobClass, 
			final String jobName, final String jobGroup, final String triggerGroup, final String cronExpression) {
		
		return new CronSchedulingMetaInfo() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return jobName;
			}
			@Override
			public Class<T> getJobClass() {
				// TODO Auto-generated method stub
				return jobClass;
			}

			@Override
			public String getJobGroup() {
				// TODO Auto-generated method stub
				return jobGroup;
			}

			@Override
			public String getTriggerGroup() {
				// TODO Auto-generated method stub
				return triggerGroup;
			}

			@Override
			public String getCronExpression() {
				// TODO Auto-generated method stub
				return cronExpression;
			}
			@Override
			public int hashCode() {
				return getName().hashCode();
			}
			@Override
			public boolean equals(Object obj) {
				return obj != null && obj instanceof CronSchedulingMetaInfo
						&& getJobGroup() != null
						&& getTriggerGroup() != null
						&& getJobGroup().equals(((SchedulingMetaInfo)obj).getJobGroup())
						&& getTriggerGroup().equals(((SchedulingMetaInfo)obj).getTriggerGroup())
						&& getName().equals(((SchedulingMetaInfo)obj).getName());
			}
		};
	}
	
	private static <T extends JobBean> SchedulingMetaInfo createIntervalMetaInfo(final Class<T> jobClass, 
			final String jobName, final String jobGroup, final String triggerGroup, final long interval, 
			final int repeatCount, final long delay) {
		
		return new IntervalSchedulingMetaInfo() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return jobName;
			}

			@Override
			public Class<? extends JobBean> getJobClass() {
				// TODO Auto-generated method stub
				return jobClass;
			}

			@Override
			public String getJobGroup() {
				// TODO Auto-generated method stub
				return jobGroup;
			}

			@Override
			public String getTriggerGroup() {
				// TODO Auto-generated method stub
				return triggerGroup;
			}

			@Override
			public int getRepeatCount() {
				// TODO Auto-generated method stub
				return repeatCount;
			}

			@Override
			public long getInterval() {
				// TODO Auto-generated method stub
				return interval;
			}
			@Override
			public long getDelay() {
				// TODO Auto-generated method stub
				return delay;
			}

			@Override
			public int hashCode() {
				return getName().hashCode();
			}
			@Override
			public boolean equals(Object obj) {
				return obj != null && obj instanceof IntervalSchedulingMetaInfo
						&& getJobGroup() != null
						&& getTriggerGroup() != null
						&& getJobGroup().equals(((SchedulingMetaInfo)obj).getJobGroup())
						&& getTriggerGroup().equals(((SchedulingMetaInfo)obj).getTriggerGroup())
						&& getName().equals(((SchedulingMetaInfo)obj).getName());
			}
		};
	}
}

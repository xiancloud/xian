package info.xiancloud.core.support.quartz;


import info.xiancloud.core.util.LOG;

public class QuartzKit {
	
	/**
	 * 
	 * @param metaInfo
	 * 
	 * @throws IllegalStateException - if fail to launch Quartz
	 */
	public static void schedule(SchedulingMetaInfo metaInfo) {
		if (QuartzLauncher.start()) {
			QuartzLauncher.schedule(metaInfo);
		} else {
			String info =  null;
			  if (metaInfo instanceof CronSchedulingMetaInfo) {
				  CronSchedulingMetaInfo meta = (CronSchedulingMetaInfo)metaInfo;
				  info =  String.format("定时任务:class:%s,expression:%s",
							 meta.getJobClass().getName(),
							 meta.getCronExpression());
			  } else {
				  IntervalSchedulingMetaInfo meta = (IntervalSchedulingMetaInfo)metaInfo;
				  info =  String.format("定时任务:class:%s,interval:%s",
	    		   			meta.getJobClass().getName(),
	    		   			meta.getInterval());
			  } 
			 
			throw new IllegalStateException(
					String.format("Fail to schedule job(%s) ,cause: Can not launch Quartz", info)
					);
		}
	}
	public static boolean unschedule(SchedulingMetaInfo metaInfo) {
		if (QuartzLauncher.start()) {
			LOG.info(String.format("存在定时任务 %s 进行取消操作",metaInfo.getJobClass().getName()));
			return QuartzLauncher.unschedule(metaInfo);
		} else {
			return false;
		}
		
	}
}

package info.xiancloud.core.support.quartz;

import org.quartz.Job;

/**
 * 定时任务接口(Cron/Interval Job). <br>
 *
 * expample:
 * <code>
 * SchedulingMetaInfo[] getMetaInfos() {   <br>
 * 		return new SchedulingMetaInfo[]{   <br>
 * 			SchedulingMetaInfoBuilder.newCronSchedulingMetaInfo(                       <br>
 * 					ExampleCronJob.class, ExampleCronJob.getSimpleName(), "0 0/30 * * * ?"   <br>
 * 			),                                                                         <br>
 * 			SchedulingMetaInfoBuilder.newIntervalSchedulingMetaInfo(                   <br>
 * 					ExampleInterval.class, ExampleInterval.getSimpleName(), 1000 * 30  <br>
 * 			)                                                                           <br>
 * 		};   <br>
 * }  <br>
 * </code>
 * @author explorerlong
 *
 */
public interface JobBean extends Job{
	SchedulingMetaInfo[] getMetaInfos();
}

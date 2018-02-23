package info.xiancloud.plugin.quartz;

import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.Reflection;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuartzLauncher {
    protected static SchedulerFactory sf = new StdSchedulerFactory();
    protected static Scheduler scheduler = null;
    private static final Object quartzStartStopLock = new Object();
    private static AtomicBoolean started = new AtomicBoolean(false);

    public static boolean start() {
        synchronized (quartzStartStopLock) {
            if (started.get()) {
                LOG.warn("已启动，不再重复启动");
                return true;
            }
            boolean ret = false;
            try {
                if (scheduler == null) {
                    scheduler = sf.getScheduler();
                    List<JobBean> jobInstances = Reflection.getSubClassInstances(JobBean.class);
                    doStart(jobInstances);
                    scheduler.start();
                    LOG.info("Quartz启动成功!");
                } else {
                    LOG.info("Quarty已经启动,不作任何操作!");
                }
                ret = true;
            } catch (Exception e) {
                scheduler = null;
                LOG.error("Quartz 启动失败!", e);
            } finally {
                started.set(true);
            }
            return ret;
        }
    }

    public static void stop() {
        synchronized (quartzStartStopLock) {
            if (!started.get()) {
                LOG.warn("未启动，不需要停止");
                return;
            }
            try {
                LOG.debug("以前是等待任务结束后退出，现改为直接退出定时器");
                scheduler.shutdown(false);
            } catch (SchedulerException e) {
                LOG.error(e);
            }
        }
    }

    private static void doStart(List<JobBean> jobInstances) {
        if (jobInstances != null) {
            for (JobBean job : jobInstances) {
                SchedulingMetaInfo[] metaInfos = job.getMetaInfos();
                if (metaInfos != null && metaInfos.length > 0) {
                    for (SchedulingMetaInfo metaInfo : metaInfos) {
                        try {
                            schedule(metaInfo);
                        } catch (Exception e) {
                            LOG.error("", e);
                        }
                    }
                } else {
                    LOG.error(job.getClass() + "缺少定时任务描述信息，getMetaInfos() return null!");
                }

            }
        }
    }

    protected static void schedule(SchedulingMetaInfo metaInfo) {
        TriggerKey key = new TriggerKey(metaInfo.getName(), metaInfo.getTriggerGroup());
        try {
            //校验 meta info信息
            validateMetaInfo(metaInfo);

            JobDetail jobDetail = JobBuilder.newJob(metaInfo.getJobClass())
                    .withIdentity(metaInfo.getName(), metaInfo.getJobGroup())
                    .build();
            jobDetail.getJobDataMap().put("metaInfo", metaInfo);
            //触发器
            Trigger trigger = null;
            if (metaInfo instanceof CronSchedulingMetaInfo) {
                CronSchedulingMetaInfo meta = (CronSchedulingMetaInfo) metaInfo;
                trigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.cronSchedule(meta.getCronExpression()))
                        .withIdentity(key)
                        .build();
                LOG.info(String.format("添加定时任务:class:%s,expression:%s",
                        jobDetail.getJobClass().getName(),
                        meta.getCronExpression()));
            } else {
                IntervalSchedulingMetaInfo meta = (IntervalSchedulingMetaInfo) metaInfo;
                SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(meta.getInterval());
                if (meta.getRepeatCount() >= 0) {
                    builder.withRepeatCount(meta.getRepeatCount());
                } else {
                    builder.repeatForever();
                }
                long delayInSeconds = meta.getDelay() / 1000;
                if (delayInSeconds > 0) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.SECOND, (int) delayInSeconds);
                    c.getTime();

                    //程序刚启动立即执行可能有问题，延迟40s
                    trigger = TriggerBuilder.newTrigger()
                            .startAt(c.getTime())
                            .withSchedule(builder)
                            .withIdentity(key)
                            .build();
                } else {

                    trigger = TriggerBuilder.newTrigger()
                            .withSchedule(builder)
                            .withIdentity(key)
                            .build();
                }
                LOG.info(String.format("添加定时任务:class:%s,interval:%s",
                        jobDetail.getJobClass().getName(),
                        meta.getInterval()));
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("建立定时任务失败，任务标识(jobClass):" + metaInfo.getJobClass()
                    + ",(group,name):" + key, e);
        }
    }

    protected static boolean unschedule(SchedulingMetaInfo metaInfo) {
        LOG.info("取消定时任务" + metaInfo.getJobClass().getName());
        TriggerKey key = new TriggerKey(metaInfo.getName(), metaInfo.getTriggerGroup());
        boolean ret = false;
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                ret = scheduler.unscheduleJob(key);
            }
        } catch (SchedulerException e) {
            LOG.error("取消任务[" + key + "]失败", e);
        }
        return ret;
    }

    private static void validateMetaInfo(SchedulingMetaInfo metaInfo) {

        if (metaInfo.getName() == null || metaInfo.getName().length() < 1) {
            throw new IllegalArgumentException("定时任务描述信息jobName不能为空!");
        }
        if (metaInfo instanceof CronSchedulingMetaInfo) {
            CronSchedulingMetaInfo meta = (CronSchedulingMetaInfo) metaInfo;
            if (meta.getCronExpression() == null
                    || meta.getCronExpression().trim().length() < 1) {
                throw new IllegalArgumentException("定时任务描述信息jobClasss不能为空!");
            }
        } else {
            IntervalSchedulingMetaInfo meta = (IntervalSchedulingMetaInfo) metaInfo;
            if (meta.getInterval() < 1) {
                throw new IllegalArgumentException("定时任务描述信息interval(Milliseconds)不能为0!");
            }
        }
    }

    //suwy增加 判断指定定时器状态
    public static boolean checkExists(String jobName) {
        LOG.info("查看任务" + jobName);
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                JobKey jobKey = JobKey.jobKey(jobName, SchedulingMetaInfoBuilder.DEFAULT_JOB_GROUP);
                return scheduler.checkExists(jobKey);
            } else {
                LOG.info("整个定时器都没在跑");
                return false;
            }
        } catch (SchedulerException e) {
            LOG.error("查看任务[" + jobName + "]失败", e);
        }
        return false;
    }

}

package quartz.demo_b;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import quartz.demo_a.HelloJob;

/**
 * Simple Triggers (简单触发器) 的使用. 几点说明:
 * 
 * 1 DateBuilder.nextGivenSecondDate 的意思代码中已经说明了,就是下一个你想要的时间,不懂的可以自己测试一下
 * 
 * 2 同一个任务可以多次注册.
 * 
 * 3 重复执行 需要设置
 * 
 * trigger = newTrigger() .withIdentity("trigger3", "group1")
 * .startAt(startTime) .withSchedule( simpleSchedule()
 * .withIntervalInSeconds(10)// 重复间隔 .withRepeatCount(10)) // 重复次数 .build();
 * 
 * 如果需要一直重复:
 * 
 * trigger = newTrigger() .withIdentity("trigger6", "group1")
 * .startAt(startTime) .withSchedule( simpleSchedule().withIntervalInSeconds(40)
 * .repeatForever()).build();
 * 
 * 4 无trigger注册,表示任务立即执行 : sched.addJob(job, true);
 * 
 * 5 // 在scheduler.start之后调用，可以在job开始后重新定义trigger，然后重新注册
 * 
 * sched.rescheduleJob(trigger.getKey(), trigger);
 * 
 * 
 * 
 * 这个Example 还是比较简单的,主要 就是Trigger的参数设置,多看看代码,里面有注释的 0.0
 * 
 * 自己动手试试吧
 */
public class SimpleTriggerExample {

	public void run() throws Exception {
		// 日期格式化
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		System.out.println("--------------- 初始化 -------------------");

		// 下一个第15秒 例:
		// 当前 10秒,则 执行时间为15秒
		// 当前 16秒,则 执行时间为30秒
		// 当前 33秒,则 执行时间为45秒
		// 当前 48秒,则 执行时间为00秒
		Date startTime = DateBuilder.nextGivenSecondDate(null, 15);

		// job1 将只会执行一次
		JobDetail job = newJob(SimpleJob.class).withIdentity("job1", "group1").build();
		SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime).build();
		// 把job1 和 trigger加入计划 . ft:此任务要执行的时间
		Date ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " 将在 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		JobDetail j = newJob(HelloJob.class).withIdentity("j", "g").build();
		SimpleTrigger t = (SimpleTrigger) newTrigger().withIdentity("t", "g").startAt(startTime).build();
		Date f = sched.scheduleJob(j, t);
		System.out.println(j.getKey().getName()+ " 将在 : " + dateFormat.format(f) + " 时运行 ");
		
		// job2 将只会和执行一次(和job1一样一样的,吼~~)
		job = newJob(SimpleJob.class).withIdentity("job2", "group1").build();
		trigger = (SimpleTrigger) newTrigger().withIdentity("trigger2", "group1").startAt(startTime).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " 将在 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		// job3 将执行11次(执行1次,重复10次) ,每10秒重复一次
		job = newJob(SimpleJob.class).withIdentity("job3", "group1").build();
		trigger = newTrigger().withIdentity("trigger3", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInSeconds(10)// 重复间隔
				.withRepeatCount(10)) // 重复次数
				.build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " 将在 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		// trigger3 改变了. 每隔10s重复.共重复2次
		// 此处说明 , 上面job3已经 设定了 trigger3 重复10次,每次10s
		// 在这里又改变了 trigger3的设置,不会对以前构成影响,而是当做一个新的来处理
		trigger = newTrigger().withIdentity("trigger3", "group2").startAt(startTime).withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(2)).forJob(job).build();
		ft = sched.scheduleJob(trigger);
		System.out.println(job.getKey().getName() + " 改变过trigger3属性的job3 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		// job5 将在5分钟后运行一次
		job = newJob(SimpleJob.class).withIdentity("job5", "group1").build();
		trigger = (SimpleTrigger) newTrigger().withIdentity("trigger5", "group1").startAt(futureDate(5, IntervalUnit.MINUTE)) // 设定5分钟后运行
				.build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " 将在 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		// job6 每40s运行一次,没有指定重复次数,则无下限的重复
		job = newJob(SimpleJob.class).withIdentity("job6", "group1").build();
		trigger = newTrigger().withIdentity("trigger6", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " 将在 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		// 所有的任务都被加入到了 scheduler中 ,但只有 schedulers.start(); 时才开始执行
		sched.start();
		System.out.println("------- 开始调度 (调用.start()方法) ----------------");
		System.out.println("-------系统 启动 的 时间 :" + dateFormat.format(new Date()));

		// 在 scheduled.start(); 之后,还可以将 jobs 添加到执行计划中
		// job7 将重复20次 ,每5分钟重复一次
		job = newJob(SimpleJob.class).withIdentity("job7", "group1").build();
		trigger = newTrigger().withIdentity("trigger7", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInMinutes(5) // 5分钟
				.withRepeatCount(20)) // 重复20次
				.build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " 将在 : " + dateFormat.format(ft) + " 时运行.并且重复: " + trigger.getRepeatCount() + " 次, 每次间隔 " + trigger.getRepeatInterval() / 1000 + " 秒");

		// job8 可以立即执行. 无trigger注册
		job = newJob(SimpleJob.class).withIdentity("job8", "group1").storeDurably().build();
		sched.addJob(job, true);
		System.out.println("手动触发  job8...(立即执行)");
		sched.triggerJob(jobKey("job8", "group1"));

		System.out.println("------- 等待30 秒... --------------");

		try {
			Thread.sleep(30L * 1000L);
		} catch (Exception e) {
		}

		// job7 将马上执行,重复10次,每秒一次
		System.out.println("-------  重新安排 ... --------------------");
		trigger = newTrigger().withIdentity("trigger7", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInMinutes(5).withRepeatCount(20)).build();

		ft = sched.rescheduleJob(trigger.getKey(), trigger);
		System.out.println("job7 被重新安排 在 : " + dateFormat.format(ft) + "  执行. \r   当前时间 :" + dateFormat.format(new Date()) + "预定执行时间已过,任务立即执行");

		try {
			System.out.println("------- 等待5分钟  ... ------------");
			Thread.sleep(300L * 1000L);
		} catch (Exception e) {
		}

		sched.shutdown(true);
		System.out.println("------- 调度已关闭 ---------------------");

		// 显示一下 已经执行的任务信息
		SchedulerMetaData metaData = sched.getMetaData();
		System.out.println("~~~~~~~~~~  执行了 " + metaData.getNumberOfJobsExecuted() + " 个 jobs.");

	}

	public static void main(String[] args) throws Exception {
		SimpleTriggerExample example = new SimpleTriggerExample();
		example.run();
	}

}

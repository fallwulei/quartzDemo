package quartz.demo_a;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import java.util.Date;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 这外例子说明了如何开始和关闭一个 Quartz scheduler 及如何运行一个任务 通过代码可以看到几个重要的类： 
 * •JobDetail：
 * 真正的任务内容，任务本身是集成Job接口的，但是真正的任务是JobBuilder通过反射的方式实例化的， 
 * •Trigger：
 * 触发器，定义任务应当开始的时间，主要分为两类SimpleTrigger,CronTrigger，当前例子的就是简单触发器，
 * CronTrigger主要用于处理quartz表达式定义的任务，比如每个月20号，每个星期一之类的。 
 * •Scheduler：
 * 计划执行者，现在我们有了要做的内容
 * (HelloJob)，有了要做的时间(下一分钟)，接下来，就把这两个内容填充到计划任务Scheduler对象里面，到了时间它就可以自动运行了
 */
public class SimpleExample {

	private static Logger log = LoggerFactory.getLogger(SimpleExample.class);
	
	public void run() throws Exception {
		System.out.println("------- 初始化 获得 Scheduler 对象 -------------");
		log.info("------- 初始化 获得 Scheduler 对象 -------------");
		// 获得 Scheduler 对象
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();

		// 获取下一分钟,即当前时间的下一分钟
		Date runTime = evenMinuteDate(new Date());

		// 定义一个 job 对象并绑定我们写的 HelloJob 类
		// 真正执行的任务并不是Job接口的实例，而是用反射的方式实例化的一个JobDetail实例
		JobDetail job = newJob(HelloJob.class).withIdentity("job1", "group1").build();

		// 定义一个触发器，startAt方法定义了任务应当开始的时间 .即下一个整数分钟执行
		//Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(runTime).build();
		
		//当前时间开始执行 1秒一次，执行10次
		//Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(new Date()).withSchedule(simpleSchedule().withIntervalInSeconds(1).withRepeatCount(10)).forJob(job).build();
		
		//2秒一次一直执行
		Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(new Date()).withSchedule(simpleSchedule().withIntervalInSeconds(2).repeatForever()).forJob(job).build();

		// 将任务和Trigger放入scheduler
		sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 将在: " + runTime + " 运行");
		log.info(job.getKey() + " 将在: " + runTime + " 运行");
		// 启动
		sched.start();
		System.out.println("------- 任务调度已经启动 -----------------");
		System.out.println("------- 等待 15 秒,保证下一个整数分钟出现 ... ---");
		log.info("------- 任务调度已经启动 -----------------");
		log.info("------- 等待 15 秒,保证下一个整数分钟出现 ... ---");
		try {
			// 等待65秒，保证下一个整数分钟出现，这里注意，如果主线程停止，任务是不会执行的
			Thread.sleep(15L * 1000L);

		} catch (Exception e) {

		}

		SchedulerMetaData metaData = sched.getMetaData();
		System.out.println("~~~~~~~~~~  执行了 " + metaData.getNumberOfJobsExecuted() + " 个 jobs.");
		log.info("~~~~~~~~~~  执行了 " + metaData.getNumberOfJobsExecuted() + " 个 jobs.");
		
		// 关闭 scheduler
		sched.shutdown(true);
		System.out.println("------- 调度已关闭 ---------------------");
		log.info("------- 调度已关闭 ---------------------");
	}

	public static void main(String[] args) throws Exception {
		SimpleExample example = new SimpleExample();
		example.run();

	}

}

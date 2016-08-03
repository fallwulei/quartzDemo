package quartz.demo_c;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @info quartzDemo
 * @author Wu,Lei
 * @version 2015-11-12
 */
public class Demo implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("-----------first quartz-------------");
	}

	/**
	 * @param args
	 * @throws SchedulerException
	 */
	public static void main(String[] args) throws SchedulerException {
		// TODO Auto-generated method stub
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		JobDetail job = newJob(Demo.class).withIdentity("job", "group").build();
		Trigger trigger = newTrigger().withIdentity("trigger", "group").startAt(new Date()).withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).forJob(job).build();
		sched.scheduleJob(job, trigger);
		// 启动
		sched.start();
		try {
			// 等待5秒
			Thread.sleep(5L * 1000L);
		} catch (Exception e) {
		}
		// 关闭 scheduler
		sched.shutdown(true);
		SchedulerMetaData metaData = sched.getMetaData();
		System.out.println("~~~~~~~~~~  执行了 " + metaData.getNumberOfJobsExecuted() + " 个 jobs");
	}

}

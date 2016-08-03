package quartz.demo_b;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// job 的名字
		String jobName = context.getJobDetail().getKey().getName();

		// 任务执行的时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String jobRunTime = dateFormat.format(Calendar.getInstance().getTime());

		// 输出任务执行情况
		System.out.println("任务 : " + jobName + " 在  " + jobRunTime + " 执行了 ");
	}
}

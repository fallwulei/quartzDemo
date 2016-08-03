package quartz.demo_a;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @info quartzDemo
 * @author Wu,Lei
 * @version 2015-11-11
 */
public class HelloJob implements Job {

	private static Logger log = LoggerFactory.getLogger(HelloJob.class);

	/**
	 * ʵ�����Լ��Ķ�ʱ���� ,��������дʲô,��˵����!<br>
	 * ����ֻ��� HelloWorld!
	 */
	@Override
	public void execute(JobExecutionContext context) {
		// ��� HelloWorld !
		System.out.println("Hello World! - " + new Date());
		log.info("Hello World! - " + new Date());
	}

}
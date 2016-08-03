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
 * Simple Triggers (�򵥴�����) ��ʹ��. ����˵��:
 * 
 * 1 DateBuilder.nextGivenSecondDate ����˼�������Ѿ�˵����,������һ������Ҫ��ʱ��,�����Ŀ����Լ�����һ��
 * 
 * 2 ͬһ��������Զ��ע��.
 * 
 * 3 �ظ�ִ�� ��Ҫ����
 * 
 * trigger = newTrigger() .withIdentity("trigger3", "group1")
 * .startAt(startTime) .withSchedule( simpleSchedule()
 * .withIntervalInSeconds(10)// �ظ���� .withRepeatCount(10)) // �ظ����� .build();
 * 
 * �����Ҫһֱ�ظ�:
 * 
 * trigger = newTrigger() .withIdentity("trigger6", "group1")
 * .startAt(startTime) .withSchedule( simpleSchedule().withIntervalInSeconds(40)
 * .repeatForever()).build();
 * 
 * 4 ��triggerע��,��ʾ��������ִ�� : sched.addJob(job, true);
 * 
 * 5 // ��scheduler.start֮����ã�������job��ʼ�����¶���trigger��Ȼ������ע��
 * 
 * sched.rescheduleJob(trigger.getKey(), trigger);
 * 
 * 
 * 
 * ���Example ���ǱȽϼ򵥵�,��Ҫ ����Trigger�Ĳ�������,�࿴������,������ע�͵� 0.0
 * 
 * �Լ��������԰�
 */
public class SimpleTriggerExample {

	public void run() throws Exception {
		// ���ڸ�ʽ��
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		System.out.println("--------------- ��ʼ�� -------------------");

		// ��һ����15�� ��:
		// ��ǰ 10��,�� ִ��ʱ��Ϊ15��
		// ��ǰ 16��,�� ִ��ʱ��Ϊ30��
		// ��ǰ 33��,�� ִ��ʱ��Ϊ45��
		// ��ǰ 48��,�� ִ��ʱ��Ϊ00��
		Date startTime = DateBuilder.nextGivenSecondDate(null, 15);

		// job1 ��ֻ��ִ��һ��
		JobDetail job = newJob(SimpleJob.class).withIdentity("job1", "group1").build();
		SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime).build();
		// ��job1 �� trigger����ƻ� . ft:������Ҫִ�е�ʱ��
		Date ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " ���� : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		JobDetail j = newJob(HelloJob.class).withIdentity("j", "g").build();
		SimpleTrigger t = (SimpleTrigger) newTrigger().withIdentity("t", "g").startAt(startTime).build();
		Date f = sched.scheduleJob(j, t);
		System.out.println(j.getKey().getName()+ " ���� : " + dateFormat.format(f) + " ʱ���� ");
		
		// job2 ��ֻ���ִ��һ��(��job1һ��һ����,��~~)
		job = newJob(SimpleJob.class).withIdentity("job2", "group1").build();
		trigger = (SimpleTrigger) newTrigger().withIdentity("trigger2", "group1").startAt(startTime).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " ���� : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		// job3 ��ִ��11��(ִ��1��,�ظ�10��) ,ÿ10���ظ�һ��
		job = newJob(SimpleJob.class).withIdentity("job3", "group1").build();
		trigger = newTrigger().withIdentity("trigger3", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInSeconds(10)// �ظ����
				.withRepeatCount(10)) // �ظ�����
				.build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " ���� : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		// trigger3 �ı���. ÿ��10s�ظ�.���ظ�2��
		// �˴�˵�� , ����job3�Ѿ� �趨�� trigger3 �ظ�10��,ÿ��10s
		// �������ָı��� trigger3������,�������ǰ����Ӱ��,���ǵ���һ���µ�������
		trigger = newTrigger().withIdentity("trigger3", "group2").startAt(startTime).withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(2)).forJob(job).build();
		ft = sched.scheduleJob(trigger);
		System.out.println(job.getKey().getName() + " �ı��trigger3���Ե�job3 : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		// job5 ����5���Ӻ�����һ��
		job = newJob(SimpleJob.class).withIdentity("job5", "group1").build();
		trigger = (SimpleTrigger) newTrigger().withIdentity("trigger5", "group1").startAt(futureDate(5, IntervalUnit.MINUTE)) // �趨5���Ӻ�����
				.build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " ���� : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		// job6 ÿ40s����һ��,û��ָ���ظ�����,�������޵��ظ�
		job = newJob(SimpleJob.class).withIdentity("job6", "group1").build();
		trigger = newTrigger().withIdentity("trigger6", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " ���� : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		// ���е����񶼱����뵽�� scheduler�� ,��ֻ�� schedulers.start(); ʱ�ſ�ʼִ��
		sched.start();
		System.out.println("------- ��ʼ���� (����.start()����) ----------------");
		System.out.println("-------ϵͳ ���� �� ʱ�� :" + dateFormat.format(new Date()));

		// �� scheduled.start(); ֮��,�����Խ� jobs ��ӵ�ִ�мƻ���
		// job7 ���ظ�20�� ,ÿ5�����ظ�һ��
		job = newJob(SimpleJob.class).withIdentity("job7", "group1").build();
		trigger = newTrigger().withIdentity("trigger7", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInMinutes(5) // 5����
				.withRepeatCount(20)) // �ظ�20��
				.build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey().getName() + " ���� : " + dateFormat.format(ft) + " ʱ����.�����ظ�: " + trigger.getRepeatCount() + " ��, ÿ�μ�� " + trigger.getRepeatInterval() / 1000 + " ��");

		// job8 ��������ִ��. ��triggerע��
		job = newJob(SimpleJob.class).withIdentity("job8", "group1").storeDurably().build();
		sched.addJob(job, true);
		System.out.println("�ֶ�����  job8...(����ִ��)");
		sched.triggerJob(jobKey("job8", "group1"));

		System.out.println("------- �ȴ�30 ��... --------------");

		try {
			Thread.sleep(30L * 1000L);
		} catch (Exception e) {
		}

		// job7 ������ִ��,�ظ�10��,ÿ��һ��
		System.out.println("-------  ���°��� ... --------------------");
		trigger = newTrigger().withIdentity("trigger7", "group1").startAt(startTime).withSchedule(simpleSchedule().withIntervalInMinutes(5).withRepeatCount(20)).build();

		ft = sched.rescheduleJob(trigger.getKey(), trigger);
		System.out.println("job7 �����°��� �� : " + dateFormat.format(ft) + "  ִ��. \r   ��ǰʱ�� :" + dateFormat.format(new Date()) + "Ԥ��ִ��ʱ���ѹ�,��������ִ��");

		try {
			System.out.println("------- �ȴ�5����  ... ------------");
			Thread.sleep(300L * 1000L);
		} catch (Exception e) {
		}

		sched.shutdown(true);
		System.out.println("------- �����ѹر� ---------------------");

		// ��ʾһ�� �Ѿ�ִ�е�������Ϣ
		SchedulerMetaData metaData = sched.getMetaData();
		System.out.println("~~~~~~~~~~  ִ���� " + metaData.getNumberOfJobsExecuted() + " �� jobs.");

	}

	public static void main(String[] args) throws Exception {
		SimpleTriggerExample example = new SimpleTriggerExample();
		example.run();
	}

}

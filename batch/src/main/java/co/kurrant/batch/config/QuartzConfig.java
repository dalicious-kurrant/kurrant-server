//package co.kurrant.batch.config;
//
//import co.kurrant.batch.MembershipCheckJob;
//import lombok.RequiredArgsConstructor;
//import org.quartz.CronTrigger;
//import org.quartz.JobDetail;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
//import org.springframework.scheduling.quartz.JobDetailFactoryBean;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import javax.sql.DataSource;
//import java.util.Objects;
//
//@Configuration
//@RequiredArgsConstructor
//public class QuartzConfig {
//    private final JobConfig jobConfig;
//    private final DataSource dataSource;
//
////    @Bean
////    public SchedulerFactoryBean schedulerFactoryBean() {
////        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
////        schedulerFactoryBean.setDataSource(dataSource);
////        schedulerFactoryBean.setJobDetails(membershipCheckJobDetail());
////        schedulerFactoryBean.setTriggers(membershipCheckTrigger());
////        return schedulerFactoryBean;
////    }
//
//    @Bean
//    public JobDetailFactoryBean membershipCheckJobDetail() {
//        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
//        jobDetailFactoryBean.setJobClass(MembershipCheckJob.class);
//        jobDetailFactoryBean.setDurability(true);
//        return jobDetailFactoryBean;
//    }
//
//    @Bean
//    public CronTriggerFactoryBean membershipCheckTrigger() {
//        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
//        cronTriggerFactoryBean.setJobDetail(Objects.requireNonNull(membershipCheckJobDetail().getObject()));
//        cronTriggerFactoryBean.setStartDelay(5000);
//        cronTriggerFactoryBean.setCronExpression("0 0 0 * * ?");
//        return cronTriggerFactoryBean;
//    }
//}
//

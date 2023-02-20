//package co.kurrant.batch;
//
//import co.dalicious.domain.user.entity.Membership;
//import co.dalicious.domain.user.entity.User;
//import co.dalicious.domain.user.repository.MembershipRepository;
//import co.dalicious.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//
//import java.time.LocalDate;
//
//@RequiredArgsConstructor
//public class MembershipCheckJob implements Job {
//
//    private final JobLauncher jobLauncher;
//
//    private final Job membershipCheckJob;
//
//    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
//        try {
//            JobExecution execution = jobLauncher.run(membershipCheckJob, jobParameters);
//            System.out.println("Exit Status : " + execution.getStatus());
//        } catch (JobExecutionException e) {
//            e.printStackTrace();
//        }
//    }
//}

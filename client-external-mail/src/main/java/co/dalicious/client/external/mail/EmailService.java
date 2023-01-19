package co.dalicious.client.external.mail;

import co.dalicious.data.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;

@PropertySource("classpath:application-mail.properties")
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final SendEmailService sendEmailService;
    private final RedisUtil redisUtil;

    /*
        메일 발송
        sendSimpleMessage의 매개변수로 들어온 to는 인증번호를 받을 메일주소
        MimeMessage 객체 안에 내가 전송할 메일의 내용을 담아준다.
        bean으로 등록해둔 javaMailSender 객체를 사용하여 이메일 send
     */
    public void sendSimpleMessage(List<String> receivers, String subject, String content) throws Exception {
        try{
            sendEmailService.send(subject, content, receivers); // 메일 발송
        }catch(IllegalArgumentException es){
            throw new IllegalArgumentException();
        }

    }
}

package co.dalicious.client.external.mail;

import co.dalicious.data.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@PropertySource("classpath:application-mail.properties")
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final SendEmailService sendEmailService;
    private final RedisUtil redisUtil;

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    /*
        메일 발송
        sendSimpleMessage의 매개변수로 들어온 to는 인증번호를 받을 메일주소
        MimeMessage 객체 안에 내가 전송할 메일의 내용을 담아준다.
        bean으로 등록해둔 javaMailSender 객체를 사용하여 이메일 send
     */
    public String sendSimpleMessage(List<String> receivers)throws Exception {
        String key = createKey(); //인증번호 생성
        String receiver = receivers.get(0);
        
        log.info("인증 번호 : " + key);
        log.info("보내는 대상 : "+ receiver);

        
        String subject = ("[커런트] 회원가입 인증 코드: "); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String content="";
        content += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        content += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
        content += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        content += key;
        content += "</td></tr></tbody></table></div>";

        try{
            redisUtil.setDataExpire(key, receiver, 300 * 1L);
            sendEmailService.send(subject, content, receivers); // 메일 발송
        }catch(IllegalArgumentException es){
            throw new IllegalArgumentException();
        }
        
        return key; // 메일로 보냈던 인증 코드를 서버로 리턴
    }

    public void verifyEmail(String key) throws IllegalArgumentException {
        String memberEmail = redisUtil.getData(key);
        if (memberEmail == null) {
            throw new IllegalArgumentException();
        }
        redisUtil.deleteData(key);
    }
}

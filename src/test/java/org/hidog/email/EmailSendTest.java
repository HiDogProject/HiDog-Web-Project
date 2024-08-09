package org.hidog.email;

import org.hidog.email.services.EmailMessage;
import org.hidog.email.services.EmailSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmailSendTest {
    @Autowired
    private EmailSendService emailSendService;

    @Test
    void sendTest() {
        EmailMessage message = new EmailMessage("sunkyu0384@naver.com", "제목", "내용");
        boolean success = emailSendService.sendMail(message);
        // 변경!!!
        assertTrue(success);
    }
}

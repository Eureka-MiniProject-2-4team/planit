package com.eureka.mp2.team4.planit.common.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // fromEmail 주입을 위해 reflection 사용
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@planit.com");
    }

    @Test
    @DisplayName("비밀번호 재설정 메일 전송 성공")
    void testSendPasswordResetEmail_Success() throws Exception {
        String to = "user@example.com";
        String token = "abc123";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordResetEmail(to, token);

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("메일 전송 중 예외 발생 → 예외 catch되고 로깅만 수행")
    void testSendPasswordResetEmail_Failure() throws Exception {
        String to = "user@example.com";
        String token = "bad-token";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("메일 전송 실패"))
                .when(javaMailSender).send(any(MimeMessage.class));

        emailService.sendPasswordResetEmail(to, token);

        verify(javaMailSender).send(any(MimeMessage.class)); // 호출은 됨
        // 예외를 던지지 않으므로 assertThrows 없음
    }
}

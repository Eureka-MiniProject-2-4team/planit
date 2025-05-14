package com.eureka.mp2.team4.planit.common.email;

public class MailTemplates {

    private MailTemplates() {
    } // static only

    public static String passwordReset(String resetLink) {
        return """
                <html>
                  <body>
                    <div style="font-family: Arial, sans-serif; padding: 20px; background: #f9f9f9;">
                      <h2 style="color: #4CAF50;">Planit 비밀번호 재설정</h2>
                      <p>안녕하세요, 아래 버튼을 클릭하여 비밀번호를 재설정해주세요:</p>
                      <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #4CAF50; 
                          color: white; text-decoration: none; border-radius: 5px;">비밀번호 재설정</a>
                      <p style="margin-top: 20px; font-size: 12px; color: #999;">
                        이 링크는 30분간만 유효합니다. 요청하지 않았다면 무시해 주세요.
                      </p>
                    </div>
                  </body>
                </html>
                """.formatted(resetLink);
    }

    public static String PASSWORD_RESET_SUBJECT = "비밀번호 재설정 링크입니다";
    public static String PASSWORD_RESET_LINK = "http://localhost:8080/reset-password.html?token=";
}

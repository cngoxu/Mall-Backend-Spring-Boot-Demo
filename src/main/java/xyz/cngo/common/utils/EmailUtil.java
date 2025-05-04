package xyz.cngo.common.utils;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.cngo.common.error.BusinessException;
import xyz.cngo.common.error.EmBusinessError;
import xyz.cngo.controller.BaseController;

import java.util.regex.Pattern;

@Component
public class EmailUtil {
    /**
     * 发件API密钥 - 通过环境变量配置
     */
    private static String Resend_API;

    /**
     * 发件邮箱地址 - 也可以通过环境变量配置
     * 格式：发件人名称 + 邮箱地址
     */
    private static String From;

    @Value("${custom.email.resend-api}")
    public void setResendApi(String apiKey) {
        EmailUtil.Resend_API = apiKey;
    }

    @Value("${custom.email.from}")
    public void setFrom(String fromAddress) {
        EmailUtil.From = fromAddress;
    }

    /**
     * 发送邮件的方法
     * @param to 收件人邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public static void sendEmail(String to, String subject, String content) throws BusinessException {
        if(!Pattern.matches(BaseController.EMAIL_REGEX, to)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "邮箱格式不正确");
        }
        Resend resend = new Resend(Resend_API);
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from(From)
                .to(to)
                .subject(subject)
                .html(content)
                .build();
        try {
            SendEmailResponse data = resend.emails().send(sendEmailRequest);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.EMAIL_SEND_FAILED, "邮件发送失败");
        }
    }

    /**
     * 发送验证码邮件
     * @param to 收件人邮箱地址
     * @param verificationCode 验证码
     */
    public static void sendVerificationCode(String to, String verificationCode, String currentTime) throws BusinessException {
        String subject = "【CNGO】验证码";
        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                + "<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>"
                + "<h1 style='color: #333;'>验证码</h1>"
                + "<p style='font-size: 18px; color: #666;'>尊敬的用户，您正在尝试进行身份验证。</p>"
                + "<p style='font-size: 24px; color: #333; font-weight: bold;'>验证码：<span style='color: #ff6600;'>" + verificationCode + "</span></p>"
                + "<p style='font-size: 14px; color: #999;'>请注意，此验证码仅用于身份验证，切勿泄露给他人。验证码将在10分钟内有效。</p>"
                + "<p style='font-size: 14px; color: #999;'>邮件创建时间：" + currentTime + "</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendEmail(to, subject, content);
    }
}

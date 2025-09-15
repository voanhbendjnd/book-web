package djnd.ben1607.drink_shop.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
// import net.bytebuddy.utility.RandomString;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public EmailService(
            UserRepository userRepository, JavaMailSender javaMailSender,
            SpringTemplateEngine springTemplateEngine, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.templateEngine = springTemplateEngine;
    }

    public String generateOneTimePassword(User user) {
        this.clearOTP(user);
        Random ran = new Random();
        // String OTP = RandomString.make(8); // -> net.bytebuddy.utility.RandomString
        // <-
        String OTP = ran.nextInt(1000, 9999) + "";
        String hashOTP = this.passwordEncoder.encode(OTP);
        user.setOneTimePassword(hashOTP);
        user.setOtpRequestedTime(new Date());
        this.userRepository.save(user);
        return OTP;
    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart,
            boolean isHtml, List<FileSystemResource> attachments) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);

            if (isMultipart) {
                for (FileSystemResource file : attachments) {
                    // Thêm ảnh dưới dạng nội dung (inline)
                    // Dùng "addInline" để ảnh hiển thị trong body, không phải đính kèm
                    message.addInline(file.getFilename(), file);
                }
            }

            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    public void sendOTPToEmail(User user) {
        String OTP = this.generateOneTimePassword(user);
        this.sendOTPEmail(user.getEmail(), OTP + " là mã khôi phục tài khoản drink web shop của bạn", "index",
                user.getName(), OTP);
    }

    @Async
    public void sendOTPEmail(String to, String subject, String templateName, String username,
            String OTP) {
        Context context = new Context();
        context.setVariable("name", username);
        context.setVariable("OTP", OTP);
        // Tạo danh sách các file ảnh
        List<FileSystemResource> attachments = new ArrayList<>();
        attachments.add(new FileSystemResource(
                new File("C:/Users/PC/Pictures/Hinhnentoanhocchomaytinh13-kichthuoc1920x1080-800x450.jpg")));
        attachments.add(new FileSystemResource(
                new File("C:/Users/PC/Pictures/genshin-impact-lumine-5k-8k-1920x1080-5163.jpg")));
        String content = this.templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true, attachments);
    }

    public void clearOTP(User user) {
        user.setOneTimePassword(null);
        user.setOtpRequestedTime(null);
        this.userRepository.save(user);
    }
}

package com.ninos.notification.service;

import com.ninos.auth_users.entity.User;
import com.ninos.enums.NotificationType;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.entity.Notification;
import com.ninos.notification.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Async
    @Override
    public void sendEmail(NotificationDTO notificationDTO, User user) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());

            // used if there is a template
            if(notificationDTO.getTemplateName() != null){
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);
                helper.setText(htmlContent, true);
            }else {
                // used if there is no template(just send a body)
                helper.setText(notificationDTO.getBody(), true);
            }

            mailSender.send(mimeMessage);

            // save to database table
            Notification savedNotification = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .body(notificationDTO.getBody())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepository.save(savedNotification);

        }catch (MessagingException e){
            log.error(e.getMessage());
        }

    }

}

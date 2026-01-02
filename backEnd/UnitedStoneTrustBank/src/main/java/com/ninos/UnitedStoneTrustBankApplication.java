package com.ninos;

import com.ninos.auth_users.entity.User;
import com.ninos.enums.NotificationType;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;


//@RequiredArgsConstructor
@SpringBootApplication
@EnableAsync
public class UnitedStoneTrustBankApplication {

//		private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(UnitedStoneTrustBankApplication.class, args);
	}


//	@Bean
//	CommandLineRunner runner(){
//		return args -> {
//			NotificationDTO notificationDTO = NotificationDTO.builder()
//					.recipient("ninos1357@yahoo.com")
//					.subject("Hellow testing email")
//					.body("Hi This ninos from nalkaton")
//					.type(NotificationType.EMAIL)
//					.build();
//
//			notificationService.sendEmail(notificationDTO, new User());
//		};
//	}

}

package com.ninos.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ninos.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

    private Long id;
    private String subject;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String body;
    private NotificationType type;
    private LocalDateTime createdAt;

    // For values/variables to be passed into email template to send
    private String templateName;
    private Map<String, Object> templateVariables;

}


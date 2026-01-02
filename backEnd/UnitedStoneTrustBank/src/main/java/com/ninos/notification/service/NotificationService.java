package com.ninos.notification.service;

import com.ninos.auth_users.entity.User;
import com.ninos.notification.dto.NotificationDTO;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO, User user);

}
package com.ninos.auth_users.service.impl;

import com.ninos.auth_users.dto.UpdatePasswordRequest;
import com.ninos.auth_users.dto.UserDTO;
import com.ninos.auth_users.entity.User;
import com.ninos.auth_users.repository.UserRepository;
import com.ninos.auth_users.service.UserService;
import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.notification.dto.NotificationDTO;
import com.ninos.notification.service.NotificationService;
import com.ninos.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    // save images to backend folder
    private final String uploadDir = "uploads/profile-pictures/";

    // save images to frontend folder
//    private final String uploadDir = "C:/Users/ninoo/OneDrive/Desktop/bank-app(springboot-react)/frontEnd/trust-bank-ui/public/profile-picture/";


    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new NotFoundException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
    }


    @Override
    public Response<UserDTO> getMyProfile() {
        User user = getCurrentLoggedInUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retrieved")
                .data(userDTO)
                .build();
    }



    @Override
    public Response<Page<UserDTO>> getAllUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page,size));
        Page<UserDTO> userDTOS = users.map(user -> modelMapper.map(user, UserDTO.class));
        return Response.<Page<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All Users retrieved")
                .data(userDTOS)
                .build();
    }



    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentLoggedInUser();

        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        if(oldPassword == null || newPassword == null){
            throw new BadRequestException("old and new Password are required");
        }

        // Validate old password
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new BadRequestException("Old Password not correct");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // SEND password change confirmation email
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your Password Was Successfully Changed")
                .templateName("password-change")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendEmail(notificationDTO, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password Changed Successfully")
                .build();
    }



    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        User user = getCurrentLoggedInUser(); // John Doe with user ID 12

        try {
            Path uploadPath = Paths.get(uploadDir); // uploadPath → uploads/profile-pictures/

            // If uploads/profile-pictures/ doesn’t exist, the system will make it
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath); // uploads/profile-pictures/
            }

            // John previously had uploads/profile-pictures/old123.jpg
            if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()){
                Path oldFile = Paths.get(user.getProfilePictureUrl());
                if(Files.exists(oldFile)){
                    Files.delete(oldFile); // That file will be deleted before saving the new one.
                }
            }

            // Generate a unique file name to avoid conflicts
            String originalFileName = file.getOriginalFilename(); // myphoto.jpg

            String fileExtension="";
            if(originalFileName != null && originalFileName.contains(".")){
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); // If original file = "myphoto.jpg", then fileExtension = ".jpg"
            }
            String newFileName = UUID.randomUUID() + fileExtension; // New file name could be: "a5f3c7d2-89c4-44cd-92ee-7ff8d49b6bcd.jpg"

            // Combines the directory path with the new file name to get the full path to where the file will be stored.
            Path filePath = uploadPath.resolve(newFileName); // filePath = uploads/profile-pictures/a5f3c7d2-89c4-44cd-92ee-7ff8d49b6bcd.jpg

            Files.copy(file.getInputStream(), filePath); // The user’s photo bytes are saved to uploads/profile-pictures/...jpg

//            String fileUrl = uploadDir + newFileName; // fileUrl = "uploads/profile-pictures/a5f3c7d2-89c4-44cd-92ee-7ff8d49b6bcd.jpg" (this for backend)
            String fileUrl = "profile-picture/" + newFileName; // retrieved path for from frontend (this for frontend)

            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Profile picture uploaded Successfully")
                    .data(fileUrl)
                    .build();

        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }

    }
}

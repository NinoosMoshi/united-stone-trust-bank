package com.ninos.auth_users.service;

import com.ninos.auth_users.dto.UpdatePasswordRequest;
import com.ninos.auth_users.dto.UserDTO;
import com.ninos.auth_users.entity.User;
import com.ninos.response.Response;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    User getCurrentLoggedInUser();
    Response<UserDTO> getMyProfile();
    Response<Page<UserDTO>> getAllUsers(int page, int size);
    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);
    Response<?> uploadProfilePicture(MultipartFile file);


}


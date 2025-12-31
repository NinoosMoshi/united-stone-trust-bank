package com.ninos.security;

import com.ninos.auth_users.entity.User;
import com.ninos.auth_users.repository.UserRepository;
import com.ninos.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("This " + email + " Is Not Found"));

        return UserDetailsImpl.builder()
                .user(user)
                .build();
    }
}

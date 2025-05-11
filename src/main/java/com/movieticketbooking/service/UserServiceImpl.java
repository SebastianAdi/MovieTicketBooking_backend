package com.movieticketbooking.service;

import com.movieticketbooking.model.User;
import com.movieticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String username) {
        return userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email : " + username));
    }
}

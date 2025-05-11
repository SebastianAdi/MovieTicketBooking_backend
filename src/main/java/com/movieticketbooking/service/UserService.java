package com.movieticketbooking.service;

import com.movieticketbooking.model.User;

public interface UserService {

    User findByEmail(String username);
}

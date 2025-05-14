package com.staypick.staypick_back.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import com.staypick.staypick_back.repository.UserRepository;

@Controller
public class MainController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

}

package com.bayu.billingservice.service;

import com.bayu.billingservice.dto.user.UserDTO;
import com.bayu.billingservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> fetchDataByCustomCondition(String customCondition) {
        return userRepository.findBySomeCustomCondition(customCondition);
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll();
    }

}

package com.bayu.billingservice;

import com.bayu.billingservice.dto.user.UserDTO;
import com.bayu.billingservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void getAll() {
        List<UserDTO> userDTOList = userService.getAll();

        log.info("Size : {}", userDTOList.size());

        for (UserDTO userDTO : userDTOList) {
            log.info("ID : {}", userDTO.getId());
            log.info("Name : {}", userDTO.getName());
            log.info("Age : {}", userDTO.getAge());
        }
    }

}

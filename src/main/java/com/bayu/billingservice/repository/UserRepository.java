package com.bayu.billingservice.repository;

import com.bayu.billingservice.dto.user.UserDTO;

import java.util.List;

public interface UserRepository {

    List<UserDTO> findBySomeCustomCondition(String customCondition);

    List<UserDTO> findAll();

    // Tambahkan metode CRUD kustom lainnya jika diperlukan
    // ...

    List<UserDTO> findAll1();
}

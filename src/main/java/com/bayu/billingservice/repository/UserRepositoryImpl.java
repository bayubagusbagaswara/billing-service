package com.bayu.billingservice.repository;

import com.bayu.billingservice.dto.user.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserDTO> findBySomeCustomCondition(String customCondition) {
        // Implementasi pencarian kustom menggunakan EntityManager
        // Misalnya, menggunakan JPQL
        String jpqlQuery = "SELECT u.column1, u.column2, u.column3 FROM UserEntity u WHERE u.someField = :customCondition";
        List<Object[]> resultList = entityManager.createQuery(jpqlQuery, Object[].class)
                .setParameter("customCondition", customCondition)
                .getResultList();

        log.info("Result List : {}", resultList);

        // Mapping hasil query ke DTO, pastikan urutannya sesuai
        return resultList.stream()
                .map(row -> new UserDTO(
                        (Integer) row[0],
                        (String) row[1],
                        (Integer) row[2])
                ) // Ubah tipe data sesuai dengan tipe data yang sebenarnya
                .toList();
    }

    // Implementasi metode CRUD kustom lainnya jika diperlukan
    // ...


    @SuppressWarnings("unchecked")
    @Override
    public List<UserDTO> findAll() {
        String nativeQuery = "SELECT * FROM user";  // Assuming 'user' is the table name
        List<Object[]> resultList = entityManager.createNativeQuery(nativeQuery)
                .getResultList();

        for (int i = 0; i < resultList.size(); i++) {
            Object[] objects = resultList.get(i);
            log.info("Object - " + i + " is {}",  Arrays.stream(objects).toList());
        }

        // Mapping hasil query ke DTO, pastikan urutannya sesuai
        return resultList.stream()
                .map(row -> new UserDTO(
                        ((Number) row[0]).intValue(),  // Assuming id is of type INT
                        (String) row[1],
                        ((Number) row[2]).intValue()   // Assuming age is of type INT
                ))
                .toList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserDTO> findAll1() {
        String nativeQuery = "SELECT * FROM user";
        return entityManager.createNativeQuery(nativeQuery, UserDTO.class)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }
}

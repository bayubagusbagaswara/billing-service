package com.bayu.billingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegexTest {

    @Test
    void regexNumber() {
        String input = "12345";
        boolean validInput = isValidInput(input);
        if (validInput) {
            System.out.println("Input sudah berupa angka");
        } else {
            System.out.println("Input masih ada huruf");
        }
    }

    private boolean isValidInput(String input) {
        // Menggunakan ekspresi reguler untuk memeriksa apakah input hanya berisi angka
        return input.matches("\\d+");
    }
}

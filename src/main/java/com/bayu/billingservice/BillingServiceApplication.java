package com.bayu.billingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
//		Class<MyRestController> clazz = MyRestController.class;
//
//		// Memeriksa apakah kelas memiliki anotasi @RequestMapping
//		if (clazz.isAnnotationPresent(RequestMapping.class)) {
//			RequestMapping classMapping = clazz.getAnnotation(RequestMapping.class);
//			String[] classPaths = classMapping.value(); // Mendapatkan path kelas
//			System.out.println("Class level mapping: " + Arrays.toString(classPaths));
//		}
//
//		// Memeriksa metode-metode di kelas
//		Method[] methods = clazz.getDeclaredMethods();
//		for (Method method : methods) {
//			if (method.isAnnotationPresent(RequestMapping.class)) {
//				RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
//				String[] methodPaths = methodMapping.value(); // Mendapatkan path metode
//				RequestMethod[] requestMethods = methodMapping.method(); // Mendapatkan HTTP method
//
//				System.out.println("Method: " + method.getName());
//				System.out.println("Mapping: " + Arrays.toString(methodPaths) + " (" + Arrays.toString(requestMethods) + ")");
//			}
//		}
	}

}

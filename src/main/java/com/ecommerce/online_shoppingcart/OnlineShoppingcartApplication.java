package com.ecommerce.online_shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ecommerce.online_shoppingcart")
public class OnlineShoppingcartApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineShoppingcartApplication.class, args);
	}

}

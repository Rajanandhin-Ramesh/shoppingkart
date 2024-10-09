package com.ecommerce.online_shoppingcart.repository;

import com.ecommerce.online_shoppingcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    Boolean existsByEmail(String userEmail);
    Boolean existsByUserName(String username);
}

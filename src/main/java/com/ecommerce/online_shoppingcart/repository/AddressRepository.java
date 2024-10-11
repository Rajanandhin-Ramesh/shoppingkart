package com.ecommerce.online_shoppingcart.repository;

import com.ecommerce.online_shoppingcart.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


public interface AddressRepository extends JpaRepository<Address, Long> {
}

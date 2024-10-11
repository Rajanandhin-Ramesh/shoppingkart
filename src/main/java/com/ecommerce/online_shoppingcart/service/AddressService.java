package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.model.User;
import com.ecommerce.online_shoppingcart.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();


    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddress(User user);
}

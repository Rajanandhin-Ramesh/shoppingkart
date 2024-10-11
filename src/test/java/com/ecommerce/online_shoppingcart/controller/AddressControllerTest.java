package com.ecommerce.online_shoppingcart.controller;

import com.ecommerce.online_shoppingcart.model.User;
import com.ecommerce.online_shoppingcart.payload.AddressDTO;
import com.ecommerce.online_shoppingcart.service.AddressService;
import com.ecommerce.online_shoppingcart.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressControllerTest {

    @Mock
    private AuthUtil authUtil;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private AddressDTO addressDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("username", "user@example.com", "password");
        addressDTO = new AddressDTO();
        addressDTO.setStreet("Street 1");
        addressDTO.setCity("City 1");
    }

    @Test
    void createAddress_success() {
        when(authUtil.loggedInUser()).thenReturn(user);
        when(addressService.createAddress(any(AddressDTO.class), eq(user))).thenReturn(addressDTO);

        ResponseEntity<AddressDTO> response = addressController.createAddress(addressDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(addressService, times(1)).createAddress(any(AddressDTO.class), eq(user));
    }

    @Test
    void getAddress_success() {
        when(addressService.getAddresses()).thenReturn(List.of(addressDTO));

        ResponseEntity<List<AddressDTO>> response = addressController.getAddress();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(addressService, times(1)).getAddresses();
    }

    @Test
    void getAddressById_success() {
        when(addressService.getAddressById(anyLong())).thenReturn(addressDTO);

        ResponseEntity<AddressDTO> response = addressController.getAddressById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(addressService, times(1)).getAddressById(anyLong());
    }

    @Test
    void getUserAddress_success() {
        when(authUtil.loggedInUser()).thenReturn(user);
        when(addressService.getUserAddress(user)).thenReturn(List.of(addressDTO));

        ResponseEntity<List<AddressDTO>> response = addressController.getUserAddress();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(addressService, times(1)).getUserAddress(user);
    }

    @Test
    void deleteAddress_success() {
        when(addressService.deleteAddress(anyLong())).thenReturn("Address deleted successfully with addressId: 1");

        ResponseEntity<String> response = addressController.deleteAddress(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Address deleted successfully with addressId: 1", response.getBody());
        verify(addressService, times(1)).deleteAddress(anyLong());
    }
}

package com.ecommerce.online_shoppingcart.service;

import com.ecommerce.online_shoppingcart.exception.ResourceNotFoundException;
import com.ecommerce.online_shoppingcart.model.Address;
import com.ecommerce.online_shoppingcart.model.User;
import com.ecommerce.online_shoppingcart.payload.AddressDTO;
import com.ecommerce.online_shoppingcart.repository.AddressRepository;
import com.ecommerce.online_shoppingcart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address;
    private User user;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        address = new Address(1L, "Street 1", "Building 1", "City 1", "State 1", "Country 1", "123456");
        user = new User("username", "user@example.com", "password");
        user.setAddresses(List.of(address));

        addressDTO = new AddressDTO();
        addressDTO.setStreet("Street 1");
        addressDTO.setCity("City 1");
    }

    @Test
    void createAddress_success() {
        // Setup the user with an empty address list
        User user = new User();
        user.setUserId(1L);
        List<Address> addresses = new ArrayList<>();
        user.setAddresses(addresses);  // Initially, the user has no addresses

        // Mock the mapping of AddressDTO to Address
        when(modelMapper.map(addressDTO, Address.class)).thenReturn(address);

        // Mock the saving of the address in the repository
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // Mock the mapping of the saved address back to AddressDTO
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(addressDTO);

        // Call the service method
        AddressDTO result = addressService.createAddress(addressDTO, user);

        // Assertions
        assertNotNull(result);

        // Ensure that the address is saved
        verify(addressRepository, times(1)).save(any(Address.class));

        // Assert that the address is added to the user's address list
        assertTrue(user.getAddresses().contains(address));

        // Optionally, check that the user now has one address in the list
        assertEquals(1, user.getAddresses().size());
    }


    @Test
    void getAddresses_success() {
        List<Address> addresses = List.of(address);
        when(addressRepository.findAll()).thenReturn(addresses);
        when(modelMapper.map(any(Address.class), eq(AddressDTO.class))).thenReturn(addressDTO);

        // When
        List<AddressDTO> result = addressService.getAddresses();

        // Then
        assertEquals(1, result.size());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void getAddressById_success() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(addressDTO);

        AddressDTO result = addressService.getAddressById(1L);

        assertNotNull(result);
        verify(addressRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAddressById_notFound() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.getAddressById(1L);
        });
    }
    // Sample Mockito setup for your test case
    @Test
    public void updateAddress_success() {
        // Create a mock user and address
        User mockUser = new User();
        mockUser.setUserId(1L); // Set a valid user ID
        mockUser.setAddresses(new ArrayList<>());

        Address mockAddress = new Address();
        mockAddress.setAddressId(1L);
        mockAddress.setUser(mockUser); // Associate the mock user with the address

        // Create an AddressDTO for updating
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity("New City");
        addressDTO.setPinCode("123456");
        addressDTO.setState("New State");
        addressDTO.setCountry("New Country");
        addressDTO.setStreet("New Street");
        addressDTO.setBuildingName("New Building");

        // Mock the repository behavior
        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));
        when(userRepository.save(any(User.class))).thenReturn(mockUser); // Mock the user save

        // Call the updateAddress method
        AddressDTO updatedAddressDTO = addressService.updateAddress(1L, addressDTO);

        // Assert that the result is not null
        assertNotNull(updatedAddressDTO, "not <null>");
        // Additional assertions to check if the updated values are correct
        assertEquals("New City", updatedAddressDTO.getCity());
        assertEquals("123456", updatedAddressDTO.getPinCode());
        // Continue with other assertions as necessary...
    }


    @Test
    void deleteAddress_success() {
        // Mock an Address entity with an associated user
        User user = new User();
        user.setUserId(1L);
        Address address = new Address();
        address.setAddressId(1L);
        address.setUser(user);

        // Mock the user having an address list
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        user.setAddresses(addresses);

        // Mock the findById method for addressRepository
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));

        // Mock the userRepository save method
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the service method
        String result = addressService.deleteAddress(1L);

        // Assertions
        assertEquals("Address deleted successfully with addressId: 1", result);

        // Verify that delete was called once
        verify(addressRepository, times(1)).delete(any(Address.class));

        // Verify that the user's address list is updated and userRepository save is called once
        verify(userRepository, times(1)).save(any(User.class));
    }
}

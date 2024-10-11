package com.ecommerce.online_shoppingcart.controller;

import com.ecommerce.online_shoppingcart.model.AppRole;
import com.ecommerce.online_shoppingcart.model.Role;
import com.ecommerce.online_shoppingcart.model.User;
import com.ecommerce.online_shoppingcart.repository.RoleRepository;
import com.ecommerce.online_shoppingcart.repository.UserRepository;
import com.ecommerce.online_shoppingcart.security.jwt.JwtUtils;
import com.ecommerce.online_shoppingcart.security.request.LoginRequest;
import com.ecommerce.online_shoppingcart.security.request.SignupRequest;
import com.ecommerce.online_shoppingcart.security.response.MessageResponse;
import com.ecommerce.online_shoppingcart.security.response.UserInfoResponse;
import com.ecommerce.online_shoppingcart.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateUser_Success() {
        LoginRequest loginRequest = new LoginRequest("userName", "password");
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "dummyJwt").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtCookie(userDetails)).thenReturn(jwtCookie);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtCookie.toString(), response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertEquals("testUser", ((UserInfoResponse) response.getBody()).getUsername());
    }

    @Test
    void testAuthenticateUser_Failure() {
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Bad credentials", ((Map<String, Object>) response.getBody()).get("message"));
    }

    @Test
    void testRegisterUser_Success() {
        // Prepare the signup request with an empty role set
        SignupRequest signUpRequest = new SignupRequest("testUser", "test@example.com", Collections.emptySet(), "testPassword");

        // Mock the repository methods to simulate the expected behavior
        when(userRepository.existsByUserName(signUpRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.of(new Role(AppRole.ROLE_USER)));

        // Call the method under test
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Validate the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", ((MessageResponse) response.getBody()).getMessage());
    }


    @Test
    void testRegisterUser_UsernameTaken() {
        SignupRequest signUpRequest = new SignupRequest("testUser", "test@example.com", Collections.emptySet(), "testPassword");
        when(userRepository.existsByUserName(signUpRequest.getUsername())).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Username is already taken!", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void testRegisterUser_EmailTaken() {
        SignupRequest signUpRequest = new SignupRequest("testUser", "test@example.com", Collections.emptySet(), "testPassword");
        when(userRepository.existsByUserName(signUpRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Email is already in use!", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void testGetUserDetails() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = authController.getUserDetails(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", ((UserInfoResponse) response.getBody()).getUsername());
    }

    @Test
    void testSignoutUser() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "").build();
        when(jwtUtils.getCleanJwtCookie()).thenReturn(cookie);

        ResponseEntity<?> response = authController.signoutUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("You've been signed out!", ((MessageResponse) response.getBody()).getMessage());
    }
}

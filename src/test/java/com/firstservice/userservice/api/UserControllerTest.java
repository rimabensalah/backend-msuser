package com.firstservice.userservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstservice.userservice.domain.NotificationPreferences;
import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.payload.request.LoginRequest;
import com.firstservice.userservice.payload.response.JwtResponse;
import com.firstservice.userservice.repository.NotificationPreferencesRepository;
import com.firstservice.userservice.repository.RoleRepository;

import com.firstservice.userservice.repository.UserRepository;
import com.firstservice.userservice.security.jwt.JwtUtils;
import com.firstservice.userservice.service.UserDetailsImpl;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.runner.RunWith;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @InjectMocks
    private UserController userController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepo;

    @Mock
    private NotificationPreferencesRepository notificationPreferencesRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private Utilisateur user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAuthenticateUser() {
        String username = "testuser";
        String password = "testpassword";
        String jwt = "testjwt";
        List<String> roles = Arrays.asList("ROLE_USER");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(authRequest)).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getEmail()).thenReturn("test@example.com");
// Mock the behavior of getAuthorities() method



        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);

        when(userRepo.findById(userDetails.getId())).thenReturn(Optional.of(user));

        NotificationPreferences preferences = new NotificationPreferences(user);
        preferences.setEmailNotificationsEnabled(false);
        when(notificationPreferencesRepository.findByUtilisateur(user)).thenReturn(preferences);

        ResponseEntity<?> responseEntity = userController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        JwtResponse jwtResponse = (JwtResponse) responseEntity.getBody();
        assertNotNull(jwtResponse);
        assertEquals(jwt, jwtResponse.getAccessToken());
        assertEquals(userDetails.getId(), jwtResponse.getId());
        assertEquals(userDetails.getUsername(), jwtResponse.getUsername());
        assertEquals(userDetails.getEmail(), jwtResponse.getEmail());
        //assertEquals(roles, jwtResponse.getRoles());
    }
    @Test
    public void shouldCreateRole() throws Exception {
        Role role = new Role(1L, RoleName.ROLE_USER);

        mockMvc.perform(post("/api/auth/role/save").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void shouldLogin() throws  Exception{
        String username = "admin33";
        String password = "ryma1234";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andDo(print());


    }

    @Test
    public void shouldReturnListOfRoles() throws Exception {
        List<Role> roles = new ArrayList<>(
                Arrays.asList(new Role(1L, RoleName.ROLE_USER),
                        new Role(2L, RoleName.ROLE_ADMIN)
                        ));

        when(roleRepository.findAll()).thenReturn(roles);
        mockMvc.perform(get("/api/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(roles.size()))
                .andDo(print());
    }



}

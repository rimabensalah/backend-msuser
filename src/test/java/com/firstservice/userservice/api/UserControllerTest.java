package com.firstservice.userservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.payload.request.LoginRequest;
import com.firstservice.userservice.repository.NotificationPreferencesRepository;
import com.firstservice.userservice.repository.RoleRepository;
import com.firstservice.userservice.repository.UserRepository;
import com.firstservice.userservice.security.jwt.JwtUtils;
import com.firstservice.userservice.service.UserDetailsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@WebMvcTest(UserController.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateRole() throws Exception {
        Role role = new Role(1L, RoleName.ROLE_USER);

        mockMvc.perform(post("/api/auth/role/save").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isCreated())
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

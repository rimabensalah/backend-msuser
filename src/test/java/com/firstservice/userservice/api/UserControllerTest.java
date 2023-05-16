package com.firstservice.userservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;
import com.firstservice.userservice.repository.RoleRepository;

import org.junit.jupiter.api.Test;
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

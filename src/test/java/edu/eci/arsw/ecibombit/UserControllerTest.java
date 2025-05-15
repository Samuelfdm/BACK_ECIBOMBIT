package edu.eci.arsw.ecibombit;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.eci.arsw.ecibombit.controller.UserController;
import edu.eci.arsw.ecibombit.dto.UserDTO;
import edu.eci.arsw.ecibombit.model.UserAccount;
import edu.eci.arsw.ecibombit.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SpringBootApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Autowired
    private ObjectMapper objectMapper;
/*
 * 
 * @Test
    public void shouldReturnBadRequestIfValidationFails() throws Exception {
        UserDTO userDTO = new UserDTO(); // All fields null or empty

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }
 */
   


    @Test
    public void shouldRegisterNewUserSuccessfully() throws Exception {
        UserDTO userDTO = new UserDTO("1234", "John Doe", "john@example.com");
        UserAccount newUser = new UserAccount();
        newUser.setOid("1234");
        newUser.setUsername("John Doe");
        newUser.setEmail("john@example.com");

        when(loginService.loginOrRegister(any(UserDTO.class))).thenReturn(newUser);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oid").value("1234"))
                .andExpect(jsonPath("$.username").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void shouldHandleIllegalArgumentException() throws Exception {
        UserDTO userDTO = new UserDTO("1234", "Invalid User", "invalid@example.com");

        when(loginService.loginOrRegister(any(UserDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid email"));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldHandleUnexpectedException() throws Exception {
        UserDTO userDTO = new UserDTO("1234", "Error User", "error@example.com");

        when(loginService.loginOrRegister(any(UserDTO.class)))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldReturnNotFoundIfUserIsNull() throws Exception {
        UserDTO userDTO = new UserDTO("9999", "Ghost", "ghost@example.com");

        when(loginService.loginOrRegister(any(UserDTO.class))).thenReturn(null);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }
}
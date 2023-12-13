package cl.petrasic.usercreator.controller;

import cl.petrasic.usercreator.datos.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import cl.petrasic.usercreator.controller.ExceptionHandlerController;
import cl.petrasic.usercreator.controller.UserController;
import cl.petrasic.usercreator.domain.User;
import cl.petrasic.usercreator.dto.UserDTO;
import cl.petrasic.usercreator.exceptions.ConstraintsException;
import cl.petrasic.usercreator.exceptions.UserAlreadyExistException;
import cl.petrasic.usercreator.repository.PhoneRepository;
import cl.petrasic.usercreator.repository.UserRepository;
import cl.petrasic.usercreator.service.UserServiceImpl;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("User controller tests")
class UserControllerTest {

    private MockMvc mvc;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PhoneRepository phoneRepository;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        this.mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Registro")
    void singU1pTest() throws Exception {

        when(userService.userRegister(any())).thenReturn(Data.createUserResponseDTO());

        UserDTO userDTO = Data.createUserDTO1().get();
        mvc.perform(post("/users/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.name").value("Charlie"))
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.user.email").value("charlie_01@correo.com"));
    }

    @Test
    @DisplayName("Registro - User Already exists")
    void singUp2Test() throws Exception {
        UserDTO userDTO = Data.createUserDTO1().get();
        when(userService.userRegister(any())).thenThrow(
                new UserAlreadyExistException("There is already a user with the email " + userDTO.getEmail()));

        mvc.perform(post("/users/sing-up").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.detail").value("There is already a user with the email " + userDTO.getEmail()));

    }

    @Test
    @DisplayName("Registro - ConstraintException 1")
    void singUpException1Test() throws Exception {

        UserDTO userDTO = Data.createUserDTO1().get();
        userDTO.setPassword("nocapsgg12");

        when(userService.userRegister(any())).thenThrow(
                new ConstraintsException("Password must have a capital letter"));

        mvc.perform(post("/users/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.detail").value("Password must have a capital letter"));

    }

    @Test
    @DisplayName("Registro - ConstraintException 2")
    void singUpException2Test() throws Exception {

        UserDTO userDTO = Data.createUserDTO1().get();

        when(userService.userRegister(any())).thenThrow(
                new ConstraintsException("Password must have two numbers"));

        userDTO.setPassword("Nonumbers");
        mvc.perform(post("/users/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.detail").value("Password must have two numbers"));
    }

    @Test
    @DisplayName("Registro - MethodArgumentNotValidException 1")
    void singUpException3Test() throws Exception {

        UserDTO userDTO = Data.createUserDTO1().get();
        userDTO.setPassword("Numb12");
        mvc.perform(post("/users/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[0].detail").value("password : The password must be between 8 and 12 characters"));

    }

    @Test
    @DisplayName("Registro - MethodArgumentNotValidException 2")
    void singUpException4Test() throws Exception {

        UserDTO userDTO = Data.createUserDTO1().get();
        userDTO.setPassword("Numb12");
        userDTO.setEmail("test_correo.com");
        userDTO.getPhones().get(0).setCountryCode(null);
        userDTO.getPhones().get(0).setNumber(null);
        userDTO.getPhones().get(1).setNumber(null);
        userDTO.getPhones().get(1).setCityCode(null);
        mvc.perform(post("/users/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[0].detail").exists())
                .andExpect(jsonPath("$[1].code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[1].timestamp").exists())
                .andExpect(jsonPath("$[1].detail").exists());
    }


    @Test
    @DisplayName("Login")
    void loginTest() throws Exception {
        UUID id = UUID.fromString("f0e8eebb-fb26-4fbd-aaa2-6a365f6077dc");
        String token = "Token";
        when(userService.login(token)).thenReturn(Data.createLoginResponse());

        mvc.perform(get("/users/login").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("charlie_01@correo.com"));

    }

    @Test
    @DisplayName("Login failed")
    void loginFailedTest() throws Exception {
        UUID id = UUID.fromString("f0e8eebb-fb26-4fbd-aaa2-6a365f6077dc");
        String token = "Token expirado";
        when(userService.login(token)).thenThrow(new UsernameNotFoundException("Username not found"));

        mvc.perform(get("/users/login").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.detail").value("Username not found"));

    }
}

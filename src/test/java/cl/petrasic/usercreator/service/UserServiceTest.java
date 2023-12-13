package cl.petrasic.usercreator.service;

import cl.petrasic.usercreator.datos.Data;

import cl.petrasic.usercreator.domain.User;
import cl.petrasic.usercreator.dto.LoginResponseDTO;
import cl.petrasic.usercreator.dto.UserDTO;
import cl.petrasic.usercreator.dto.UserResponseDTO;
import cl.petrasic.usercreator.exceptions.ConstraintsException;
import cl.petrasic.usercreator.exceptions.UserAlreadyExistException;
import cl.petrasic.usercreator.mapper.UserMapper;
import cl.petrasic.usercreator.repository.PhoneRepository;
import cl.petrasic.usercreator.repository.UserRepository;
import cl.petrasic.usercreator.security.service.UserDetailsServiceImpl;
import cl.petrasic.usercreator.security.utils.JWTUtils;
import cl.petrasic.usercreator.service.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test service users")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, phoneRepository,
                new UserDetailsServiceImpl(userRepository), jwtUtils,
                new UserMapper(new BCryptPasswordEncoder(), new ModelMapper()));
    }

    @Test
    @DisplayName("Bad password 1")
    void passwordTest1() {

        UserDTO userDTO = Data.createUserDTO1().get();
        userDTO.setPassword("nocapitallet");
        ConstraintsException ex = assertThrows(ConstraintsException.class, () -> userService.userRegister(userDTO));
        assertTrue(ex.getMessage().contains("Password must have a capital letter"));

        userDTO.setPassword("Nonumbers");
        ex = assertThrows(ConstraintsException.class, () -> userService.userRegister(userDTO));
        assertTrue(ex.getMessage().contains("Password must have two numbers"));

        userDTO.setPassword("Just0nenum");
        ex = assertThrows(ConstraintsException.class, () -> userService.userRegister(userDTO));
        assertTrue(ex.getMessage().contains("Password must have two numbers"));

        userDTO.setPassword("TwOcaplet");
        ex = assertThrows(ConstraintsException.class, () -> userService.userRegister(userDTO));
        assertTrue(ex.getMessage().contains("Password must have a capital letter"));

        userDTO.setPassword("Thr33numb3");
        ex = assertThrows(ConstraintsException.class, () -> userService.userRegister(userDTO));
        assertTrue(ex.getMessage().contains("Password must have two numbers"));

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    @DisplayName("Save user")
    void saveUserTest() {

        User user = Data.createUser1().orElse(null);
        when(userRepository.findByEmail(any())).thenReturn(Data.createUser1());
        when(phoneRepository.saveAll(any())).thenReturn(Data.createPhoneList(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDTO userDTO = Data.createUserDTO1().orElse(null);
        UserResponseDTO savedUSer = userService.userRegister(userDTO);

        assertAll(
                () -> assertNotNull(savedUSer),
                () -> assertEquals(user.getEmail(), savedUSer.getUser().getEmail()),
                () -> assertEquals(user.getName(), savedUSer.getUser().getName()),
                () -> assertEquals(user.getPassword(), savedUSer.getUser().getPassword()),
                () -> assertEquals(user.getRole(), savedUSer.getUser().getRole()),
                () -> assertEquals(user.getCreateAt(), savedUSer.getUser().getCreateAt()),
                () -> assertEquals(user.getLastLogin(), savedUSer.getUser().getLastLogin()),
                () -> assertEquals(user.isActive(), savedUSer.getUser().isActive())
        );

        verify(userRepository).findByEmail(any());
        verify(userRepository).save(any(User.class));

    }

    @Test
    @DisplayName("Existing User")
    void existingUserTest() {

        when(userRepository.existsByEmail(any())).thenReturn(true);

        UserDTO userDTO = Data.createUserDTO1().orElse(null);
        UserAlreadyExistException ex = assertThrows(UserAlreadyExistException.class, () -> userService.userRegister(userDTO));
        assertEquals("There is already a user with the email " + userDTO.getEmail(), ex.getMessage());

        verify(userRepository).existsByEmail(any(String.class));
        verify(userRepository, never()).findByEmail(any(String.class));
        verify(userRepository, never()).saveAndFlush(any(User.class));

    }

    @Test
    @DisplayName("Login test")
    void loginTest() {
        User user = Data.createUser1().orElse(null);
        when(userRepository.findByEmail(any())).thenReturn(Data.createUser1());
        when(userRepository.save(any())).thenReturn(user);
        when(phoneRepository.findByUser(any())).thenReturn(Data.createPhoneList(user));
        when(jwtUtils.generateToken(any())).thenReturn("token nuevo");

        LoginResponseDTO loginResponseDTO = userService.login("token actual");

        assertAll(
                () -> assertEquals("Charlie", loginResponseDTO.getName()),
                () -> assertEquals("charlie_01@correo.com", loginResponseDTO.getEmail()),
                () -> assertEquals("token nuevo", loginResponseDTO.getToken()),
                () -> assertTrue(loginResponseDTO.isActive()),
                () -> assertEquals(user.getCreateAt(), loginResponseDTO.getCreated()),
                () -> assertNotNull(loginResponseDTO.getPhones()),
                () -> assertEquals(2, loginResponseDTO.getPhones().size()),
                () -> assertEquals(345790145, loginResponseDTO.getPhones().get(0).getNumber()),
                () -> assertEquals(261, loginResponseDTO.getPhones().get(0).getCityCode()),
                () -> assertEquals("+54", loginResponseDTO.getPhones().get(0).getCountryCode())
        );
    }
}

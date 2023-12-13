package cl.petrasic.usercreator.service;

import cl.petrasic.usercreator.domain.Phone;
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
import cl.petrasic.usercreator.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTUtils jwtUtils;
    private final UserMapper userMapper;

    public UserResponseDTO userRegister(UserDTO userDTO) {
        log.info("Registrando usuario {}", userDTO);
        UserResponseDTO response = new UserResponseDTO();
        checkPassword(userDTO.getPassword());
        User user = saveUser(userDTO);
        log.info("Usuario registrado: {}", user);
        response.setUser(user);
        response.setToken(createToken(user));
        return response;
    }

    private User saveUser(UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistException("El correo ya registrado " + user.getEmail());
        }
        List<Phone> phones = userDTO.getPhones().stream()
                .map(p -> userMapper.toPhone(p, user))
                        .collect(Collectors.toList());
        User saveUser = userRepository.save(user);
        phoneRepository.saveAll(phones);
        return saveUser;
    }

    private void checkPassword(String password) {
        if (!checkCapitalsAmount(password)) {
            throw new ConstraintsException("Password debe tener una letra mayúscula");
        }
        if (!checkNumbersAmount(password)) {
            throw new ConstraintsException("Password debe tener dos números");
        }
    }

    private boolean checkCapitalsAmount(String password) {
        Matcher matcher = evaluateRegex(password, Constants.CAPITAL_REGEX);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count == 1;
    }

    private boolean checkNumbersAmount(String password) {
        Matcher matcher = evaluateRegex(password, Constants.NUMBER_REGEX);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count == 2;
    }
    private Matcher evaluateRegex(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(content);
    }

    public LoginResponseDTO login(String token) {
        String username = jwtUtils.getUsername(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        List<Phone> phones = phoneRepository.findByUser(user);
        String newToken = createToken(user);
        user.setLastLogin(LocalDateTime.now());
        return userMapper.createLoginResponse(userRepository.save(user), phones, newToken);
    }

    private String createToken(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtUtils.generateToken(userDetails);
    }

}

package cl.petrasic.usercreator.service;

import cl.petrasic.usercreator.dto.LoginResponseDTO;
import cl.petrasic.usercreator.dto.UserDTO;
import cl.petrasic.usercreator.dto.UserResponseDTO;


public interface UserService {
    UserResponseDTO userRegister(UserDTO userDTO);
    LoginResponseDTO login(String token);
}

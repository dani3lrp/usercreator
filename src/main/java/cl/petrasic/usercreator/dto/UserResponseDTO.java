package cl.petrasic.usercreator.dto;

import cl.petrasic.usercreator.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private User user;
    private String token;
}

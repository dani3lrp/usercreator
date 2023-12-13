package cl.petrasic.usercreator.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserDTO {

    private String name;

    @NotNull(message = "The email cannot be empty")
    @Email(regexp = ".+@.+\\..+", message = "Email con formato incorrecto")
    private String email;

    @NotNull(message = "The password cannot be empty")
    @Size(min = 8, max = 12, message = "Password debe contener entre 8 a 12 caracteres")
    private String password;

    private List<@Valid PhoneDTO> phones;
}

package org.tnf.concurrentframework.seckill.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.tnf.concurrentframework.seckill.model.User;

@Data
public class UserDTO {
    @NotBlank
    private String username;
    @Email
    private String email;
    private String password;

    UserDTO() {

    }

    UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }
}
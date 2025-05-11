package org.tnf.concurrentframework.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.tnf.concurrentframework.example.model.User;

@Data
public class UserDTO {
    @NotBlank
    private String username;
    @Email
    private String email;
    private String password;
    private String token;
    private String id;

    UserDTO() {

    }

    UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }
}
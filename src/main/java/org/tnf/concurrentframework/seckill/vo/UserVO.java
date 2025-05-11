package org.tnf.concurrentframework.seckill.vo;

import lombok.Data;
import org.tnf.concurrentframework.seckill.model.User;

@Data
public class UserVO {
    private String id;
    private String username;
    private String email;

    public UserVO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

}
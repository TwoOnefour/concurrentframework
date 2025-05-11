package org.tnf.concurrentframework.example.vo;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.tnf.concurrentframework.example.model.User;

@Data
public class UserVO {
    private String id;
    private String username;
    private String email;
    private String sessionId;
    public UserVO() {

    }

    public UserVO(User user) {
        BeanUtils.copyProperties(user, this);
    }

}


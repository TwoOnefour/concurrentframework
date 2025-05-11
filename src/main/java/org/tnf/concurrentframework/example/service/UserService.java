package org.tnf.concurrentframework.example.service;

import org.tnf.concurrentframework.example.dto.UserDTO;
import org.tnf.concurrentframework.example.vo.UserVO;

public interface UserService {
    UserVO register(UserDTO dto);

    UserVO getUserById(String id);

    UserVO createUser(UserDTO user);

    UserVO login(UserDTO user);

    void logout(String sessionId);

    UserVO getUserByToken(String token);
}

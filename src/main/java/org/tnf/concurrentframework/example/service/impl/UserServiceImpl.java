package org.tnf.concurrentframework.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.tnf.concurrentframework.example.dao.UserMapper;
import org.tnf.concurrentframework.example.dto.UserDTO;
import org.tnf.concurrentframework.example.model.User;
import org.tnf.concurrentframework.example.service.UserService;
import org.tnf.concurrentframework.example.utils.JwtUtils;
import org.tnf.concurrentframework.example.vo.UserVO;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

// UserServiceImpl.java
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserVO register(UserDTO dto) {
        User user = userMapper.selectByUserName(dto.getUsername());
        if (user != null) {
            throw new RuntimeException("User already exists");
        }
        if (userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        return createUser(dto);
    }

    @Override
    public UserVO getUserById(String id) {
        User user = userMapper.selectById(id);
        return user != null ? new UserVO(user) : null;
    }

    @Override
    public UserVO createUser(UserDTO user) {
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        newUser.setId(UUID.randomUUID().toString());
        userMapper.insert(newUser);
        return new UserVO(newUser);
    }

    @Override
    public UserVO login(UserDTO user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new RuntimeException("Username or password cannot be empty");
        }
        User existingUser = userMapper.selectByUserName(user.getUsername());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        long randomExpireTime = System.currentTimeMillis() + 3600 * 1000 +
                (long) (Math.random() * 1000 * 60);
        String jwt = JwtUtils.generateToken(existingUser.getId(), randomExpireTime);
        redisTemplate
                .opsForValue()
                .set("user:jwt:" + jwt, existingUser, randomExpireTime, TimeUnit.MINUTES);

        return new UserVO(existingUser);
    }

    @Override
    public void logout(String jwt) {
        redisTemplate.delete("user:jwt:" + jwt);
    }

    public UserVO getUserByToken(String token) {
        if (token == null || !JwtUtils.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }
        String userId = JwtUtils.getUserId(token);
        User user = (User) redisTemplate.opsForValue().get("user:jwt:" + userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return new UserVO(user);
    }

}
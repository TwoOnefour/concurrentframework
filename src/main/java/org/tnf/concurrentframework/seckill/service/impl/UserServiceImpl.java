package org.tnf.concurrentframework.seckill.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.tnf.concurrentframework.seckill.dao.UserMapper;
import org.tnf.concurrentframework.seckill.dto.UserDTO;
import org.tnf.concurrentframework.seckill.model.User;
import org.tnf.concurrentframework.seckill.service.UserService;
import org.tnf.concurrentframework.seckill.vo.UserVO;

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
        User user = userMapper.selectByName(dto.getUsername());
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

        redisTemplate.opsForValue().get("user:token:" + user.);
        User existingUser = userMapper.selectByName(user.getUsername());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        redisTemplate.opsForValue().set("user:cache:" + userId, user, 30, TimeUnit.MINUTES);
        String token = UUID.randomUUID().toString();
        int randomExpireTime = (int) (Math.random() * 30 + 1);

        redisTemplate.opsForValue()
                .set("user:token:" + token, existingUser.getId(), 30 + randomExpireTime, TimeUnit.MINUTES);
        return new UserVO(existingUser);
    }

    public UserVO getUserFromRedis(UserDTO user) {

    }
}
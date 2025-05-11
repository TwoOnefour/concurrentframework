package org.tnf.concurrentframework.seckill.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.tnf.concurrentframework.seckill.dao.UserMapper;
import org.tnf.concurrentframework.seckill.dto.UserDTO;
import org.tnf.concurrentframework.seckill.model.User;
import org.tnf.concurrentframework.seckill.service.UserService;
import org.tnf.concurrentframework.seckill.vo.UserVO;

import java.util.UUID;

// UserServiceImpl.java
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserVO register(UserDTO dto) {
        User user = userMapper.selectByName(dto.getUsername());
        if (user != null) {
            throw new RuntimeException("User already exists");
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
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(newUser, userVO);
        return userVO;
    }


}
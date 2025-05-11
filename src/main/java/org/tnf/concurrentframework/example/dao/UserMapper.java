package org.tnf.concurrentframework.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.tnf.concurrentframework.example.model.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUserName(@NotBlank String username);

    User selectByEmail(@NotBlank String email);

}
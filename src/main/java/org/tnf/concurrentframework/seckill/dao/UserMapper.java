package org.tnf.concurrentframework.seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.tnf.concurrentframework.seckill.model.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUserName(@NotBlank String username);

    User selectByEmail(@NotBlank String email);

}
package org.tnf.concurrentframework.seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.tnf.concurrentframework.seckill.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
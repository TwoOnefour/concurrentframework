package com.tnf.concurrentframework.seckill.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("users")
public class User {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String username;
    private String email;
}
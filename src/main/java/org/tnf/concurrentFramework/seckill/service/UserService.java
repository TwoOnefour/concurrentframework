package org.tnf.concurrentframework.seckill.service;

import org.tnf.concurrentframework.seckill.dto.UserDTO;
import org.tnf.concurrentframework.seckill.vo.UserVO;

public interface UserService {
    UserVO register(UserDTO dto);

    UserVO getUserById(String id);


}

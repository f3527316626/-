package com.easy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.bean.User;
import  com.easy.mapper.*;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

}

package com.easy.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easy.bean.User;
import com.easy.service.UserService;
import com.easy.util.PasswordUtil;
import com.easy.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/")
    public Result add(@RequestBody User user){
        //将密码进行加密，其他的信息不变
        user.setPassword(PasswordUtil.encodePassword(PasswordUtil.DEFAULT_PASSWORD));
        userService.save(user);//将数据保存到数据库当中
        user=userService.getById(user.getId());//通过id更新数据库
        return Result.success("新增数据成功",user);
    }
    @PutMapping("/")
    public Result edit(@RequestBody User user){
        userService.updateById(user);
        user=userService.getById(user.getId());
        return Result.success("编辑数据成功",user);
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id){
        userService.removeById(id);
        return Result.success("删除数据成功");
    }
    //获取单个数据
    @GetMapping("/{id}")
    public Result getById(@PathVariable int id){
        User user=userService.getById(id);
        return Result.success("",user);
    }
    //分页查询获取多个数据
    @GetMapping("/")
    public Result getPage(Page page,@RequestParam(value = "searchtext",required = false) String searchtext){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper();
        if(searchtext!=null) {
            lambdaQueryWrapper.like(User::getUsername, searchtext);
            lambdaQueryWrapper.or();
            lambdaQueryWrapper.like(User::getNickname, searchtext);
        }
        //select * from user where username=serchtext or nickname=searchtext
        //更新数据
        page=userService.page(page,lambdaQueryWrapper);
        return Result.success("",page);
    }
    //更新图像的方法
    @PutMapping("/avatar/{id}")
    public Result updateAvatar(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        System.out.println("更新头像请求 - ID: " + id + ", 请求体: " + request);
        // 根据ID获取用户
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        //从请求体（前端URL）中获取头像地址
        String imageUrl = request.get("image");
        //验证头像地址是否为空
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return Result.error("头像地址不能为空");
        }
        //设置头像地址
        user.setImage(imageUrl);
        //更新用户信息，利用service调用mapper操作数据库
        boolean success = userService.updateById(user);
        if (success) {
            System.out.println("头像更新成功，用户ID: " + id + ", 头像路径: " + imageUrl);
            return Result.success("头像更新成功", user);
        } else {
            return Result.error("头像更新失败");
        }
    }
    //更新用户基本信息（不包含密码）
    @PutMapping("/basic/{id}")
    public Result updateBasicInfo(@PathVariable Integer id, @RequestBody User userInfo) {
        //首先找到当前的用户（根据id）
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 更新基本信息，但不更新密码
        user.setUsername(userInfo.getUsername());
        user.setEmail(userInfo.getEmail());
        user.setPhone(userInfo.getPhone());
        user.setGender(userInfo.getGender());
        user.setNickname(userInfo.getNickname());
        user.setImage(userInfo.getImage());
        //执行更新操作
        userService.updateById(user);
        return Result.success("基本信息更新成功", user);
    }
    //更新用户密码
    @PutMapping("/password/{id}")
    public Result updatePassword(@PathVariable Integer id,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 验证旧密码
        if (!PasswordUtil.encodePassword(oldPassword).equals(user.getPassword())) {
            return Result.error("旧密码错误");
        }
        user.setPassword(PasswordUtil.encodePassword(newPassword));
        userService.updateById(user);
        return Result.success("密码更新成功", user);
    }
    //重置用户密码（管理员功能）
    @PutMapping("/reset-password/{id}")
    public Result resetPassword(@PathVariable Integer id) {
        //使用service服务调用mapper操作根据用户id获取用户的信息
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        //设置为默认密码DEFAULT_PASSWORD（123123）
        user.setPassword(PasswordUtil.encodePassword(PasswordUtil.DEFAULT_PASSWORD));
        //更新数据
        userService.updateById(user);
        return Result.success("密码已重置为默认密码", user);
    }
    //更新用户最后登录时间
    @PutMapping("/login-time/{id}")
    public Result updateLoginTime(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setLast_login_time(LocalDateTime.now());
        userService.updateById(user);
        return Result.success("登录时间已更新", user);
    }
    //批量查询用户（根据ID列表）
    @PostMapping("/batch")
    public Result getUsersByIds(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.success("", new ArrayList<>());
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, ids);
        List<User> users = userService.list(queryWrapper);
        return Result.success("", users);
    }
    //条件查询用户（多条件）
    @PostMapping("/query")
    public Result queryUsers(@RequestBody User queryParams) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (queryParams.getUsername() != null) {
            queryWrapper.like(User::getUsername, queryParams.getUsername());
        }
        if (queryParams.getEmail() != null) {
            queryWrapper.like(User::getEmail, queryParams.getEmail());
        }
        if (queryParams.getPhone() != null) {
            queryWrapper.like(User::getPhone, queryParams.getPhone());
        }
        if (queryParams.getNickname() != null) {
            queryWrapper.like(User::getNickname, queryParams.getNickname());
        }
        if (queryParams.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryParams.getStatus());
        }
        if (queryParams.getGender() != null) {
            queryWrapper.eq(User::getGender, queryParams.getGender());
        }
        List<User> users = userService.list(queryWrapper);
        return Result.success("", users);
    }
    //统计用户数量
    @GetMapping("/count")
    public Result getUserCount(@RequestParam(value = "status", required = false) Integer status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        long count = userService.count(queryWrapper);
        return Result.success("", count);
    }
}
package com.easy.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easy.bean.Admin;
import com.easy.service.AdminService;
import com.easy.util.PasswordUtil;
import com.easy.util.Result;
import com.easy.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@CrossOrigin
@RestController
@RequestMapping("adminlogin")
public class AdminLoginController {
    @Autowired
    AdminService adminService;
    @PostMapping("dologin")
    //@RequestBody将JSON格式的请求体转换为Admin对象
    public Result doLogin(@RequestBody Admin admin){
        //根据账号查询管理员的信息
        LambdaQueryWrapper<Admin> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        // 设置查询条件：adminname字段等于传入的用户名
        lambdaQueryWrapper.eq(Admin::getAdminname,admin.getAdminname());
        //执行查询，获取数据库中的管理员信息
        Admin admin_db=adminService.getOne(lambdaQueryWrapper);
        // 检查是否查询到管理员，如果管理员的信息不为空
        if(admin_db!=null) {
            //比对密码，admin.getPassword是前端传过来的密码，admin_db.getPassword是数据库中的密码
            if(PasswordUtil.equalsPassword(admin.getPassword(),admin_db.getPassword())){
                //生成管理员的token
                String token= TokenUtil.generateAdminToken(admin_db.getId().toString());
                return Result.success("管理员登录成功",token);
            }
        }
        return Result.fail("管理员登录失败，请重新尝试");
    }
    @GetMapping("isadminlogin")
    public Result isAdminLogin(HttpServletRequest request){
        //获取登录用户的id
        int id=TokenUtil.getLoginAdminID(request);
        //如果没有id说明管理员没有登录或者登录无效
        if(id<0){
            return Result.success("",false);
        }
        else{
            return Result.success("",true);
        }
    }
    @GetMapping("getloginadmin")
    public Result getLoginAdmin(HttpServletRequest request){
        //获取登录用户的id
        int id=TokenUtil.getLoginAdminID(request);
        //根据id去数据库中查找管理员的所有信息
        Admin admin=adminService.getById(id);
        //清空密码字段，避免泄露给前端
        admin.setPassword("");
        return Result.success("",admin);
    }
}
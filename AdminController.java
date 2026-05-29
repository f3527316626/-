package com.easy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easy.bean.Admin;
import com.easy.service.AdminService;
import com.easy.util.PasswordUtil;
import com.easy.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;  // 添加这行导入

@CrossOrigin
@RestController//声明这是一个RESTful风格的控制器，返回JSON数据，不需要再单独定义一个ResponseBody，直接返回JSON数据，等于controller+ResponseBody
@RequestMapping("admin")//所有接口路径都以"/admin"开头
public class AdminController {
    //自动注入Service类，使得能够自动查找Service,使用Service来调用mapper进而对数据库进行修改
    @Autowired
    AdminService adminService;
    @PostMapping("/")//实际路径是/admin/，处理的是POST请求
    //@RequestBody表示接收JSON格式的请求体
    public Result add(@RequestBody Admin admin){
        //使用工具类util中的加密函数，对设置的密码进行加密
        admin.setPassword(PasswordUtil.encodePassword(PasswordUtil.DEFAULT_PASSWORD));
        adminService.save(admin); //保存管理员到数据库
        admin=adminService.getById(admin.getId());//重新查询获取完整信息（包括数据库生成的id等）
        return Result.success("新增数据成功",admin);//返回成功结果和新增的管理员数据
    }
    @PutMapping("/")//同上，处理的是PUT请求
    public Result edit(@RequestBody Admin admin){
        adminService.updateById(admin);//根据id更新管理员信息，id是寻找那个对应管理员的一个键
        admin=adminService.getById(admin.getId()); //重新查询获取更新后的数据
        return Result.success("编辑数据成功",admin);
    }
    @DeleteMapping("/{id}")//处理Delete请求，路径为"/admin/{id}"，{id}是路径参数
    //PathVatiable是将前端URL？后面的id转化成整数，然后拼接到DeleteMapping当中
    //比如说前端是http://localhost:5173/shop/products/brand/9，就将9提取出来然后赋值给上面的路径
    public Result delete(@PathVariable int id){//@PathVariable获取路径中的id参数
        adminService.removeById(id);//根据id删除管理员
        return Result.success("删除数据成功");
    }
    //查询单个管理员的信息
    //http://localhost:5173/main/productedit?id=1，是指的？后面的id，将它转化成整数，赋值给{id}
    @GetMapping("/{id}")//处理的是Get请求，路径为"/admin/{id}"
    public Result getById(@PathVariable int id){
        Admin admin=adminService.getById(id);//根据id查询管理员
        return Result.success("",admin);//返回查询结果
    }
    //查询所有管理员的信息
    @GetMapping("/")
    //page是分页参数，MyBatis Plus的Page对象
    //@RequestParam获取查询参数，required=false表示非必填，从HTTP请求的查询参数（URL 问号后的部分）中提取值并绑定到方法参数上。
    // GET /category/?searchtext=手机&page=1&size=10，提取1和10，传入到getPage(1,10,searchtext)
    public Result getPage(Page page,@RequestParam(value = "searchtext",required = false) String searchtext){
        LambdaQueryWrapper<Admin> lambdaQueryWrapper=new LambdaQueryWrapper();
        if(searchtext!=null) {
            // 传统方式（容易写错字段名）
            //new QueryWrapper<Admin>().like("adminname", searchtext);
            // Lambda方式（IDE自动提示，编译时检查），可以防止SQL注入问题
            new LambdaQueryWrapper<Admin>().like(Admin::getAdminname, searchtext);
            //eq是等值查询，like是模糊查询
            // 在adminname上进行模糊查询等价SQL: WHERE adminname LIKE '%searchtext%'
            lambdaQueryWrapper.like(Admin::getAdminname, searchtext);
            //作用：将后续条件用OR连接，而不是默认的AND
            lambdaQueryWrapper.or();
            // 在realname上进行模糊查询等价SQL: WHERE realname LIKE '%searchtext%'
            lambdaQueryWrapper.like(Admin::getRealname, searchtext);
            //-- 最终生成的SQL（realname或者admin中只要任意一个包括searchtext就行）
            //SELECT * FROM admin
            //WHERE adminname LIKE '%searchtext%' OR realname LIKE '%searchtext%';
        }
        //调用service执行分页查询
        page=adminService.page(page,lambdaQueryWrapper);
        return Result.success("",page);
    }

    //更新管理员最后登录时间
    @PutMapping("/login-time/{id}")
    public Result updateLoginTime(@PathVariable Integer id) {
        //根据id查询数据库中对应id的管理员
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        //更新数据库中的最后登录时间为当前的时间
        admin.setLast_login_time(LocalDateTime.now());
        //更新数据
        adminService.updateById(admin);
        return Result.success("登录时间已更新", admin);
    }
    // 重置管理员密码为默认密码
    @PutMapping("/reset-password/{id}")
    public Result resetPassword(@PathVariable int id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        // 使用工具类加密默认密码
        admin.setPassword(PasswordUtil.encodePassword(PasswordUtil.DEFAULT_PASSWORD));
        adminService.updateById(admin);
        return Result.success("密码重置成功", admin);
    }
}
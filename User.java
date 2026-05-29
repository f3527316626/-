package com.easy.bean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String gender;
    private String image;
    private String nickname;
    private Integer status=0;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm")
    private LocalDateTime last_login_time;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm")
    private LocalDateTime registration_date;
}
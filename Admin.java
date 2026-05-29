package com.easy.bean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Admin {
    //凡是有id字段的都自增
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String realname;
    private String password;
    private Integer status;
    @JsonFormat(pattern="YYYY-MM-dd:mm")
    private LocalDateTime last_login_time;
    @JsonFormat(pattern="YYYY-MM-dd:mm")
    private LocalDateTime registration_data;
}

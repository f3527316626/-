package com.easy.bean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class Product {
    //id是主键，方式是递增的
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer category_id;
    private Integer brand_id;
    private String image;
    private BigDecimal price;
    private Integer status;
    @TableLogic//逻辑删除，不是真删除，用于保存信息
    private Integer del_flag;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm")
    private LocalDateTime create_time;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm")
    private LocalDateTime update_time;
    @TableField(exist = false)//表明数据库中并没有这个字段，是临时的
    private Category category;
    @TableField(exist = false)
    private Brand brand;
}
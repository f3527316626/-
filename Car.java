package com.easy.bean;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
//指定数据库中的表名是rental_orders
@TableName("rental_orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("order_no")  // 指定数据库列名
    private String orderNo;
    @TableField("user_id")
    private Integer userId;
    @TableField("vehicle_id")
    private Integer vehicleId;
    // 核心信息
    @TableField("vehicle_name")
    private String vehicleName;
    @TableField("driver_name")
    private String driverName;
    // 租赁时间 - 修正时间格式，添加秒
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  // 添加秒和时区
    @TableField("pickup_time")
    private LocalDateTime pickupTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  // 添加秒和时区
    @TableField("return_time")
    private LocalDateTime returnTime;
    // 地点信息
    @TableField("pickup_location")
    private String pickupLocation;
    // 费用信息
    @TableField("total_price")
    private BigDecimal totalPrice;
    // 订单状态
    @TableField("order_status")
    private String orderStatus = "pending";  // 默认待确认
    // 时间戳
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
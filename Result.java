package com.easy.util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code;
    private String message;
    private Object data;
    public static Result success(String message){
        return new Result(200,message,null);
    }
    public static Result success(String message,Object data){
        return new Result(200,message,data);
    }
    public static Result fail(String message){
        return new Result(500,message,null);
    }
    // 添加 error 方法
    public static Result error(String message){
        return new Result(400,message,null);
    }
    // 添加带错误码的error方法
    public static Result error(int code, String message){
        return new Result(code, message, null);
    }
}
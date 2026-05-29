package com.easy.util;
import cn.hutool.crypto.digest.DigestUtil;
public class PasswordUtil {
    public static final String DEFAULT_PASSWORD="123123";
    private static char[] arr={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    /**
     * 原文密码加密成密文的方法
     * 随机次数随机盐值加密
     * @param srcpass  原文密码
     * @return   密文密码
     */
    public static String encodePassword(String srcpass){
        //生成随机参数，count_index决定迭代次数，salt_index决定盐值
        int count_index=(int)(Math.random()*arr.length);
        int salt_index=(int)(Math.random()*arr.length);
        //将原始密码和盐值进行拼接
        String result=srcpass+arr[salt_index];
        //根据count_index进行md5hash  0-48次  “a”-97次
        for(int i=0;i<arr[count_index];i++){
            result= DigestUtil.md5Hex(result);
        }
        //生成最终的密文  迭代次数+md5哈希的结果+盐值字符
        return arr[count_index]+result+arr[salt_index];
    }
    /**
     * 原文密码和密文密码比较的方法
     * @param srcpass  用户输入的原始密码
     * @param enpass   数据库中存储的密文
     * @return     比较的结果
     */
    public static boolean equalsPassword(String srcpass,String enpass){
        //从密文中取第一个字符（迭代次数）和最后一个字符（盐值）
        char count=enpass.charAt(0);
        char salt=enpass.charAt(enpass.length()-1);
        //用户输入的密码加盐之后再进行哈希，哈希后的加上第一个字母的次数，进行比较，判断密码是否相同
        srcpass=srcpass+salt;
        for(int i=0;i<count;i++){
            srcpass=DigestUtil.md5Hex(srcpass);
        }
        return enpass.equals(count+srcpass+salt);
    }
    public static void main(String[] args) {
        System.out.println(encodePassword("123123"));
    }
}

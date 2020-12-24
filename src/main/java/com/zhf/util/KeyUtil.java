package com.zhf.util;

import java.util.Random;

/**
 * 随机生成字符串
 */
public class KeyUtil {

    /**
     * 生成唯一的主键
     * 格式：时间+随机数
     * @return
     */
    /** 避免一次生成多个且相同的id，用synchronized锁. */
    public static synchronized String genUniqueKey(){
        Random random = new Random();
        /** 生成六位随机数. */
        Integer number = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(number);
    }
}

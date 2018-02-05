package com.gt.union.common.util;

import java.util.UUID;

/**
 * 联盟卡工具类
 *
 * @author hongjiye
 * @time 2017-12-05 15:53
 **/
public class UnionCardUtil {

    /**
     * 生成联盟卡号  12位  10开头
     *
     * @return
     */
    public static String generateCardNo() {
        int machineId = 10;// 最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {// 有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return machineId + String.format("%010d", hashCodeV);
    }

}

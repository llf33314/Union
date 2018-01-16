package com.gt.union.api.amqp.entity;

import com.alibaba.fastjson.JSON;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.PropertiesUtil;

/**
 * 消息队列实体
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public class PhoneMessage extends SmsMessage{
    private String company;

    private Integer model;

    private Integer busId;

    private String mobiles;

    private String content;

    /**
     * 默认构造函数，json转对象时需要
     */
    private PhoneMessage(){
    }

    public PhoneMessage(Integer busId, String mobiles, String content) {
        this.company = PropertiesUtil.getWxmpCompany();
        this.model = ConfigConstant.SMS_UNION_MODEL;

        this.busId = busId;
        this.mobiles = mobiles;
        this.content = content;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

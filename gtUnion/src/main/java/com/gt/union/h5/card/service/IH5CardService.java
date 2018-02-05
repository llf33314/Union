package com.gt.union.h5.card.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.union.h5.card.vo.CardDetailVO;
import com.gt.union.h5.card.vo.IndexVO;
import com.gt.union.h5.card.vo.MyCardDetailVO;

/**
 * @author hongjiye
 * @time 2017-12-19 11:38
 **/
public interface IH5CardService {

    /**
     * 获取商家h5联盟卡首页信息
     *
     * @param phone 粉丝用户手机号
     * @param busId 商家id
     * @return
     * @throws Exception 统一处理异常
     */
    IndexVO getIndexVO(String phone, Integer busId) throws Exception;

    /**
     * 联盟卡详情
     *
     * @param phone      粉丝用户手机号
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动卡id
     * @return
     * @throws Exception 统一处理异常
     */
    CardDetailVO getCardDetail(String phone, Integer busId, Integer unionId, Integer activityId) throws Exception;

    /**
     * 获取我的联盟卡详情
     *
     * @param phone 用户手机号
     * @return
     * @throws Exception 统一处理异常
     */
    MyCardDetailVO myCardDetail(String phone) throws Exception;

    /**
     * 绑定联盟卡手机号
     *
     * @param member 用户
     * @param busId  商家id
     * @param phone  手机号
     * @param code   验证码
     * @throws Exception 统一处理异常
     *
     */
    void bindCardPhone(Member member, Integer busId, String phone, String code) throws Exception;

    /**
     * 办理联盟卡
     *
     * @param phone      手机号
     * @param busId      商家id
     * @param activityId 活动id
     * @param unionId    联盟id
     * @return
     * @throws Exception 统一处理异常
     */
    String cardTransaction(String phone, Integer busId, Integer activityId, Integer unionId) throws Exception;

    /**
     * 获取联盟卡消费记录
     *
     * @param page  分页参数
     * @param phone 手机号
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageConsumeByPhone(Page page, String phone) throws Exception;
}

package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.vo.CardFanDetailVO;
import com.gt.union.card.main.vo.CardFanSearchVO;
import com.gt.union.card.main.vo.CardFanVO;

import java.util.List;

/**
 * 联盟卡根信息 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
public interface IUnionCardFanService extends IService<UnionCardFan> {
    //***************************************** Domain Driven Design - get *****************************************

    /**
     * 首页-联盟卡-详情
     *
     * @param fanId   粉丝id
     * @param busId   商家id
     * @param unionId 联盟id
     * @return CardFanDetailVO
     * @throws Exception 统一处理异常
     */
    CardFanDetailVO getFanDetailVOByIdAndBusIdAndUnionId(Integer fanId, Integer busId, Integer unionId) throws Exception;

    /**
     * 根据粉丝id，获取对象
     *
     * @param fanId 粉丝id
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getById(Integer fanId) throws Exception;

    /**
     * 前台-联盟卡消费核销-搜索
     *
     * @param busId 商家id
     * @param numberOrPhone 联盟卡号或手机号
     * @param optUnionId    联盟id
     * @return CardFanSearchVO
     * @throws Exception 统一处理异常
     */
    CardFanSearchVO getCardFanSearchVOByBusId(Integer busId, String numberOrPhone, Integer optUnionId) throws Exception;

    /**
     * 根据联盟卡号或手机号，获取粉丝信息
     *
     * @param numberOrPhone 联盟卡号或手机号
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getByNumberOrPhone(String numberOrPhone) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：首页-联盟卡
     *
     * @param busId     商家id
     * @param unionId   联盟id
     * @param optNumber 联盟卡号
     * @param optPhone  手机号
     * @return List<CardFanVO>
     * @throws Exception 统一处理异常
     */
    List<CardFanVO> listCardFanVoByBusIdAndUnionId(Integer busId, Integer unionId, String optNumber, String optPhone) throws Exception;

    /**
     * controller专用：获取在指定联盟下具有有效折扣卡的粉丝信息
     *
     * @param unionId   联盟id
     * @param optNumber 联盟卡号
     * @param optPhone  手机号
     * @return List<UnionCardFan>
     * @throws Exception 统一处理异常
     */
    List<UnionCardFan> listWithDiscountCardByUnionId(Integer unionId, String optNumber, String optPhone) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}
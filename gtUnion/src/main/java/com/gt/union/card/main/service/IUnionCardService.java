package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardPhoneVO;
import com.gt.union.union.main.vo.UnionPayVO;

import java.util.List;

/**
 * 联盟卡 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardService extends IService<UnionCard> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 前台-办理联盟卡-查询联盟和联盟卡
     *
     * @param busId      商家id
     * @param fanId      粉丝id
     * @param optUnionId 联盟id
     * @return CardApplyVO
     * @throws Exception 统一处理异常
     */
    CardApplyVO getCardApplyVOByBusIdAndFanId(Integer busId, Integer fanId, Integer optUnionId) throws Exception;

    /**
     * 获取有效的折扣卡信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getValidDiscountCardByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception;

    /**
     * 获取有效的折扣卡信息
     *
     * @param unionId    联盟id
     * @param fanId      粉丝id
     * @param activityId 活动id
     * @return UnionCard
     * @throws Exception 统一处理异常
     */
    UnionCard getValidActivityCardByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception;

    /**
     * 根据id获取联盟卡信息
     * @param id        联盟卡id
     * @return
     * @throws Exception
     */
    UnionCard getById(Integer id) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取有效的联盟卡列表信息
     *
     * @param unionId 联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取有效的联盟卡列表信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception;

    /**
     * 获取有效的联盟卡列表信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param type    联盟卡类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception;

    /**
     * 获取有效的联盟卡列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    /**
     * 获取有效的联盟卡列表信息
     *
     * @param fanId 粉丝id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> listValidByFanId(Integer fanId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 前台-办理联盟卡-确定
     *
     * @param busId          商家id
     * @param unionId        联盟id
     * @param fanId          粉丝id
     * @param activityIdList 活动id列表
     * @param unionCardApplyService
	 * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO saveApplyByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId, List<Integer> activityIdList, IUnionCardApplyService unionCardApplyService) throws Exception;

    /**
     * 前台-办理联盟卡-校验手机验证码
     *
     * @param vo 表单内容
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan checkCardPhoneVO(CardPhoneVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 前台-办理联盟卡-确定-回调
     *
     * @param orderNo    订单号
     * @param socketKey  socket关键字
     * @param payType    支付类型
     * @param payOrderNo 支付订单号
     * @param isSuccess  是否成功
     * @return String 返回结果
     */
    String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess);

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计活动卡数量
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断是否存在有效的联盟卡信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param type    联盟卡类型
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception;

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟卡类型进行过滤
     *
     * @param cardList 数据源
     * @param type     类型
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param cardList 数据源
     * @param unionId  联盟id
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByUnionId(List<UnionCard> cardList, Integer unionId) throws Exception;

    /**
     * 过滤掉过期的联盟卡
     *
     * @param cardList 数据源
     * @return List<UnionCard>
     * @throws Exception 统一处理异常
     */
    List<UnionCard> filterByValidity(List<UnionCard> cardList) throws Exception;

    //***************************************** Object As a Service - get **********************************************

}
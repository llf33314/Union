package com.gt.union.card.consume.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.vo.ConsumePostVO;
import com.gt.union.card.consume.vo.ConsumeRecordVO;
import com.gt.union.union.main.vo.UnionPayVO;

import java.util.Date;
import java.util.List;

/**
 * 消费核销 服务接口
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
public interface IUnionConsumeService extends IService<UnionConsume> {
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 前台-消费核销记录-分页；前台-消费核销记录-导出
     *
     * @param busId         商家id
     * @param optUnionId    联盟id
     * @param optShopId     门店id
     * @param optCardNumber 联盟卡号
     * @param optPhone      手机号
     * @param optBeginTime  开始时间
     * @param optEndTime    结束时间
     * @return List<ConsumeRecordVO>
     * @throws Exception 统一处理异常
     */
    List<ConsumeRecordVO> listConsumeRecordVOByBusId(Integer busId, Integer optUnionId, Integer optShopId, String optCardNumber,
                                                     String optPhone, Date optBeginTime, Date optEndTime) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 前台-联盟卡消费核销-支付
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @param vo      表单内容
     * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO saveConsumePostVOByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId, ConsumePostVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 前台-联盟卡消费核销-支付-回调
     *
     * @param consumeId 消费id
     * @param socketKey socket关键字
     * @param payType   支付方式
     * @param orderNo   订单号
     * @param isSuccess 是否成功(0：否 1：是)
     * @return String 返回结果
     */
    String updateCallbackById(String consumeId, String socketKey, String payType, String orderNo, Integer isSuccess);

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}
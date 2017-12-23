package com.gt.union.card.consume.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.vo.ConsumePostVO;
import com.gt.union.card.consume.vo.ConsumeRecordVO;
import com.gt.union.common.exception.ParamException;
import com.gt.union.h5.card.vo.MyCardConsumeVO;
import com.gt.union.union.main.vo.UnionPayVO;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 获取消费核销信息
     *
     * @param orderNo 订单号
     * @return UnionConsume
     * @throws Exception 统一处理异常
     */
    UnionConsume getByOrderNo(String orderNo) throws Exception;

    /**
     * 根据订单号和行业模型查询消费记录
     * @param orderNo       订单号
     * @param model         行业模型
     * @throws Exception 统一处理异常
     * @return
     */
    UnionConsume getByOrderNoAndModel(String orderNo, Integer model) throws Exception;

    //***************************************** Domain Driven Design - count *********************************************

    /**
     * 根据联盟卡粉丝id计算消费记录数
     * @param fanId     联盟卡粉丝id
     * @return
     */
    Integer countPayByFanId(Integer fanId) throws ParamException;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：前台-消费核销记录；导出：前台-消费核销记录
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

    /**
     * 分页：联盟卡手机端，我的消费记录列表
     * @param fanId     联盟卡粉丝id
     * @param page      分页参数
     * @return
     */
    List<MyCardConsumeVO> listConsumeByFanId(Integer fanId, Page page) throws Exception;

    /**
     * 分页：根据联盟卡粉丝id获取已支付消费记录列表
     * @param fanId     联盟卡粉丝id
     * @param page      分页参数
     * @return
     */
    List<UnionConsume> listPayByFanId(Integer fanId, Page page);


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

    /**
     * 保存核销记录
     * @param unionConsume
     * @throws Exception
     */
    void save(UnionConsume unionConsume) throws Exception;


    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 前台-联盟卡消费核销-支付-回调
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

    //***************************************** Domain Driven Design - boolean *****************************************

}
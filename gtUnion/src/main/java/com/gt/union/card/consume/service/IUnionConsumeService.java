package com.gt.union.card.consume.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
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
public interface IUnionConsumeService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 缓存穿透-获取未删除的消费核销信息
     *
     * @param orderNo 订单号
     * @return UnionConsume
     * @throws Exception 统一处理异常
     */
    UnionConsume getValidByOrderNo(String orderNo) throws Exception;

    /**
     * 缓存穿透-根据订单号和行业模型查询未删除的消费记录
     *
     * @param orderNo 订单号
     * @param model   行业模型
     * @return UnionConsume
     * @throws Exception 统一处理异常
     */
    UnionConsume getValidByOrderNoAndModel(String orderNo, Integer model) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 重复方法抽离：根据消费记录列表获取VO对象
     *
     * @param consumeList 消费记录列表
     * @return List<ConsumeRecordVO>
     * @throws Exception 统一处理异常
     */
    List<ConsumeRecordVO> listConsumeRecordVO(List<UnionConsume> consumeList) throws Exception;

    /**
     * 导出：前台-消费核销记录
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
    List<ConsumeRecordVO> listConsumeRecordVOByBusId(
            Integer busId, Integer optUnionId, Integer optShopId, String optCardNumber, String optPhone, Date optBeginTime, Date optEndTime) throws Exception;

    /**
     * 分页：前台-消费核销记录
     *
     * @param page          分页对象
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
    Page pageConsumeRecordVOByBusId(
            Page page, Integer busId, Integer optUnionId, Integer optShopId, String optCardNumber, String optPhone, Date optBeginTime, Date optEndTime) throws Exception;

    /**
     * 缓存穿透-分页：联盟卡手机端，我的消费记录列表
     *
     * @param page  分页参数
     * @param fanId 联盟卡粉丝id
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageConsumeByFanId(Page page, Integer fanId) throws Exception;

    /**
     * 缓存穿透-分页：根据联盟卡粉丝id获取已支付消费记录列表
     *
     * @param page  分页参数
     * @param fanId 联盟卡粉丝id
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pagePayByFanId(Page page, Integer fanId) throws Exception;

    //********************************************* Base On Business - save ********************************************

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

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

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

    //********************************************* Base On Business - other *******************************************

    /**
     * 根据联盟卡粉丝id计算消费记录数
     *
     * @param fanId 联盟卡粉丝id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidPayByFanId(Integer fanId) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionConsumeList 数据源
     * @param delStatus        删除状态
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> filterByDelStatus(List<UnionConsume> unionConsumeList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取消费核销信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionConsume
     * @throws Exception 统一处理异常
     */
    UnionConsume getById(Integer id) throws Exception;

    /**
     * 获取未删除的消费核销信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionConsume
     * @throws Exception 统一处理异常
     */
    UnionConsume getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的消费核销信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionConsume
     * @throws Exception 统一处理异常
     */
    UnionConsume getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionConsumeList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionConsume> unionConsumeList) throws Exception;


    /**
     * 获取消费核销列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listByMemberId(Integer memberId) throws Exception;

    /**
     * 获取未删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listValidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取已删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listInvalidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取消费核销列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取消费核销列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listByCardId(Integer cardId) throws Exception;

    /**
     * 获取未删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listValidByCardId(Integer cardId) throws Exception;

    /**
     * 获取已删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listInvalidByCardId(Integer cardId) throws Exception;

    /**
     * 获取消费核销列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listByFanId(Integer fanId) throws Exception;

    /**
     * 获取未删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listValidByFanId(Integer fanId) throws Exception;

    /**
     * 获取已删除的消费核销列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listInvalidByFanId(Integer fanId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionConsume>
     * @throws Exception 统一处理异常
     */
    List<UnionConsume> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionConsume> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionConsume 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionConsume newUnionConsume) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionConsumeList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionConsume> newUnionConsumeList) throws Exception;

    //****************************************** Object As a Service - remove ******************************************

    /**
     * 移除(by myBatisGenerator)
     *
     * @param id 移除内容
     * @throws Exception 统一处理异常
     */
    void removeById(Integer id) throws Exception;

    /**
     * 批量移除(by myBatisGenerator)
     *
     * @param idList 移除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //****************************************** Object As a Service - update ******************************************

    /**
     * 更新(by myBatisGenerator)
     *
     * @param updateUnionConsume 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionConsume updateUnionConsume) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionConsumeList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionConsume> updateUnionConsumeList) throws Exception;

}
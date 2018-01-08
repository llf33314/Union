package com.gt.union.card.sharing.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.vo.CardSharingRecordVO;

import java.util.Date;
import java.util.List;

/**
 * 联盟卡售卡分成记录 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
public interface IUnionCardSharingRecordService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的售卡佣金分成记录
     *
     * @param unionId         联盟id
     * @param sharingMemberId 分成盟员id
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidByUnionIdAndSharingMemberId(Integer unionId, Integer sharingMemberId) throws Exception;

    /**
     * 分页：我的联盟-售卡佣金分成管理-售卡分成记录；导出：我的联盟-售卡佣金分成管理-售卡分成记录
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param optCardNumber 卡号
     * @param optBeginTime  开始时间
     * @param optEndTime    结束时间
     * @return List<CardSharingRecordVO>
     * @throws Exception 统一处理异常
     */
    List<CardSharingRecordVO> listCardSharingRecordVOByBusIdAndUnionId(
            Integer busId, Integer unionId, String optCardNumber, Date optBeginTime, Date optEndTime) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardSharingRecordList 数据源
     * @param delStatus                  删除状态
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> filterByDelStatus(List<UnionCardSharingRecord> unionCardSharingRecordList, Integer delStatus) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param recordList 数据源
     * @param unionId    联盟id
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> filterByUnionId(List<UnionCardSharingRecord> recordList, Integer unionId) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟卡售卡分成记录信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardSharingRecord
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRecord getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardSharingRecord
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRecord getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardSharingRecord
     * @throws Exception 统一处理异常
     */
    UnionCardSharingRecord getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardSharingRecordList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardSharingRecord> unionCardSharingRecordList) throws Exception;


    /**
     * 获取联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param sharingMemberId sharingMemberId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listBySharingMemberId(Integer sharingMemberId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param sharingMemberId sharingMemberId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidBySharingMemberId(Integer sharingMemberId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param sharingMemberId sharingMemberId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listInvalidBySharingMemberId(Integer sharingMemberId) throws Exception;

    /**
     * 获取联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listInvalidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listByActivityId(Integer activityId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listInvalidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listByCardId(Integer cardId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidByCardId(Integer cardId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param cardId cardId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listInvalidByCardId(Integer cardId) throws Exception;

    /**
     * 获取联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listByFanId(Integer fanId) throws Exception;

    /**
     * 获取未删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listValidByFanId(Integer fanId) throws Exception;

    /**
     * 获取已删除的联盟卡售卡分成记录列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listInvalidByFanId(Integer fanId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardSharingRecord>
     * @throws Exception 统一处理异常
     */
    List<UnionCardSharingRecord> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardSharingRecord> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardSharingRecord 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardSharingRecord newUnionCardSharingRecord) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardSharingRecordList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardSharingRecord> newUnionCardSharingRecordList) throws Exception;

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
     * @param updateUnionCardSharingRecord 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardSharingRecord updateUnionCardSharingRecord) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardSharingRecordList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardSharingRecord> updateUnionCardSharingRecordList) throws Exception;

}
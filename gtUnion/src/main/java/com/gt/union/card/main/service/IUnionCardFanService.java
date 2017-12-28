package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
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
public interface IUnionCardFanService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardFanList 数据源
     * @param delStatus        删除状态
     * @return List<UnionCardFan>
     * @throws Exception 统一处理异常
     */
    List<UnionCardFan> filterByDelStatus(List<UnionCardFan> unionCardFanList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟卡粉丝信息信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟卡粉丝信息信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟卡粉丝信息信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardFanList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardFan> unionCardFanList) throws Exception;


    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardFan>
     * @throws Exception 统一处理异常
     */
    List<UnionCardFan> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardFan> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardFan 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardFan newUnionCardFan) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardFanList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardFan> newUnionCardFanList) throws Exception;

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
     * @param updateUnionCardFan 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardFan updateUnionCardFan) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardFanList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardFan> updateUnionCardFanList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *****************************************

    /**
     * 我的联盟-首页-联盟卡-分页数据-详情
     *
     * @param busId   商家id
     * @param fanId   粉丝id
     * @param unionId 联盟id
     * @return CardFanDetailVO
     * @throws Exception 统一处理异常
     */
    CardFanDetailVO getFanDetailVOByBusIdAndIdAndUnionId(Integer busId, Integer fanId, Integer unionId) throws Exception;


    /**
     * 前台-联盟卡消费核销-搜索
     *
     * @param busId         商家id
     * @param numberOrPhone 联盟卡号或手机号
     * @param optUnionId    联盟id
     * @return CardFanSearchVO
     * @throws Exception 统一处理异常
     */
    CardFanSearchVO getCardFanSearchVOByBusId(Integer busId, String numberOrPhone, Integer optUnionId) throws Exception;

    /**
     * 获取粉丝信息
     *
     * @param numberOrPhone 联盟卡号或手机号
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getByNumberOrPhone(String numberOrPhone) throws Exception;

    /**
     * 获取粉丝信息
     *
     * @param phone 手机号
     * @return UnionCardFan
     * @throws Exception 统一处理异常
     */
    UnionCardFan getByPhone(String phone) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：我的联盟-首页-联盟卡；导出：我的联盟-首页-联盟卡
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
     * 获取具有有效折扣卡的粉丝信息
     *
     * @param unionId   联盟id
     * @param optNumber 联盟卡号
     * @param optPhone  手机号
     * @return List<UnionCardFan>
     * @throws Exception 统一处理异常
     */
    List<UnionCardFan> listWithValidDiscountCardByUnionId(Integer unionId, String optNumber, String optPhone) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************


    /**
     * 根据手机号查联盟卡粉丝信息，如果不存在 则新增
     *
     * @param phone 手机号
     * @return
     */
    UnionCardFan getOrSaveByPhone(String phone) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}
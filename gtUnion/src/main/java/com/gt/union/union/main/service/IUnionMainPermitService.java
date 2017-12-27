package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.vo.UnionPayVO;

import java.util.List;

/**
 * 联盟许可 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
public interface IUnionMainPermitService {
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
     * @param unionMainPermitList 数据源
     * @param delStatus           删除状态
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> filterByDelStatus(List<UnionMainPermit> unionMainPermitList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟许可信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟许可信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟许可信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainPermitList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMainPermit> unionMainPermitList) throws Exception;


    /**
     * 获取联盟许可列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的联盟许可列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listValidByBusId(Integer busId) throws Exception;

    /**
     * 获取已删除的联盟许可列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listInvalidByBusId(Integer busId) throws Exception;

    /**
     * 获取联盟许可列表信息(by myBatisGenerator)
     *
     * @param packageId packageId
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listByPackageId(Integer packageId) throws Exception;

    /**
     * 获取未删除的联盟许可列表信息(by myBatisGenerator)
     *
     * @param packageId packageId
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listValidByPackageId(Integer packageId) throws Exception;

    /**
     * 获取已删除的联盟许可列表信息(by myBatisGenerator)
     *
     * @param packageId packageId
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listInvalidByPackageId(Integer packageId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    List<UnionMainPermit> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionMainPermit>
     * @throws Exception 统一处理异常
     */
    Page<UnionMainPermit> pageSupport(Page page, EntityWrapper<UnionMainPermit> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMainPermit 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainPermit newUnionMainPermit) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainPermitList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainPermit> newUnionMainPermitList) throws Exception;

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
     * @param updateUnionMainPermit 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMainPermit updateUnionMainPermit) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainPermitList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMainPermit> updateUnionMainPermitList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取有效的联盟许可信息
     *
     * @param busId 商家id
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getValidByBusId(Integer busId) throws Exception;

    /**
     * 获取联盟许可信息
     *
     * @param sysOrderNo 订单号
     * @return UnionMainPermit
     * @throws Exception 统一处理异常
     */
    UnionMainPermit getBySysOrderNo(String sysOrderNo) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************


    /**
     * 我的联盟-创建联盟-购买盟主服务-支付
     *
     * @param busId     商家id
     * @param packageId 套餐id
     * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO saveByBusIdAndPackageId(Integer busId, Integer packageId) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 创建联盟-购买盟主服务-支付-回调
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
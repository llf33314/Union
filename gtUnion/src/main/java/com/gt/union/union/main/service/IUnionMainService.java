package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionVO;

import java.util.List;

/**
 * 联盟 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
public interface IUnionMainService {
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
     * @param unionMainList 数据源
     * @param delStatus     删除状态
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> filterByDelStatus(List<UnionMain> unionMainList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMain
     * @throws Exception 统一处理异常
     */
    UnionMain getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMain
     * @throws Exception 统一处理异常
     */
    UnionMain getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMain
     * @throws Exception 统一处理异常
     */
    UnionMain getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMain> unionMainList) throws Exception;


    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionMain>
     * @throws Exception 统一处理异常
     */
    Page<UnionMain> pageSupport(Page page, EntityWrapper<UnionMain> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMain 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMain newUnionMain) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMain> newUnionMainList) throws Exception;

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
     * @param updateUnionMain 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMain updateUnionMain) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMain> updateUnionMainList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 我的联盟-联盟设置-联盟基本信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionVO
     * @throws Exception 统一处理异常
     */
    UnionVO getUnionVOByBusIdAndId(Integer busId, Integer unionId) throws Exception;


    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：我的联盟-加入联盟-选择联盟
     *
     * @param busId 商家id
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listJoinValidByBusId(Integer busId) throws Exception;

    /**
     * 获取我的有效的具有读权限的联盟列表
     *
     * @param busId 商家id
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listMyValidReadByBusId(Integer busId) throws Exception;

    /**
     * 获取我的有效的具有写权限的联盟列表
     *
     * @param busId 商家id
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listMyValidWriteByBusId(Integer busId) throws Exception;

    /**
     * 辅助接口：获取我的联盟列表
     *
     * @param busId 商家id
     * @return ist<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionVO> listUnionVOByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************


    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************


    /**
     * 我的联盟-联盟设置-联盟基本信息-保存
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param vo      更新内容
     * @throws Exception 统一处理异常
     */
    void updateUnionVOByBusIdAndId(Integer busId, Integer unionId, UnionVO vo) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 获取联盟剩余可加盟数
     *
     * @param unionId 联盟id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countSurplusByUnionId(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断联盟有效性
     *
     * @param unionId 联盟id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean isUnionValid(Integer unionId) throws Exception;

    /**
     * 判断联盟有效性
     *
     * @param union 联盟
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean isUnionValid(UnionMain union) throws Exception;

}
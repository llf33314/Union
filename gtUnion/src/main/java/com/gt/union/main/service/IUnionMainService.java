package com.gt.union.main.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.vo.UnionMainVO;

import java.util.List;

/**
 * 联盟主表 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainService extends IService<UnionMain> {

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据商家id和盟员身份id，获取联盟对象
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return UnionMain
     * @throws Exception 全局处理异常
     */
    UnionMain getByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id，获取联盟成员总数上限
     *
     * @param busId {not null} 商家id
     * @return Integer
     * @throws Exception 全局处理异常
     */
    Integer getLimitMemberByBusId(Integer busId) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据商家id，分页获取具有读权限的盟员身份所在的联盟列表信息
     *
     * @param page  {not null} 分页对象
     * @param busId {not null} 商家id
     * @return Page<UnionMain>
     * @throws Exception 全局处理异常
     */
    Page<UnionMain> pageReadByBusId(Page<UnionMain> page, Integer busId) throws Exception;

    /**
     * 根据商家id，分页获取商家尚未加入的联盟列表信息
     *
     * @param page  {not null} 分页对象
     * @param busId {not null} 商家id
     * @return Page<UnionMain>
     * @throws Exception 全局处理异常
     */
    Page<UnionMain> pageOtherUnionByBusId(Page<UnionMain> page, Integer busId) throws Exception;

    /**
     * 根据联盟id集合，获取联盟列表信息
     *
     * @param unionIdList 联盟id集合
     * @return List<UnionMain>
     * @throws Exception 全局处理异常
     */
    List<UnionMain> listByIds(List<Integer> unionIdList) throws Exception;

    /**
     * 交集，判断标准是联盟id
     *
     * @param unionList  集合1
     * @param unionList2 集合2
     * @return List<UnionMain>
     * @throws Exception 全局处理异常
     */
    List<UnionMain> intersection(List<UnionMain> unionList, List<UnionMain> unionList2) throws Exception;

    /**
     * 根据商家id，获取所有具有读权限的盟员身份所在的联盟列表信息
     *
     * @param busId {not null} 商家id
     * @return List<UnionMain>
     * @throws Exception 全局处理异常
     */
    List<UnionMain> listReadByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取所有具有写权限的盟员身份所在的联盟列表信息
     *
     * @param busId {not null} 商家id
     * @return List<UnionMain>
     * @throws Exception 全局处理异常
     */
    List<UnionMain> listWriteByBusId(Integer busId) throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据盟员id、商家id和VO对象，更新联盟信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @param vo       {not null} 更新信息
     * @throws Exception 全局处理异常
     */
    void updateByMemberIdAndBusIdAndVO(Integer memberId, Integer busId, UnionMainVO vo) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    /**
     * 根据联盟id，检查联盟的有效期
     *
     * @param unionId {not null} 联盟id
     * @throws Exception 全局处理异常
     */
    void checkUnionValid(Integer unionId) throws Exception;

    /**
     * 根据联盟检查联盟的有效期
     *
     * @param union {not null} 联盟
     * @throws Exception 全局处理异常
     */
    void checkUnionValid(UnionMain union) throws Exception;

    /**
     * 根据联盟id，判断联盟是否有效
     *
     * @param unionId {not null} 联盟id
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean isUnionValid(Integer unionId) throws Exception;

    /**
     * 根据联盟判断联盟是否有效
     *
     * @param union {not null} 联盟
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean isUnionValid(UnionMain union) throws Exception;

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param unionId 对象id
     * @return UnionMain
     * @throws Exception 全局处理异常
     */
    UnionMain getById(Integer unionId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newUnion 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMain newUnion) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newUnionList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMain> newUnionList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param unionId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer unionId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param unionIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> unionIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateUnion 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMain updateUnion) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateUnionList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMain> updateUnionList) throws Exception;
}

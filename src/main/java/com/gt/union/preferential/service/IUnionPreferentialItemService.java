package com.gt.union.preferential.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.preferential.entity.UnionPreferentialItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠服务项 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionPreferentialItemService extends IService<UnionPreferentialItem> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据优惠服务项id获取对象
     *
     * @param itemId {not null} 优惠服务项id
     * @return
     * @throws Exception
     */
    UnionPreferentialItem getById(Integer itemId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 查询我的优惠服务
     *
     * @param page
     * @param unionId 联盟id
     * @param busId   商家id
     * @return
     */
    Page listByUnionId(Page page, Integer unionId, Integer busId) throws Exception;

    /**
     * 根据优惠项目id和优惠服务状态，获取优惠服务项列表信息
     *
     * @param projectId {not null} 优惠项目id
     * @param status    {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
    List<UnionPreferentialItem> listByProjectIdAndStatus(Integer projectId, Integer status) throws Exception;

    /**
     * 通过managerId和verifyStatus查询对应的优惠服务项信息
     *
     * @param projectId    项目id
     * @param verifyStatus 审核状态
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listByProjectIdWidthStatus(Integer projectId, Integer verifyStatus) throws Exception;

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据优惠服务项id、商家id和盟员身份id，提交优惠服务项
     *
     * @param itemId   {not null} 优惠服务项id
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @throws Exception
     */
    void submitByIdAndBusIdAndMemberId(Integer itemId, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据优惠服务项id、商家id和盟员身份id，移除优惠服务项
     *
     * @param itemId   {not null} 优惠服务项id
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @throws Exception
     */
    void removeByIdAndBusIdAndMemberId(Integer itemId, Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id、盟员身份id、批量操作的优惠服务项id和是否审核通过，批量审核优惠服务项
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemIdList {not null} 优惠服务项id列表
     * @param isOK       {not null} 是否审核通过
     * @throws Exception
     */
    void updateBatchByBusIdAndMemberId(Integer busId, Integer memberId, List<Integer> itemIdList, Integer isOK) throws Exception;

    /**
     * 删除优惠服务项目
     *
     * @param unionId 联盟id
     * @param busId   商家id
     * @param ids     服务项目ids
     * @throws Exception
     */
    void delete(Integer unionId, Integer busId, String ids) throws Exception;

    /**
     * 审核优惠服务项目
     *
     * @param unionId      联盟id
     * @param busId        商家id
     * @param ids          服务项目ids
     * @param verifyStatus 审核状态 2：通过 3：不通过
     */
    void verify(Integer unionId, Integer busId, String ids, Integer verifyStatus) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和新增的优惠服务名称，保存新增信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param itemName {not null} 优惠服务名称
     * @throws Exception
     */
    void saveByBusIdAndMemberIdAndName(Integer busId, Integer memberId, String itemName) throws Exception;

    /**
     * 保存优惠服务项目
     *
     * @param unionId 联盟id
     * @param busId   商家id
     * @param name
     */
    void save(Integer unionId, Integer busId, String name) throws Exception;

    /**
     * 提交优惠服务项目审核
     *
     * @param unionId 联盟id
     * @param busId   商家id
     * @param id      优惠服务项目id
     */
    void addVerify(Integer unionId, Integer busId, Integer id) throws Exception;

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}

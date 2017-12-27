package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.member.entity.UnionMemberOut;
import com.gt.union.union.member.vo.MemberOutPeriodVO;
import com.gt.union.union.member.vo.MemberOutVO;

import java.util.List;

/**
 * 退盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberOutService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的退盟申请信息
     *
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getValidByIdAndUnionId(Integer outId, Integer unionId) throws Exception;

    /**
     * 获取未删除的退盟申请信息
     *
     * @param unionId       联盟id
     * @param applyMemberId 退盟盟员id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getValidByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 分页：我的联盟-退盟管理-退盟审核
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<MemberOutVO>
     * @throws Exception 统一处理异常
     */
    List<MemberOutVO> listMemberOutVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 分页：我的联盟-退盟管理-退盟过渡期
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<MemberOutPeriodVO>
     * @throws Exception 统一处理异常
     */
    List<MemberOutPeriodVO> listMemberOutPeriodVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    /**
     * 我的联盟-首页-盟员列表-分页数据-移出
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param applyMemberId 被移出的盟员id
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionIdAndApplyMemberId(Integer busId, Integer unionId, Integer applyMemberId) throws Exception;

    /**
     * 我的联盟-退盟管理-退盟过渡期-退盟申请
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param reason  退盟理由
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionId(Integer busId, Integer unionId, String reason) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    /**
     * 我的联盟-首页-盟员列表-分页数据-撤消移出；我的联盟-退盟管理-退盟过渡期-撤消退盟
     *
     * @param busId   商家id
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @throws Exception 统一处理异常
     */
    void removeByBusIdAndIdAndUnionId(Integer busId, Integer outId, Integer unionId) throws Exception;

    //********************************************* Base On Business - update ******************************************

    /**
     * 我的联盟-退盟管理-退盟审核-分页数据-同意或拒绝
     *
     * @param busId   商家id
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @param isPass  是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateByBusIdAndIdAndUnionId(Integer busId, Integer outId, Integer unionId, Integer isPass) throws Exception;

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionMemberOutList 数据源
     * @param delStatus          删除状态
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> filterByDelStatus(List<UnionMemberOut> unionMemberOutList, Integer delStatus) throws Exception;

    /**
     * 根据退盟盟员id进行过滤
     *
     * @param outList       退盟申请列表
     * @param applyMemberId 退盟盟员id
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> filterByApplyMemberId(List<UnionMemberOut> outList, Integer applyMemberId) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取退盟申请信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getById(Integer id) throws Exception;

    /**
     * 获取未删除的退盟申请信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的退盟申请信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMemberOutList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMemberOut> unionMemberOutList) throws Exception;


    /**
     * 获取退盟申请列表信息(by myBatisGenerator)
     *
     * @param applyMemberId applyMemberId
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 获取未删除的退盟申请列表信息(by myBatisGenerator)
     *
     * @param applyMemberId applyMemberId
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listValidByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 获取已删除的退盟申请列表信息(by myBatisGenerator)
     *
     * @param applyMemberId applyMemberId
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listInvalidByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 获取退盟申请列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的退盟申请列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的退盟申请列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    Page<UnionMemberOut> pageSupport(Page page, EntityWrapper<UnionMemberOut> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMemberOut 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMemberOut newUnionMemberOut) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMemberOutList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMemberOut> newUnionMemberOutList) throws Exception;

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
     * @param updateUnionMemberOut 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMemberOut updateUnionMemberOut) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMemberOutList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMemberOut> updateUnionMemberOutList) throws Exception;

}
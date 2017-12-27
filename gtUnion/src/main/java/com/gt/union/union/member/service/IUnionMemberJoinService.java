package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.member.entity.UnionMemberJoin;
import com.gt.union.union.member.vo.MemberJoinCreateVO;
import com.gt.union.union.member.vo.MemberJoinVO;

import java.util.List;

/**
 * 入盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberJoinService {
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
     * @param unionMemberJoinList 数据源
     * @param delStatus           删除状态
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> filterByDelStatus(List<UnionMemberJoin> unionMemberJoinList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取入盟申请信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getById(Integer id) throws Exception;

    /**
     * 获取未删除的入盟申请信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的入盟申请信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMemberJoinList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMemberJoin> unionMemberJoinList) throws Exception;


    /**
     * 获取入盟申请列表信息(by myBatisGenerator)
     *
     * @param applyMemberId applyMemberId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 获取未删除的入盟申请列表信息(by myBatisGenerator)
     *
     * @param applyMemberId applyMemberId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listValidByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 获取已删除的入盟申请列表信息(by myBatisGenerator)
     *
     * @param applyMemberId applyMemberId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listInvalidByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 获取入盟申请列表信息(by myBatisGenerator)
     *
     * @param recommendMemberId recommendMemberId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listByRecommendMemberId(Integer recommendMemberId) throws Exception;

    /**
     * 获取未删除的入盟申请列表信息(by myBatisGenerator)
     *
     * @param recommendMemberId recommendMemberId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listValidByRecommendMemberId(Integer recommendMemberId) throws Exception;

    /**
     * 获取已删除的入盟申请列表信息(by myBatisGenerator)
     *
     * @param recommendMemberId recommendMemberId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listInvalidByRecommendMemberId(Integer recommendMemberId) throws Exception;

    /**
     * 获取入盟申请列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的入盟申请列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的入盟申请列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    Page<UnionMemberJoin> pageSupport(Page page, EntityWrapper<UnionMemberJoin> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMemberJoin 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMemberJoin newUnionMemberJoin) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMemberJoinList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMemberJoin> newUnionMemberJoinList) throws Exception;

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
     * @param updateUnionMemberJoin 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMemberJoin updateUnionMemberJoin) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMemberJoinList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMemberJoin> updateUnionMemberJoinList) throws Exception;

    
    // TODO
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取入盟申请信息
     *
     * @param joinId  入盟申请id
     * @param unionId 联盟id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getByIdAndUnionId(Integer joinId, Integer unionId) throws Exception;

    /**
     * 获取入盟申请信息
     *
     * @param unionId       联盟id
     * @param applyMemberId 申请盟员id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：我的联盟-入盟审核
     *
     * @param buId          商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @param optPhone      盟员手机号
     * @return List<MemberJoinVO>
     * @throws Exception 统一处理异常
     */
    List<MemberJoinVO> listMemberJoinVOByBusIdAndUnionId(Integer buId, Integer unionId, String optMemberName, String optPhone) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 我的联盟-加入联盟-保存；我的联盟-推荐入盟
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param type    入盟类型
     * @param vo      表单信息
     * @throws Exception 统一处理异常
     */
    void saveJoinCreateVOByBusIdAndUnionIdAndType(Integer busId, Integer unionId, Integer type, MemberJoinCreateVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 我的联盟-入盟审核-分页数据-通过或不通过
     *
     * @param busId   商家id
     * @param joinId  入盟申请id
     * @param unionId 联盟id
     * @param isPass  是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer joinId, Integer unionId, Integer isPass) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据入盟盟员id进行过滤
     *
     * @param joinList      数据源
     * @param applyMemberId 入盟盟员id
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> filterByApplyMemberId(List<UnionMemberJoin> joinList, Integer applyMemberId) throws Exception;

}
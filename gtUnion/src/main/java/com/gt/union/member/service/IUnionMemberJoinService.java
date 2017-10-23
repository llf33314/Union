package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberJoin;
import com.gt.union.member.vo.UnionMemberJoinVO;

import java.util.List;

/**
 * 联盟成员入盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
public interface IUnionMemberJoinService extends IService<UnionMemberJoin> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * * 根据商家id和盟员id，分页获取入盟申请列表信息
     *
     * @param page                 分页对象
     * @param busId                {not null} 商家id
     * @param memberId             {not null} 盟员id
     * @param optionEnterpriseName 可选项 企业名称
     * @param optionDirectorPhone  可选项 负责人电话
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId, String optionEnterpriseName
            , String optionDirectorPhone) throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    /**
     * 保存自由加入联盟的申请信息
     *
     * @param busId             {not null} 商家id
     * @param unionId           {not null} 联盟id
     * @param unionMemberJoinVO {not null} 申请信息实体
     * @throws Exception 全局处理异常
     */
    void saveTypeJoin(Integer busId, Integer unionId, UnionMemberJoinVO unionMemberJoinVO) throws Exception;

    /**
     * 保存推荐加入联盟的申请信息
     *
     * @param busId             {not null} 商家id
     * @param memberId          {not null} 推荐人盟员身份id
     * @param unionMemberJoinVO {not null} 申请信息实体
     * @throws Exception 全局处理异常
     */
    void saveTypeRecommend(Integer busId, Integer memberId, UnionMemberJoinVO unionMemberJoinVO) throws Exception;

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 入盟审核操作，同意入盟或不同意入盟
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param joinId   {not null} 入盟申请id
     * @param isOK     {not null} 是否同意入盟
     * @throws Exception 全局处理异常
     */
    void updateJoinStatus(Integer busId, Integer memberId, Integer joinId, Integer isOK) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param joinId 对象id
     * @return UnionMemberJoin
     * @throws Exception 全局处理异常
     */
    UnionMemberJoin getById(Integer joinId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据入盟申请人的盟员身份id查询对象列表
     *
     * @param applyMemberId 入盟申请人的盟员身份id
     * @return List<UnionMemberJoin>
     * @throws Exception 全局处理异常
     */
    List<UnionMemberJoin> listByApplyMemberId(Integer applyMemberId) throws Exception;

    /**
     * 根据入盟推荐者的盟员身份id查询对象列表
     *
     * @param recommendMemberId 入盟推荐者的盟员身份id
     * @return List<UnionMemberJoin>
     * @throws Exception 全局处理异常
     */
    List<UnionMemberJoin> listByRecommendMemberId(Integer recommendMemberId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newJoin 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMemberJoin newJoin) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newJoinList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMemberJoin> newJoinList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param joinId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer joinId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param joinIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> joinIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateJoin 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMemberJoin updateJoin) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateJoinList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMemberJoin> updateJoinList) throws Exception;
}

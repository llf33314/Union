package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberJoin;
import com.gt.union.member.vo.UnionMemberJoinVO;

import java.util.List;

/**
 * <p>
 * 联盟成员入盟申请 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberJoinService extends IService<UnionMemberJoin> {
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /**
     * * 根据商家id和盟员id，分页获取入盟申请列表信息
     *
     * @param page                 分页对象
     * @param busId                {not null} 商家id
     * @param memberId             {not null} 盟员id
     * @param optionEnterpriseName 可选项 企业名称
     * @param optionDirectorPhone  可选项 负责人电话
     * @return
     * @throws Exception
     */
    Page pageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId, String optionEnterpriseName
            , String optionDirectorPhone) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /**
     * 保存自由加入联盟的申请信息
     *
     * @param busId             {not null} 商家id
     * @param unionId           {not null} 联盟id
     * @param unionMemberJoinVO {not null} 申请信息实体
     * @throws Exception
     */
    void saveTypeJoin(Integer busId, Integer unionId, UnionMemberJoinVO unionMemberJoinVO) throws Exception;

    /**
     * 保存推荐加入联盟的申请信息
     *
     * @param busId             {not null} 商家id
     * @param memberId          {not null} 推荐人盟员身份id
     * @param unionMemberJoinVO {not null} 申请信息实体
     * @throws Exception
     */
    void saveTypeRecommend(Integer busId, Integer memberId, UnionMemberJoinVO unionMemberJoinVO) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 入盟审核操作，同意入盟或不同意入盟
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param joinId   {not null} 入盟申请id
     * @param isOK     {not null} 是否同意入盟
     * @throws Exception
     */
    void updateJoinStatus(Integer busId, Integer memberId, Integer joinId, Integer isOK) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    UnionMemberJoin getById(Integer joinId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    List<UnionMemberJoin> listByApplyMemberId(Integer applyMemberId) throws Exception;

    List<UnionMemberJoin> listByRecommendMemberId(Integer recommendMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    void save(UnionMemberJoin newJoin) throws Exception;

    void saveBatch(List<UnionMemberJoin> newJoinList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    void removeById(Integer joinId) throws Exception;

    void removeBatchById(List<Integer> joinIdList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    void update(UnionMemberJoin updateJoin) throws Exception;

    void updateBatch(List<UnionMemberJoin> updateJoinList) throws Exception;
}

package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMemberJoin;
import com.gt.union.member.vo.UnionMemberJoinVO;

/**
 * <p>
 * 联盟成员入盟申请 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberJoinService extends IService<UnionMemberJoin> {
    //-------------------------------------------------- get ----------------------------------------------------------
    //------------------------------------------ list(include page) ---------------------------------------------------

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

    //------------------------------------------------- update --------------------------------------------------------

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

    //------------------------------------------------- save ----------------------------------------------------------

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

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}

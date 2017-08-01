package com.gt.union.service.basic;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMember;

import java.util.Map;

/**
 * <p>
 * 联盟成员列表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionMemberService extends IService<UnionMember> {
    /**
     * 根据联盟id和商家id获取盟员列表信息，并支持根据联盟名称进行模糊查询
     * @param page
     * @param unionId
     * @param enterpriseName
     * @return
     * @throws Exception
     */
    Page listMember(Page page, final Integer unionId, final String enterpriseName) throws Exception;

    /**
     * 根据联盟id获取退盟申请列表，并根据退盟状态outStatus进行过滤
     * @param page
     * @param unionId
     * @param outStatus
     * @return
     * @throws Exception
     */
    Page listOut(Page page, final Integer unionId, final Integer outStatus) throws Exception;

    /**
     * 根据联盟id及用户id获取可转移盟主权限的盟员信息列表
     * @param page
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    Page listTransfer(Page page, final Integer unionId, final Integer busId) throws Exception;

    /**
     * 根据盟员id获取详细信息
     * @param unionMemberId
     * @return
     * @throws Exception
     */
    Map<String, Object> getDetail(Integer unionMemberId) throws Exception;

    /**
     * 盟主权限转移
     * @param unionMemberId
     * @param unionId
     * @param busId
     * @param isUnionOwner
     * @throws Exception
     */
    void updateIsUnionMember(Integer unionMemberId, Integer unionId, Integer busId, Integer isUnionOwner) throws Exception;

    /**
     * 判断用户是否是对应联盟的盟主
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    boolean isUnionOwner(Integer unionId, Integer busId) throws Exception;

    /**
     * 通过：判断是否存在
     * @param wrapper
     * @return
     * @throws Exception
     */
    boolean isExistUnionMember(Wrapper wrapper) throws Exception;

    /**
     * 查询联盟的盟员佣金比设置
     * @param page  分页
     * @param unionId   联盟id
     * @param busId   商家id
     * @return
     * @throws Exception
     */
    Page selectUnionBrokerageList(Page page, Integer unionId, Integer busId) throws Exception;

    /**
     * 判断盟员是否有效
     * @param unionMember 联盟成员
     * @return -1-->联盟成员不存在 -2-->联盟成员已退出 -3-->联盟成员处于过渡期 1-->联盟成员有效
     */
    int isMemberValid(UnionMember unionMember);


    /**
     * 查询联盟成员
     * @param busId 商家id
     * @param unionId   联盟id
     * @return
     */
    UnionMember getUnionMember(Integer busId, Integer unionId);
}

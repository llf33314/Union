package com.gt.union.service.basic;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMember;

import java.util.List;
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
    Page listByUnionIdInPage(Page page, final Integer unionId, final String enterpriseName) throws Exception;

    /**
     * 根据联盟id获取盟员列表信息
     * @param unionId
     * @return
     */
    List<Map<String,Object>> listByUnionIdInList(Integer unionId) throws Exception;

    /**
     * 根据联盟id和退盟状态outStatus获取退盟申请列表
     * @param page
     * @param unionId
     * @param outStatus
     * @return
     * @throws Exception
     */
    Page listByUnionIdAndOutStatus(Page page, final Integer unionId, final Integer outStatus) throws Exception;

    /**
     * 根据联盟id、用户id、退盟状态和是否盟主获取盟员信息列表
     * @param page
     * @param unionId
     * @param busId
     * @param outStatus
     * @param isNuionOwner
     * @return
     * @throws Exception
     */
    Page listByUnionIdAndOutStatusAndIsNuionOwner(Page page, final Integer unionId, final Integer busId
            , final Integer outStatus, final Integer isNuionOwner) throws Exception;

    /**
     * 根据盟员id获取详细信息
     * @param id
     * @return
     * @throws Exception
     */
    Map<String, Object> getById(Integer id) throws Exception;

    /**
     * 盟主权限转移
     * @param id
     * @param unionId
     * @param busId
     * @throws Exception
     */
    void updateIsNuionOwnerById(Integer id, Integer unionId, Integer busId) throws Exception;

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
     * 查询联盟成员
     * @param busId 商家id
     * @param unionId   联盟id
     * @return
     */
    UnionMember getUnionMember(Integer busId, Integer unionId) throws Exception;


    /**
     * 判断盟员是否有效
     * @param unionMember 联盟成员
     */
    void isMemberValid(UnionMember unionMember) throws Exception;

    /**
     * 判断盟员是否有效
     * @param busId 商家id
     * @param unionId   联盟id
     */
    void isMemberValid(Integer busId, Integer unionId) throws Exception;

    /**
     * 查询商家加入的联盟数
     * @param applyBusId    商家id
     * @return
     */
    int getUnionMemberCount(Integer applyBusId);

    /**
     * 审核退盟成员
     * @param id    盟员id
     * @param unionId   联盟id
     * @param busId     商家id
     * @param verifyStatus  审核状态  1：同意退盟 2：拒绝退盟
     * @return
     * @throws Exception
     */
	Map<String, Object> updateOutStatusById(Integer id, Integer unionId, Integer busId, Integer verifyStatus) throws Exception;

    /**
     * 根据联盟id查询未提交优惠项目的盟员
     *
     * @param page      分页参数
     * @param unionId   联盟id
     * @return
     */
	Page listUncommitByUnionId(Page page, Integer unionId);

    /**
     * 根据联盟id查询非提交优惠项目的盟员数
     * @param unionId   联盟id
     * @return
     */
    int countUncommitPreferentialManager(Integer unionId);
}

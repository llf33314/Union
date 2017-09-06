package com.gt.union.service.basic;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
    Page listMapByUnionIdInPage(Page page, final Integer unionId, final String enterpriseName) throws Exception;

    /**
     * 根据联盟id获取盟员列表信息
     * @param unionId
     * @return
     */
    List<Map<String,Object>> listMapByUnionIdInList(Integer unionId) throws Exception;

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
     * @param isUnionOwner
     * @return
     * @throws Exception
     */
    Page listByUnionIdAndOutStatusAndIsUnionOwner(Page page, final Integer unionId, final Integer busId
            , final Integer outStatus, final Integer isUnionOwner) throws Exception;

    /**
     * 根据盟员id获取详细信息
     * @param id
     * @return
     * @throws Exception
     */
    Map<String, Object> getMapById(Integer id) throws Exception;

    /**
     * 接受盟主权限转移
     * @param busId
     * @param unionId
     * @throws Exception
     */
    void acceptUnionOwner(Integer busId, Integer unionId) throws Exception;

    /**
     * 转移盟主权限
     * @param id
     * @param unionId
     * @param originalOwnerBusId
     */
    void transferUnionOwner(Integer id, Integer unionId, Integer originalOwnerBusId) throws Exception;

    /**
     * 查询联盟成员
     * @param unionId   联盟id
     * @param busId 商家id
     * @return
     */
    UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;

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

    /**
     * 查询导出盟员列表的数据
     * @param unionId   联盟id
     * @param enterpriseName    企业名称
     * @return
     */
    List<Map<String,Object>> listByUnionIdList(Integer unionId, String enterpriseName) throws Exception;

    /**
     * 申请退盟
     * @param unionId   联盟id
     * @param busId        商家id
     * @param outReason 退盟理由
     */
	void applyOutUnion(Integer unionId, Integer busId, String outReason) throws Exception;

    /**
     * 盟主撤销权限转移
     * @param id    盟员id
     * @param unionId   联盟id
     * @param busId 盟主商家id
     */
	void cancelUnionOwner(Integer id, Integer unionId, Integer busId) throws Exception;

    /**
     * 盟主移出盟员
     * @param id    盟员id
     * @param unionId   联盟id
     * @param busId 商家id
     */
	void updateMemberOutById(Integer id, Integer unionId, Integer busId) throws Exception;

    /**
     * 生成盟
     * @param unionId   联盟id
     * @return
     */
	String createUnionSignByUnionId(Integer unionId);
}

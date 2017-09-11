package com.gt.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMember;

import java.util.List;

/**
 * <p>
 * 联盟成员 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMemberService extends IService<UnionMember> {
    /**
     * 根据商家id判断该商家是否是某联盟的盟主
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    boolean isUnionOwner(Integer busId) throws Exception;

    /**
     * 根据商家id和联盟id判断该商家是否是该联盟的盟主
     *
     * @param unionId {not null} 联盟id
     * @param busId   {not null} 商家id
     * @return
     * @throws Exception
     */
    boolean isUnionOwner(Integer unionId, Integer busId) throws Exception;

    /**
     * 判断商家是否具有联盟的盟员权限，未加盟、已退盟以及退盟过渡期不具有
     *
     * @param unionId {not null} 联盟id
     * @param busId   {not null} 商家id
     * @return
     * @throws Exception
     */
    public boolean hasUnionMemberAuthority(Integer unionId, Integer busId) throws Exception;

    /**
     * 根据联盟id获取该联盟盟主的盟员信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    UnionMember getUnionOwnerByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id、是否盟主、盟员状态，获取商家的盟员身份列表信息
     *
     * @param busId        {not null} 商家id
     * @param isUnionOwner {not null} 是否盟主
     * @param status       {not null} 盟员状态
     * @param orStatus 或操作，盟员状态，为空时不参与查询
     * @return
     * @throws Exception
     */
    List<UnionMember> listUnionMemberByBusIdAndIsUnionOwnerAndStatus(Integer busId, Integer isUnionOwner, Integer status
            , Integer orStatus) throws Exception;

    /**
     * 根据联盟id和盟员状态统计盟员个数
     *
     * @param unionId {not null} 联盟id
     * @param status  {not null} 盟员状态
     * @return
     * @throws Exception
     */
    Integer countUnionMemberByUnionIdAndStatus(Integer unionId, Integer status) throws Exception;

    /**
     * 根据商家id和联盟id查询盟员信息
     * @param unionId
     * @param busId
     * @return
     */
    UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;

    /**
     * 根据联盟id查询盟员列表
     * @param unionId
     * @return
     */
	List<UnionMember> getByUnionId(Integer unionId) throws Exception;
}

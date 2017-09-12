package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
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
     * 判断商家是否具有联盟的盟员权限，未加盟、已退盟以及退盟过渡期不具有
     *
     * @param memberId {not null} 盟员id
     * @return
     * @throws Exception
     */
    public boolean hasUnionMemberAuthority(Integer memberId) throws Exception;

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
     * @param orStatus     或操作，盟员状态，为空时不参与查询
     * @return
     * @throws Exception
     */
    List<UnionMember> listByBusIdAndIsUnionOwnerAndStatus(Integer busId, Integer isUnionOwner, Integer status
            , Integer orStatus) throws Exception;

    /**
     * 根据联盟id和盟员状态统计盟员个数
     *
     * @param unionId {not null} 联盟id
     * @param status  {not null} 盟员状态
     * @return
     * @throws Exception
     */
    Integer countByUnionIdAndStatus(Integer unionId, Integer status) throws Exception;

    /**
     * 根据商家id和联盟id查询盟员信息
     *
     * @param unionId
     * @param busId
     * @return
     */
    UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;

    /**
     * 根据盟员id，分页获取所有与盟员同属一个联盟的盟员信息
     *
     * @param page                 {not null} 分页对象
     * @param memberId             {not null} 盟员id
     * @param busId                {not null} 商家id
     * @param optionEnterpriseName 可选项，盟员名称，模糊匹配
     * @return
     * @throws Exception
     */
    Page pageMapByMemberIdAndBusId(Page page, Integer memberId, Integer busId, String optionEnterpriseName) throws Exception;

    /**
     * 根据盟员id和商家id，获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    UnionMember getByMemberIdAndBusId(Integer memberId, Integer busId) throws Exception;

    /**
     * 根据联盟id查询盟员列表
     * @param unionId
     * @return
     */
	List<UnionMember> listByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id查询商家加入的有效盟员信息列表
     * @param busId
     * @return
     */
	List<UnionMember> listValidByBusId(Integer busId) throws Exception;

    /**
     * 根据id获取盟员信息
     * @param memberId
     * @return
     */
	UnionMember getById(Integer memberId);
}

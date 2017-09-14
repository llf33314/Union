package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.vo.UnionMemberVO;

import java.util.List;
import java.util.Map;

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
    boolean hasUnionMemberAuthority(Integer unionId, Integer busId) throws Exception;

    /**
     * 判断商家是否具有联盟的盟员权限，未加盟、已退盟以及退盟过渡期不具有
     *
     * @param memberId {not null} 盟员id
     * @return
     * @throws Exception
     */
    boolean hasUnionMemberAuthority(Integer memberId) throws Exception;

    /**
     * 根据商家id和盟员id，检查商家信息和盟员信息是否匹配
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员id
     * @throws Exception
     */
    boolean isUnionMemberValid(Integer busId, Integer memberId) throws Exception;

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
     * @param statusArray  {not null} 盟员状态
     * @return
     * @throws Exception
     */
    List<UnionMember> listByBusIdAndIsUnionOwnerAndStatus(Integer busId, Integer isUnionOwner, Object[] statusArray) throws Exception;

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
     * 根据联盟id统计有效盟员数，即已加入的、申请退出的和退盟过渡期的
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    Integer countValidMemberByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，统计盟员数
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Integer countValidMemberByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id和联盟id查询盟员信息
     *
     * @param unionId
     * @param busId
     * @return
     */
    UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;

    /**
     * 根据盟员id和商家id，分页获取所有与该盟员同属一个联盟的盟员信息
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
     * 根据盟员id和商家id，获取所有与该盟员通属一个联盟的盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listMapByMemberIdAndBusId(Integer memberId, Integer busId) throws Exception;

    /**
     * 根据盟员id和商家id，获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    UnionMember getByIdAndBusId(Integer memberId, Integer busId) throws Exception;

    /**
     * 根据联盟id查询盟员列表
     *
     * @param unionId
     * @return
     */
    List<UnionMember> listByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id查询商家加入的有效盟员信息列表
     *
     * @param busId
     * @return
     */
    List<UnionMember> listValidByBusId(Integer busId) throws Exception;

    /**
     * 根据id获取盟员信息
     *
     * @param memberId
     * @return
     */
    UnionMember getById(Integer memberId);

    /**
     * 根据联盟ids和盟员ids查询匹配的盟员列表
     * @param unionIds      联盟ids
     * @param memberIds     盟员ids
     * @return
     */
	List<UnionMember> listByUnionIdsAndUnionMemberIds(List<Integer> unionIds, List<Integer> memberIds);

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员信息
     *
     * @param memberId      {not null} 盟员身份id
     * @param busId         {not null} 商家id
     * @param unionMemberVO {not null} 更新内容实体
     * @throws Exception
     */
    void updateByIdAndBusId(Integer memberId, Integer busId, UnionMemberVO unionMemberVO) throws Exception;
}

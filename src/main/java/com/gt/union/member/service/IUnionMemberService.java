package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.vo.CardDividePercentVO;
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
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id获取该联盟盟主的盟员信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    UnionMember getOwnerByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，获取具有盟主权限的盟员身份信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    UnionMember getOwnerByBusId(Integer busId) throws Exception;

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
     * 根据id获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @return
     */
    UnionMember getById(Integer memberId) throws Exception;

    /**
     * 根据商家id和联盟id，获取商家在该联盟的盟员身份
     *
     * @param busId   {not null} 商家id
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    UnionMember getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

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
    Page pageMapByIdAndBusId(Page page, Integer memberId, Integer busId, String optionEnterpriseName) throws Exception;

    /**
     * 根据盟员id和商家id，获取所有与该盟员同属一个联盟的盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listMapByIdAndBusId(Integer memberId, Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有读权限的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<UnionMember> listReadByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有写权限的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<UnionMember> listWriteByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取所有具有写权限，且所在联盟是有效的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<UnionMember> listWriteWithValidUnionByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有读权限、且不是盟主的盟员身份列表信息
     *
     * @param busId {not null}
     * @return
     * @throws Exception
     */
    List<UnionMember> listNotOwnerReadByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有读权限的盟员身份列表信息，以及对应所在的联盟信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listReadMapByBusId(Integer busId) throws Exception;

    /**
     * 根据联盟id，获取该联盟下所有具有读权限的盟员身份列表信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    List<UnionMember> listReadByUnionId(Integer unionId) throws Exception;

    /**
     * 根据盟员id列表，获取盟员列表信息
     *
     * @param memberIdList 盟员id列表
     * @return
     * @throws Exception
     */
    List<UnionMember> listByIds(List<Integer> memberIdList) throws Exception;

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员信息
     *
     * @param memberId      {not null} 盟员身份id
     * @param busId         {not null} 商家id
     * @param unionMemberVO {not null} 更新内容实体
     * @throws Exception
     */
    void updateByIdAndBusId(Integer memberId, Integer busId, UnionMemberVO unionMemberVO) throws Exception;

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员售卡分成信息
     *
     * @param memberId                {not null} 盟员身份id
     * @param busId                   {not null} 商家id
     * @param cardDividePercentVOList {not null} 更新内容实体
     * @throws Exception
     */
    void updateCardDividePercentByIdAndBusId(Integer memberId, Integer busId, List<CardDividePercentVO> cardDividePercentVOList) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------

    /**
     * 根据联盟id，统计具有读权限的盟员数
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    Integer countReadByUnionId(Integer unionId) throws Exception;

    /**
     * 根据联盟id，统计具有写权限的盟员数
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    Integer countWriteByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，统计具有读权限的盟员身份数
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Integer countReadByBusId(Integer busId) throws Exception;

    //------------------------------------------------ boolean --------------------------------------------------------

    /**
     * 根据商家id判断该商家是否是某联盟的盟主
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    boolean isUnionOwner(Integer busId) throws Exception;

    /**
     * 根据盟员对象，判断是否具有读权限，只有已加盟、申请退盟状态和退盟过渡期才有，未加盟、申请加盟和已退盟不具有
     *
     * @param unionMember {not null} 盟员对象
     * @return
     * @throws Exception
     */
    boolean hasReadAuthority(UnionMember unionMember) throws Exception;

    /**
     * 根据盟员对象，判断是否具有写权限，只有已加盟和申请退盟状态才有，未加盟、申请加盟、退盟过渡期和已退盟不具有
     *
     * @param unionMember {not null} 盟员对象
     * @return
     * @throws Exception
     */
    boolean hasWriteAuthority(UnionMember unionMember) throws Exception;
}

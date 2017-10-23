package com.gt.union.member.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.vo.CardDividePercentVO;
import com.gt.union.member.vo.UnionMemberVO;

import java.util.List;
import java.util.Map;

/**
 * 联盟成员 服务接口
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
public interface IUnionMemberService extends IService<UnionMember> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据联盟id获取该联盟盟主的盟员信息
     *
     * @param unionId {not null} 联盟id
     * @return UnionMember
     * @throws Exception 全局处理异常
     */
    UnionMember getOwnerByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，获取具有盟主权限的盟员身份信息
     *
     * @param busId {not null} 商家id
     * @return UnionMember
     * @throws Exception 全局处理异常
     */
    UnionMember getOwnerByBusId(Integer busId) throws Exception;

    /**
     * 根据盟员id和商家id，获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return UnionMember
     * @throws Exception 全局处理异常
     */
    UnionMember getByIdAndBusId(Integer memberId, Integer busId) throws Exception;

    /**
     * 根据商家id和联盟id，获取商家在该联盟的盟员身份
     *
     * @param busId   {not null} 商家id
     * @param unionId {not null} 联盟id
     * @return UnionMember
     * @throws Exception 全局处理异常
     */
    UnionMember getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据盟员身份id列表，分页获取盟员身份列表信息
     *
     * @param page         {not null} 分页对象
     * @param memberIdList {not null} 盟员身份id列表
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageByIds(Page<UnionMember> page, List<Integer> memberIdList) throws Exception;

    /**
     * 根据盟员id和商家id，分页获取所有与该盟员同属一个联盟的盟员信息
     *
     * @param page                 {not null} 分页对象
     * @param memberId             {not null} 盟员id
     * @param busId                {not null} 商家id
     * @param optionEnterpriseName 可选项，盟员名称，模糊匹配
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageMapByIdAndBusId(Page page, Integer memberId, Integer busId, String optionEnterpriseName) throws Exception;

    /**
     * 根据盟员身份对象，分页获取与盟员之间的商机佣金比例设置列表信息
     *
     * @param page        {not null} 分页对象
     * @param unionMember {not null} 盟员身份对象
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageOpportunityRatioMapByMember(Page page, final UnionMember unionMember) throws Exception;

    /**
     * 根据盟主信息，分页获取本联盟中所有未提交优惠项目的盟员信息，未提交是指：不存在被审核通过的优惠服务项
     *
     * @param page        {not null} 分页对象
     * @param ownerMember {not null} 盟主身份
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pagePreferentialUnCommitByUnionOwner(Page<UnionMember> page, UnionMember ownerMember) throws Exception;

    /**
     * 根据盟主信息，分页获取本联盟中所有其他盟员是否具有盟主权限转移的相关记录
     *
     * @param page        {not null} 分页对象
     * @param ownerMember {not null} 盟主身份
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageTransferMapByUnionOwner(Page page, UnionMember ownerMember) throws Exception;

    /**
     * 根据盟员id和商家id，获取所有与该盟员同属一个联盟的盟员相关信息
     *
     * @param memberId             {not null} 盟员id
     * @param busId                {not null} 商家id
     * @param optionEnterpriseName 可选项 盟员名称
     * @return List <Map <String, Object>>
     * @throws Exception 全局处理异常
     */
    List<Map<String, Object>> listMapByIdAndBusId(Integer memberId, Integer busId, String optionEnterpriseName) throws Exception;

    /**
     * 根据盟员id，获取所有与该盟员同属一个联盟的盟员相关信息
     *
     * @param memberId {not null} 盟员id
     * @return List <Map <String, Object>>
     * @throws Exception 全局处理异常
     */
    List<Map<String, Object>> listMapById(Integer memberId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有读权限的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listReadByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有写权限的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listWriteByBusId(Integer busId) throws Exception;

    /**
     * 根据联盟id，获取该联盟下所有具有读权限的盟员身份列表信息
     *
     * @param unionId {not null} 联盟id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listReadByUnionId(Integer unionId) throws Exception;

    /**
     * 根据联盟id，获取该联盟下所有具有写权限的盟员身份列表信息
     *
     * @param unionId {not null} 联盟id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listWriteByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，获取所有具有写权限，且所在联盟是有效的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listWriteWithValidUnionByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有读权限、且不是盟主的盟员身份列表信息
     *
     * @param busId {not null}
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listNotOwnerReadByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取商家所有具有读权限的盟员身份列表信息，以及对应所在的联盟信息
     *
     * @param busId {not null} 商家id
     * @return List <Map <String, Object>>
     * @throws Exception 全局处理异常
     */
    List<Map<String, Object>> listReadWithUnionByBusId(Integer busId) throws Exception;

    /**
     * 根据盟员id列表，获取盟员列表信息
     *
     * @param memberIdList 盟员id列表
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listByIds(List<Integer> memberIdList) throws Exception;

    /**
     * 获取所有退盟过渡期时间已过，但状态还没置为无效的盟员信息列表
     *
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listExpired() throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员信息
     *
     * @param memberId {not null} 盟员身份id
     * @param busId    {not null} 商家id
     * @param memberVO {not null} 更新内容实体
     * @throws Exception 全局处理异常
     */
    void updateByIdAndBusId(Integer memberId, Integer busId, UnionMemberVO memberVO) throws Exception;

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员售卡分成信息
     *
     * @param memberId                {not null} 盟员身份id
     * @param busId                   {not null} 商家id
     * @param cardDividePercentVOList {not null} 更新内容实体
     * @throws Exception 全局处理异常
     */
    void updateCardDividePercentByIdAndBusId(Integer memberId, Integer busId, List<CardDividePercentVO> cardDividePercentVOList) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    /**
     * 根据联盟id，统计具有读权限的盟员数
     *
     * @param unionId {not null} 联盟id
     * @return Integer
     * @throws Exception 全局处理异常
     */
    Integer countReadByUnionId(Integer unionId) throws Exception;

    /**
     * 根据联盟id，统计具有写权限的盟员数
     *
     * @param unionId {not null} 联盟id
     * @return Integer
     * @throws Exception 全局处理异常
     */
    Integer countWriteByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，统计具有读权限的盟员身份数
     *
     * @param busId {not null} 商家id
     * @return Integer
     * @throws Exception 全局处理异常
     */
    Integer countReadByBusId(Integer busId) throws Exception;

    /**
     * 根据盟主身份，统计本联盟中所有未提交优惠项目的盟员数，未提交是指：不存在被审核通过的优惠服务项
     *
     * @param ownerMember {not null} 盟主身份
     * @return Integer
     * @throws Exception 全局处理异常
     */
    Integer countPreferentialUnCommitByUnionOwner(UnionMember ownerMember) throws Exception;

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    /**
     * 根据商家id判断该商家是否是某联盟的盟主
     *
     * @param busId {not null} 商家id
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean isUnionOwner(Integer busId) throws Exception;

    /**
     * 根据盟员对象，判断是否具有读权限，只有已加盟、申请退盟状态和退盟过渡期才有，未加盟、申请加盟和已退盟不具有
     *
     * @param unionMember {not null} 盟员对象
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean hasReadAuthority(UnionMember unionMember) throws Exception;

    /**
     * 根据盟员对象，判断是否具有写权限，只有已加盟和申请退盟状态才有，未加盟、申请加盟、退盟过渡期和已退盟不具有
     *
     * @param unionMember {not null} 盟员对象
     * @return boolean
     * @throws Exception 全局处理异常
     */
    boolean hasWriteAuthority(UnionMember unionMember) throws Exception;

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param memberId 对象id
     * @return UnionMember
     * @throws Exception 全局处理异常
     */
    UnionMember getById(Integer memberId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据商家id查询对象列表
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listByBusId(Integer busId) throws Exception;

    /**
     * 根据联盟id查询对象列表
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 全局处理异常
     */
    List<UnionMember> listByUnionId(Integer unionId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newMember 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMember newMember) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newMemberList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMember> newMemberList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param memberId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer memberId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param memberIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> memberIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateMember 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMember updateMember) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateMemberList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMember> updateMemberList) throws Exception;

}

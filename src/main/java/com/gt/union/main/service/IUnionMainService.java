package com.gt.union.main.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.vo.UnionMainVO;

import java.util.List;

/**
 * <p>
 * 联盟主表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainService extends IService<UnionMain> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id，获取联盟信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    UnionMain getById(Integer unionId) throws Exception;

    /**
     * 根据商家id和盟员身份id，获取联盟对象
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    UnionMain getByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception;

    /**
     * 根据商家id，获取联盟成员总数上限
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Integer getLimitMemberByBusId(Integer busId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据联盟id集合，获取联盟列表信息
     *
     * @param unionIdList 联盟id集合
     * @return
     * @throws Exception
     */
    List<UnionMain> listByIds(List<Integer> unionIdList) throws Exception;

    /**
     * 交集，判断标准是联盟id
     *
     * @param unionMainList
     * @param unionMainList2
     * @return
     * @throws Exception
     */
    List<UnionMain> intersection(List<UnionMain> unionMainList, List<UnionMain> unionMainList2) throws Exception;

    /**
     * 根据商家id，获取所有具有读权限的盟员身份所在的联盟列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<UnionMain> listReadByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，获取所有具有写权限的盟员身份所在的联盟列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<UnionMain> listWriteByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，分页获取具有读权限的盟员身份所在的联盟列表信息
     *
     * @param page  {not null} 分页对象
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Page<UnionMain> pageReadByBusId(Page page, Integer busId) throws Exception;

    /**
     * 根据商家id，分页获取商家尚未加入的联盟列表信息
     *
     * @param page  {not null} 分页对象
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Page<UnionMain> pageOtherUnionByBusId(Page page, Integer busId) throws Exception;

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据盟员id、商家id和VO对象，更新联盟信息
     *
     * @param memberId    {not null} 盟员id
     * @param busId       {not null} 商家id
     * @param unionMainVO {not null} 更新信息
     * @throws Exception
     */
    void updateByMemberIdAndBusIdAndVO(Integer memberId, Integer busId, UnionMainVO unionMainVO) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

    /**
     * 根据联盟id，检查联盟的有效期
     *
     * @param unionId {not null} 联盟id
     * @throws Exception
     */
    void checkUnionMainValid(Integer unionId) throws Exception;

    /**
     * 根据联盟检查联盟的有效期
     *
     * @param unionMain {not null} 联盟
     * @throws Exception
     */
    void checkUnionMainValid(UnionMain unionMain) throws Exception;

    /**
     * 根据联盟id，判断联盟是否有效
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    boolean isUnionMainValid(Integer unionId) throws Exception;

    /**
     * 根据联盟判断联盟是否有效
     *
     * @param unionMain {not null} 联盟
     * @return
     * @throws Exception
     */
    boolean isUnionMainValid(UnionMain unionMain) throws Exception;

}

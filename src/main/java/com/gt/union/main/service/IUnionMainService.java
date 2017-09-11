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
    /**
     * 根据id检查联盟的有效性
     *
     * @param id {not null} 联盟id
     * @throws Exception
     */
    void checkUnionMainValid(Integer id) throws Exception;

    /**
     * 根据联盟检查联盟的有效性
     *
     * @param unionMain {not null} 联盟
     * @throws Exception
     */
    void checkUnionMainValid(UnionMain unionMain) throws Exception;

    /**
     * 根据id获取联盟信息
     *
     * @param id {not null} 联盟id
     * @return
     * @throws Exception
     */
    UnionMain getById(Integer id) throws Exception;

    /**
     * 根据商家id，获取所有具有盟员身份的联盟信息，即创建的联盟+加入的联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    List<UnionMain> listByBusId(Integer busId) throws Exception;

    /**
     * 根据商家id，分页获取具有盟员身份的联盟信息，即创建的联盟+加入的联盟
     *
     * @param page  {not null} 分页对象
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Page<UnionMain> PageByBusId(Page page, Integer busId) throws Exception;

    /**
     * 根据盟员id、商家id和VO对象，更新联盟信息
     *
     * @param memberId    {not null} 盟员id
     * @param busId       {not null} 商家id
     * @param unionMainVO {not null} 更新信息
     * @throws Exception
     */
    void updateByMemberIdAndBusIdAndVO(Integer memberId, Integer busId, UnionMainVO unionMainVO) throws Exception;
}

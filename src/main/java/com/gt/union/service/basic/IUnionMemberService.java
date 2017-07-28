package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMember;

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
     * @param busId
     * @param enterpriseName
     * @return
     * @throws Exception
     */
    Page listMember(Page page, final Integer unionId, final Integer busId, final String enterpriseName) throws Exception;

    /**
     * 根据联盟id获取退盟申请列表，并根据退盟状态outStatus进行过滤
     * @param page
     * @param unionId
     * @param outStatus
     * @return
     * @throws Exception
     */
    Page listOut(Page page, final Integer unionId, final Integer outStatus) throws Exception;
}

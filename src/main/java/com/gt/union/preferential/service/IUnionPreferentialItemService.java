package com.gt.union.preferential.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.preferential.entity.UnionPreferentialItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠服务项 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionPreferentialItemService extends IService<UnionPreferentialItem> {
    /**
     * 通过managerId和verifyStatus查询对应的优惠服务项信息
     * @param projectId     项目id
     * @param verifyStatus  审核状态
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listByProjectIdWidthStatus(Integer projectId, Integer verifyStatus) throws Exception;

    /**
     * 查询我的优惠服务
     * @param page
     * @param unionId	联盟id
     * @param busId	商家id
     * @return
     */
    Page listByUnionId(Page page, Integer unionId, Integer busId) throws Exception;

    /**
     * 保存优惠服务项目
     * @param unionId  联盟id
     * @param busId	商家id
     * @param name
     */
    void save(Integer unionId, Integer busId, String name) throws Exception;

    /**
     * 删除优惠服务项目
     * @param unionId   联盟id
     * @param busId     商家id
     * @param ids       服务项目ids
     * @throws Exception
     */
    void delete(Integer unionId, Integer busId, String ids) throws Exception;

    /**
     *	提交优惠服务项目审核
     * @param unionId	  联盟id
     * @param busId     商家id
     * @param id	优惠服务项目id
     */
    void addVerify(Integer unionId, Integer busId, Integer id) throws Exception;

    /**
     * 审核优惠服务项目
     * @param unionId	联盟id
     * @param busId		商家id
     * @param ids		服务项目ids
     * @param verifyStatus	审核状态 2：通过 3：不通过
     */
    void verify(Integer unionId, Integer busId, String ids, Integer verifyStatus) throws Exception;
}

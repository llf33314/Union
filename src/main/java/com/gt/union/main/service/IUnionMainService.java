package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMain;

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
     * @param id {not null} 联盟id
     * @throws Exception
     */
	void checkUnionMainValid(Integer id) throws Exception;

    /**
     * 根据联盟检查联盟的有效性
     * @param unionMain {not null} 联盟
     * @throws Exception
     */
    void checkUnionMainValid(UnionMain unionMain) throws Exception;

    /**
     * 根据id获取联盟信息
     * @param id {not null} 联盟id
     * @return
     * @throws Exception
     */
	UnionMain getUnionMainById(Integer id) throws Exception;

}

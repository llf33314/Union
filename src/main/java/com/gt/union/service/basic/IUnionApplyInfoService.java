package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionApplyInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员申请信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionApplyInfoService extends IService<UnionApplyInfo> {

	/**
	 * 更新盟员信息
	 * @param unionApplyInfo
	 * @param id
	 * @param unionId
	 */
	void updateUnionApplyInfo(UnionApplyInfo unionApplyInfo, Integer id, Integer unionId) throws Exception;

    /**
     * 比例设置分页展示
     * @param page
     * @param unionId
     * @return
     * @throws Exception
     */
	Page pageSellDivideProportion(Page page, Integer unionId) throws Exception;

    /**
     * 比例设置列表展示
     * @param unionid
     * @throws Exception
     */
    List<Map<String,Object>> listSellDivideProportion(Integer unionid) throws Exception;

    /**
     * 更新比例
     * @param unionApplyInfoList
     * @throws Exception
     */
    void updateSellDivideProportion(List<UnionApplyInfo> unionApplyInfoList) throws Exception;

}

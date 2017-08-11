package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.vo.basic.UnionApplyVO;

import java.util.Map;

/**
 * <p>
 * 联盟成员申请推荐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionApplyService extends IService<UnionApply> {
	/**
	 * 根据联盟id和审核状态applyStatus获取入盟申请相关信息，并根据enterpriseName/directorPhone进行模糊匹配
	 * @param page
	 * @param unionId
	 * @param applyStatus
	 * @param enterpriseName
	 * @param directorPhone
	 * @return
	 * @throws Exception
	 */
	Page listByUnionIdAndApplyStatus(Page page, final Integer unionId, final Integer applyStatus, final String enterpriseName, final String directorPhone) throws Exception;

	/**
	 * 获取盟员信息
	 * @param busId	主账号商家id
	 * @param unionId	联盟id
	 * @return
	 */
	UnionApplyInfo getUnionApplyInfo(Integer busId, Integer unionId) throws Exception;

	/**
	 * 获取商家申请加盟
	 * @param busId	商家id
	 * @param unionId	联盟id
	 * @return
	 */
	UnionApply getUnionApply(Integer busId, Integer unionId);

	/**
	 * 审核申请信息
	 * @param busId		审核的商家id
	 * @param id		申请id
	 * @param unionId	联盟id
	 * @param applyStatus	审核状态 1：通过 2：不通过
	 */
	void updateByUnionIdAndApplyStatus(Integer busId, Integer id, Integer unionId, Integer applyStatus) throws Exception;

	/**
	 * 添加盟员申请
	 * @param busId    商家id
	 * @param unionApplyVO    申请信息
	 */
	Map<String, Object> save(Integer busId, UnionApplyVO unionApplyVO) throws Exception;
}

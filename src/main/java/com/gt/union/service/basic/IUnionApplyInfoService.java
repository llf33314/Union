package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.vo.UnionApplyInfoVO;
import com.gt.union.entity.basic.vo.UnionApplyVO;

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
	 * @param vo	盟员信息
	 * @param busId
	 */
	void updateById(UnionApplyInfoVO vo, Integer busId) throws Exception;

    /**
     * 比例设置分页展示
     * @param page
     * @param unionId
     * @return
     * @throws Exception
     */
	Page listBySellDivideProportionInPage(Page page, Integer unionId) throws Exception;

    /**
     * 比例设置列表展示
     * @param unionId
     * @throws Exception
     */
    List<Map<String,Object>> listBySellDivideProportionInList(Integer unionId) throws Exception;

    /**
     * 更新比例
     * @param unionApplyInfoList
     * @throws Exception
     */
    void updateBySellDivideProportion(List<UnionApplyInfo> unionApplyInfoList) throws Exception;

	/**
	 * 根据infoId获取编辑盟员信息
	 * @param id
	 * @param unionId	联盟id
	 * @param busId	商家id
	 * @return
	 */
	Map<String,Object> getMapById(Integer id, Integer unionId, Integer busId) throws Exception;

	/**
	 * 保存unionApplyInfo
	 * @param vo
	 * @param applyId 申请id
	 * @return
	 */
	UnionApplyInfo saveUnionApplyInfo(UnionApplyVO vo, Integer applyId);

    /**
     * 根据联盟id和busId获取申请附加信息
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
	UnionApplyInfo getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;
}

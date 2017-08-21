package com.gt.union.service.consume;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.consume.UnionConsumeRecord;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员消费 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionConsumeRecordService extends IService<UnionConsumeRecord> {
    /**
     * 查询本店消费核销记录，支持来源cardBusId过滤，联盟卡号cardNo、手机号phone的模糊匹配，开始时间beginTime、结束时间endTime的范围过滤
     * @param page
     * @param unionId
     * @param busId
     * @param cardBusId
     * @param cardNo
     * @param phone
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
	Page listMyByUnionId(Page page, Integer unionId, Integer busId, Integer cardBusId, String cardNo, String phone, String beginTime, String endTime) throws Exception;

    /**
     * 查询它店消费核销记录，支持来源cardBusId过滤，联盟卡号cardNo、手机号phone的模糊匹配，开始时间beginTime、结束时间endTime的范围过滤
     * @param page
     * @param unionId
     * @param busId
     * @param cardBusId
     * @param cardNo
     * @param phone
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
	Page listOtherByUnionId(Page page, Integer unionId, Integer busId, Integer cardBusId, String cardNo, String phone, String beginTime, String endTime) throws Exception;

	/**
	 * 导出本店联盟卡消费记录列表
	 * @param unionId	联盟id
	 * @param busId		本商家id
	 * @param fromBusId	来源的商家id
	 * @param cardNo	联盟卡号
	 * @param phone		手机号
	 * @param beginTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	List<Map<String,Object>> listMyByUnionId(Integer unionId, Integer busId, Integer fromBusId, String cardNo, String phone, String beginTime, String endTime) throws Exception;
}

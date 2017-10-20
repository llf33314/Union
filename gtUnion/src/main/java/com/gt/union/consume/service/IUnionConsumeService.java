package com.gt.union.consume.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.consume.entity.UnionConsume;
import com.gt.union.consume.vo.UnionConsumeParamVO;
import com.gt.union.consume.vo.UnionConsumeVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消费 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionConsumeService extends IService<UnionConsume> {

	/**
	 * 线上联盟卡核销
	 * @param reqdata
	 * @return
	 */
	UnionConsumeResult consumeByUnionCard(UnionConsumeParam reqdata) throws Exception;

	/**
	 * 线上联盟退款
	 * @param orderNo	订单号
	 * @param model		模型
	 * @return
	 */
	UnionRefundResult unionRefund(String orderNo, Integer model ) throws Exception;

	/**
	 * 根据订单号和模型查询消费记录
	 * @param orderNo	订单号
	 * @param model		模型
	 * @return
	 */
	UnionConsume getByOrderNoAndModel(String orderNo, Integer model);

	/**
	 * 我的消费记录列表
	 * @param page
	 * @param unionId	联盟id
	 * @param busId		商家id
	 * @param memberId	来源盟员id
	 * @param cardNo	联盟卡号
	 * @param phone		联盟卡手机号
	 * @param beginTime	消费起始时间
	 * @param endTime	消费结束时间
	 * @return
	 */
	Page listMy(Page page, Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception;

	/**
	 * 他店消费记录列表
	 * @param page
	 * @param unionId	联盟id
	 * @param busId		商家id
	 * @param memberId	来源盟员id
	 * @param cardNo	联盟卡号
	 * @param phone		联盟卡手机号
	 * @param beginTime	消费起始时间
	 * @param endTime	消费结束时间
	 * @return
	 */
	Page listOther(Page page, Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception;

	/**
	 * 联盟卡核销
	 * @param busId		商家id
	 * @param vo		核销参数
	 */
	void consumeByCard(Integer busId, UnionConsumeParamVO vo) throws Exception;

	/**
	 * 导出本店消费记录列表
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<UnionConsumeVO> listMyByUnionId(Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception;

	/**
	 * 导出本店消费记录列表
	 * @param titles
	 * @param contentName
	 * @param list
	 * @return
	 */
	HSSFWorkbook exportConsumeFromDetail(String[] titles, String[] contentName, List<UnionConsumeVO> list);

	/**
	 * 导出他店消费列表
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<UnionConsumeVO> listOtherByUnionId(Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime);

	/**
	 * 导出他店消费列表
	 * @param titles
	 * @param contentName
	 * @param list
	 * @return
	 */
	HSSFWorkbook exportConsumeToDetail(String[] titles, String[] contentName, List<UnionConsumeVO> list);

	/**
	 * 消费核销扫码支付成功回调
	 * @param encrypt
	 * @param only
	 * @param payType
	 */
	void payConsumeSuccess(String encrypt, String only, Integer payType) throws Exception;

	/**
	 * 生成消费核销支付二维码
	 * @param busId
	 * @param vo
	 * @return
	 */
	Map<String,Object> payConsumeQRCode(Integer busId, UnionConsumeParamVO vo) throws Exception;

}

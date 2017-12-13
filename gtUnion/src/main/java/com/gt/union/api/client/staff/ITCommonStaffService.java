package com.gt.union.api.client.staff;

import com.gt.api.bean.session.TCommonStaff;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-13 10:25
 **/
public interface ITCommonStaffService {

	/**
	 * 根据门店id获取员工列表
	 * @param shopId	门店id
	 * @return
	 */
	List<TCommonStaff> listTCommonStaffByShopId(Integer shopId);

	/**
	 * 根据员工id获取员工信息
	 * @param staffId	员工id
	 * @return
	 */
	TCommonStaff getTCommonStaffById(Integer staffId);
}

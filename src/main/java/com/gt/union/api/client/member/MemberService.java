package com.gt.union.api.client.member;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
public interface MemberService {

	/**
	 * 根据商家id和用户ids查询列表信息
	 * @param busId
	 * @param memberIds
	 * @return
	 */
	List<Map> listByBusIdAndMemberIds(Integer busId, String memberIds);
}

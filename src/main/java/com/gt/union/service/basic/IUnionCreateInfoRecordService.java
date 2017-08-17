package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionCreateInfoRecord;

/**
 * <p>
 * 创建联盟服务记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-31
 */
public interface IUnionCreateInfoRecordService extends IService<UnionCreateInfoRecord> {
    /**
     * 根据用户id获取UnionCreateInfoRecord对象
     * @param busId
     * @return
     * @throws Exception
     */
	public UnionCreateInfoRecord getByBusId(Integer busId) throws Exception;
}

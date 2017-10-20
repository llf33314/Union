package com.gt.union.log.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.log.entity.UnionLogError;

/**
 * <p>
 * 错误日志表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-08
 */
public interface IUnionLogErrorService extends IService<UnionLogError> {
    /**
     * 如果异常不为空，则保存异常信息
     * @param e
     */
    void saveIfNotNull(Exception e);
}

package com.gt.union.log.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.log.entity.UnionLogRequest;
import com.gt.union.log.mapper.UnionLogRequestMapper;
import com.gt.union.log.service.IUnionLogRequestService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 请求日志表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-08
 */
@Service
public class UnionLogRequestServiceImpl extends ServiceImpl<UnionLogRequestMapper, UnionLogRequest> implements IUnionLogRequestService {
	
}

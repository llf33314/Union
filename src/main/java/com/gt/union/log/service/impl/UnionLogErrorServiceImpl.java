package com.gt.union.log.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.DateUtil;
import com.gt.union.log.constant.LogConstant;
import com.gt.union.log.entity.UnionLogError;
import com.gt.union.log.mapper.UnionLogErrorMapper;
import com.gt.union.log.service.IUnionLogErrorService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 错误日志表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-08
 */
@Service
public class UnionLogErrorServiceImpl extends ServiceImpl<UnionLogErrorMapper, UnionLogError> implements IUnionLogErrorService {

    @Override
    public void saveIfNotNull(Exception e) {
        try {
            if (e != null) {
                StackTraceElement[] elements = e.getStackTrace();
                StackTraceElement element = elements[0];
                String className = element.getClassName();
                String methodName = element.getMethodName();
                StringBuilder sbMessage = new StringBuilder(e.getMessage()).append(LogConstant.lineSeperator);
                for (int i = 0; i < elements.length; i++) {
                    element = elements[i];
                    sbMessage.append("    at ").append(element.getClassName())
                            .append(".").append(element.getMethodName())
                            .append("(").append(element.getFileName())
                            .append(":").append(element.getLineNumber())
                            .append(")").append(LogConstant.lineSeperator);
                }
                UnionLogError unionLogError = new UnionLogError();
                unionLogError.setCreatetime(DateUtil.getCurrentDate());
                unionLogError.setClassName(className);
                unionLogError.setMethodName(methodName);
                unionLogError.setError(e.getMessage());
                unionLogError.setDetail(sbMessage.toString().getBytes());
                this.insert(unionLogError);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}

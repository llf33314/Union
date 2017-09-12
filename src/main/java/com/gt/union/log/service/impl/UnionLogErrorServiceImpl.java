package com.gt.union.log.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
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
                String errorMessage = e.getMessage();
                if (e instanceof ParamException || e instanceof BusinessException || e instanceof BaseException) {
                    errorMessage = ((BaseException) e).getErrorMsg();
                }
                StringBuilder sbDetailMessage = new StringBuilder(errorMessage)
                        .append(LogConstant.lineSeperator);
                for (int i = 0; i < elements.length; i++) {
                    element = elements[i];
                    sbDetailMessage.append("    at ").append(element.getClassName())
                            .append(".").append(element.getMethodName())
                            .append("(").append(element.getFileName())
                            .append(":").append(element.getLineNumber())
                            .append(")").append(LogConstant.lineSeperator);
                }
                UnionLogError unionLogError = new UnionLogError();
                unionLogError.setCreatetime(DateUtil.getCurrentDate());
                unionLogError.setClassName(className);
                unionLogError.setMethodName(methodName);
                unionLogError.setError(errorMessage);
                unionLogError.setDetail(sbDetailMessage.toString().getBytes());
                this.insert(unionLogError);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}

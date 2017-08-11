package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.entity.basic.UnionMemberPreferentialService;
import com.gt.union.mapper.basic.UnionMemberPreferentialServiceMapper;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 盟员优惠服务项 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionMemberPreferentialServiceServiceImpl extends ServiceImpl<UnionMemberPreferentialServiceMapper, UnionMemberPreferentialService> implements IUnionMemberPreferentialServiceService {
    private static final String PAGE_PREFERENTIAL_SERVICE_MANAGERID = "UnionMemberPreferentialServiceServiceImpl.pagePreferentialServiceByManagerId()";

    @Override
    public Page pagePreferentialServiceByManagerId(Page page, final Integer managerId, final Integer verifyStatus) throws Exception {
        if (page == null) {
            throw new ParamException(PAGE_PREFERENTIAL_SERVICE_MANAGERID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (managerId == null) {
            throw new ParamException(PAGE_PREFERENTIAL_SERVICE_MANAGERID, "参数managerId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(PAGE_PREFERENTIAL_SERVICE_MANAGERID, "参数verifyStatus为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                .eq("manager_id", managerId)
                .eq("verify_status", verifyStatus);

        entityWrapper.setSqlSelect(" id id, service_name serviceName, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");

        return this.selectPage(page, entityWrapper);
    }
}

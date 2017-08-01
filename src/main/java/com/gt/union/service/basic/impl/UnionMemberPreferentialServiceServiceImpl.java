package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
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

    @Override
    public Page pagePreferentialServiceByManagerId(Page page, final Integer managerId, final Integer verifyStatus) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberPreferentialServiceServiceImpl.pagePreferentialServiceByManagerId():参数page不能为空!");
        }
        if (managerId == null) {
            throw new Exception("UnionMemberPreferentialServiceServiceImpl.pagePreferentialServiceByManagerId():参数managerId不能为空!");
        }
        if (verifyStatus == null) {
            throw new Exception("UnionMemberPreferentialServiceServiceImpl.pagePreferentialServiceByManagerId():参数verifyStatus不能为空!");
        }

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                .eq("manager_id", managerId)
                .eq("verify_status", verifyStatus);

        entityWrapper.setSqlSelect(" id id, service_name serviceName, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");

        return this.selectPage(page, entityWrapper);
    }
}

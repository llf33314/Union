package com.gt.union.preferential.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.mapper.UnionPreferentialProjectMapper;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠项目 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionPreferentialProjectServiceImpl extends ServiceImpl<UnionPreferentialProjectMapper, UnionPreferentialProject> implements IUnionPreferentialProjectService {

    @Override
    public UnionPreferentialProject getByMemberId(Integer id) {
        EntityWrapper entityWrapper = new EntityWrapper<UnionPreferentialProject>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.eq("member_id",id);
        UnionPreferentialProject project = this.selectOne(entityWrapper);
        return project;
    }
}

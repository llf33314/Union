package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.mapper.UnionMainDictMapper;
import com.gt.union.main.service.IUnionMainDictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 联盟设置申请填写信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainDictServiceImpl extends ServiceImpl<UnionMainDictMapper, UnionMainDict> implements IUnionMainDictService {

    @Override
    public List<UnionMainDict> list(Integer unionId) {
        EntityWrapper entityWrapper = new EntityWrapper<UnionMainDict>();
        entityWrapper.eq("union_id", unionId);
        List<UnionMainDict> list = this.selectList(entityWrapper);
        return list;
    }
}

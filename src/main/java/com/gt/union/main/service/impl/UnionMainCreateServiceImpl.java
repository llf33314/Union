package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.main.entity.UnionMainCreate;
import com.gt.union.main.mapper.UnionMainCreateMapper;
import com.gt.union.main.service.IUnionMainCreateService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 创建联盟 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainCreateServiceImpl extends ServiceImpl<UnionMainCreateMapper, UnionMainCreate> implements IUnionMainCreateService {

    @Override
    public boolean hasCreateUnionMain(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        UnionMainCreate unionMainCreate = this.selectOne(entityWrapper);
        return unionMainCreate != null ? true : false;
    }
}

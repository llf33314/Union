package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.entity.business.UnionBusinessRecommendInfo;
import com.gt.union.mapper.business.UnionBusinessRecommendInfoMapper;
import com.gt.union.service.business.IUnionBusinessRecommendInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟商家推荐关联信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBusinessRecommendInfoServiceImpl extends ServiceImpl<UnionBusinessRecommendInfoMapper, UnionBusinessRecommendInfo> implements IUnionBusinessRecommendInfoService {
    private static final String GET_RECOMMEND_ID = "UnionBusinessRecommendInfoServiceImpl.getByRecommendId()";

    @Override
    public UnionBusinessRecommendInfo getByRecommendId(Integer recommendId) throws Exception {
        if (recommendId == null) {
            throw new ParamException(GET_RECOMMEND_ID, "参数recommendId为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("recommend_id", recommendId);

        return this.selectOne(entityWrapper);
    }
}

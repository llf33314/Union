package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.mapper.UnionMainDictMapper;
import com.gt.union.main.service.IUnionMainDictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    //-------------------------------------------------- get ----------------------------------------------------------
    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据联盟id，获取联盟申请填写信息设置
     *
     * @param unionId {not null} 联盟id
     * @return
     */
    @Override
    public List<UnionMainDict> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper<UnionMainDict>();
        entityWrapper.eq("union_id", unionId);
        return this.selectList(entityWrapper);
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据联盟id删除联盟申请填写信息设置
     *
     * @param unionId {not null} 联盟id
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("union_id", unionId);
        this.delete(entityWrapper);
    }

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}

package com.gt.union.user.introduction.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.user.introduction.constant.UserIntroductionConstant;
import com.gt.union.user.introduction.dao.IUnionUserIntroductionDao;
import com.gt.union.user.introduction.entity.UnionUserIntroduction;
import com.gt.union.user.introduction.service.IUnionUserIntroductionService;
import com.gt.union.user.introduction.util.UnionUserIntroductionCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 联盟商家简介 服务实现类
 *
 * @author linweicong
 * @version 2018-01-24 16:24:13
 */
@Service
public class UnionUserIntroductionServiceImpl implements IUnionUserIntroductionService {

    @Autowired
    private IUnionUserIntroductionDao unionUserIntroductionDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public UnionUserIntroduction getValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionUserIntroduction result = null;
        String key = UnionUserIntroductionCacheUtil.getBusIdKey(busId);
        String data = redisCacheUtil.get(key);
        if (StringUtil.isNotEmpty(data)) {
            result = JSONArray.parseObject(data, UnionUserIntroduction.class);
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("bus_id", busId)
            .eq("del_status", CommonConstant.DEL_STATUS_NO)
            .orderBy("id", true);
        result = unionUserIntroductionDao.selectOne(wrapper);
        if (CommonUtil.isNotEmpty(result)) {
            redisCacheUtil.set(key, result);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer busId, UnionUserIntroduction unionUserIntroduction) throws Exception {
        if (busId == null || unionUserIntroduction == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionUserIntroduction userIntroduction = getValidByBusId(busId);
        String key = UnionUserIntroductionCacheUtil.getBusIdKey(busId);
        if (CommonUtil.isNotEmpty(userIntroduction)) {
            //编辑 删除以前的
            UnionUserIntroduction introduction = new UnionUserIntroduction();
            introduction.setId(userIntroduction.getId());
            introduction.setDelStatus(CommonConstant.DEL_STATUS_YES);
            unionUserIntroductionDao.updateById(introduction);
            redisCacheUtil.remove(key);
        }

        UnionUserIntroduction saveUserIntroduction = new UnionUserIntroduction();
        saveUserIntroduction.setId(null);
        saveUserIntroduction.setBusId(busId);
        saveUserIntroduction.setCreateTime(new Date());
        saveUserIntroduction.setModifyTime(new Date());
        saveUserIntroduction.setContent(unionUserIntroduction.getContent());
        saveUserIntroduction.setDelStatus(CommonConstant.DEL_STATUS_NO);

        unionUserIntroductionDao.insert(saveUserIntroduction);
        if (CommonUtil.isNotEmpty(saveUserIntroduction)) {
            redisCacheUtil.set(key, saveUserIntroduction);
        }
    }

}

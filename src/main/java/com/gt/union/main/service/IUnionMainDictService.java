package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainDict;

import java.util.List;

/**
 * <p>
 * 联盟设置申请填写信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainDictService extends IService<UnionMainDict> {

    /**
     * 根据联盟id获取设置的推荐申请信息
     * @param unionId
     * @return
     */
    List<UnionMainDict> list(Integer unionId);
}

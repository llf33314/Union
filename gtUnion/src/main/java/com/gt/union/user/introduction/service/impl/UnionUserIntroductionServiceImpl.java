package com.gt.union.user.introduction.service.impl;

import com.gt.union.user.introduction.dao.IUnionUserIntroductionDao;
import com.gt.union.user.introduction.service.IUnionUserIntroductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    

}
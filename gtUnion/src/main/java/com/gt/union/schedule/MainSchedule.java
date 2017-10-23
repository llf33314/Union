package com.gt.union.schedule;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.ListUtil;
import com.gt.union.main.entity.UnionMainCreate;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.service.IUnionMainCreateService;
import com.gt.union.main.service.IUnionMainPermitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/20 0020.
 */
@Component
public class MainSchedule {
    private Logger logger = LoggerFactory.getLogger(MainSchedule.class);

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionMainCreateService unionMainCreateService;

    /**
     * 每天00:00:00执行
     * 盟主服务许可过期后，自动改为删除状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void disableExpiredPermit() {
        try {
            List<UnionMainPermit> expiredPermitList = this.unionMainPermitService.listExpired();
            if (ListUtil.isNotEmpty(expiredPermitList)) {
                List<UnionMainPermit> updatePermitList = new ArrayList<>();
                for (UnionMainPermit expiredPermit : expiredPermitList) {
                    UnionMainPermit updatePermit = new UnionMainPermit();
                    updatePermit.setId(expiredPermit.getId()); //过期的盟主服务许可id
                    updatePermit.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    updatePermitList.add(updatePermit);
                }
                this.unionMainPermitService.updateBatchById(updatePermitList);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 每天00:20:00执行
     * 盟主服务过期后，联盟创建记录自动改为删除状态
     */
    @Scheduled(cron = "0 20 0 * * ?")
    public void disableExpiredCreate() {
        try {
            List<UnionMainCreate> expiredCreateList = this.unionMainCreateService.listExpired();
            if (ListUtil.isNotEmpty(expiredCreateList)) {
                List<UnionMainCreate> updateCreateList = new ArrayList<>();
                for (UnionMainCreate expiredCreate : expiredCreateList) {
                    UnionMainCreate updateCreate = new UnionMainCreate();
                    updateCreate.setId(expiredCreate.getId()); //对应的联盟创建记录id
                    updateCreate.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    updateCreateList.add(updateCreate);
                }
                this.unionMainCreateService.updateBatchById(updateCreateList);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

package com.gt.union.schedule;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.ListUtil;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/27 0027.
 */
@Component
public class PreferentialSchedule {
    private Logger logger = LoggerFactory.getLogger(PreferentialSchedule.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionPreferentialProjectService unionPreferentialProjectService;

    @Autowired
    private IUnionPreferentialItemService unionPreferentialItemService;

    /**
     * 每天00:00:00执行
     * 优惠项目在其所属的盟员退盟后，自动变成删除状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void disableExpiredProject() {
        try {
            List<UnionPreferentialProject> expiredProjectList = this.unionPreferentialProjectService.listExpired();
            if (ListUtil.isNotEmpty(expiredProjectList)) {
                List<UnionPreferentialProject> updateProjectList = new ArrayList<>();
                for (UnionPreferentialProject expiredProject : expiredProjectList) {
                    UnionPreferentialProject updateProject = new UnionPreferentialProject();
                    updateProject.setId(expiredProject.getId()); //优惠项目id
                    updateProject.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    updateProjectList.add(updateProject);
                }
                this.unionPreferentialProjectService.updateBatchById(updateProjectList);
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

    /**
     * 每天00:20:00执行
     * 优惠服务在优惠项目不存在时，自动变成删除状态
     */
    @Scheduled(cron = "0 20 0 * * ?")
    public void disableExpiredItem() {
        try {
            List<UnionPreferentialItem> expiredItemList = this.unionPreferentialItemService.listExpired();
            if (ListUtil.isNotEmpty(expiredItemList)) {
                List<UnionPreferentialItem> updateItemList = new ArrayList<>();
                for (UnionPreferentialItem expiredItem : expiredItemList) {
                    UnionPreferentialItem updateItem = new UnionPreferentialItem();
                    updateItem.setId(expiredItem.getId()); //优惠服务id
                    updateItem.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    updateItemList.add(updateItem);
                }
                this.unionPreferentialItemService.updateBatchById(updateItemList);
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }
}

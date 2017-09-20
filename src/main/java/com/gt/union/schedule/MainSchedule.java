package com.gt.union.schedule;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.ListUtil;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.main.entity.UnionMainCreate;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.entity.UnionMainTransfer;
import com.gt.union.main.service.IUnionMainCreateService;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.service.IUnionMainTransferService;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 2017/9/20 0020.
 */
@Component
public class MainSchedule {
    private Logger logger = LoggerFactory.getLogger(MainSchedule.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionMainCreateService unionMainCreateService;

    @Autowired
    private IUnionMainTransferService unionMainTransferService;

    @Autowired
    private IUnionPreferentialProjectService unionPreferentialProjectService;

    @Autowired
    private IUnionPreferentialItemService unionPreferentialItemService;

    /**
     * 每天00:00:00执行
     * 盟主服务许可过期后，自动改为删除状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void disableExpiredPermit() {
        try {
            List<UnionMainPermit> expiredPermitList = this.unionMainPermitService.listExpired();
            if (ListUtil.isNotEmpty(expiredPermitList)) {
                for (UnionMainPermit expiredPermit : expiredPermitList) {
                    UnionMainPermit updatePermit = new UnionMainPermit();
                    updatePermit.setId(expiredPermit.getId()); //过期的盟主服务许可id
                    updatePermit.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    this.unionMainPermitService.updateById(updatePermit);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

    /**
     * 每天00:10:00执行
     * 盟主服务过期后，联盟创建记录自动改为删除状态
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void disableExpiredCreate() {
        try {
            List<UnionMainCreate> expiredCreateList = this.unionMainCreateService.listExpired();
            if (ListUtil.isNotEmpty(expiredCreateList)) {
                for (UnionMainCreate expiredCreate : expiredCreateList) {
                    UnionMainCreate updateCreate = new UnionMainCreate();
                    updateCreate.setId(expiredCreate.getId()); //对应的联盟创建记录id
                    updateCreate.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    this.unionMainCreateService.updateById(updateCreate);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

    /**
     * 每天00:20:00执行
     * 盟主权限转移被其中一个人接受后，其余的转移申请自动变成删除状态
     */
    @Scheduled(cron = "0 20 0 * * ?")
    public void disableExpiredTransfer() {
        try {
            List<UnionMainTransfer> expiredTransferList = this.unionMainTransferService.listExpired();
            if (ListUtil.isNotEmpty(expiredTransferList)) {
                for (UnionMainTransfer expiredTransfer : expiredTransferList) {
                    UnionMainTransfer updateTransfer = new UnionMainTransfer();
                    updateTransfer.setId(expiredTransfer.getId()); //对应的盟主权限转移记录id
                    updateTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    this.unionMainTransferService.updateById(updateTransfer);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

    /**
     * 每天00:30:00执行
     * 优惠项目在其所属的盟员退盟后，自动变成删除状态
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void disableExpiredProject() {
        try {
            List<UnionPreferentialProject> expiredProjectList = this.unionPreferentialProjectService.listExpired();
            if (ListUtil.isNotEmpty(expiredProjectList)) {
                for (UnionPreferentialProject expiredProject : expiredProjectList) {
                    UnionPreferentialProject updateProject = new UnionPreferentialProject();
                    updateProject.setId(expiredProject.getId()); //优惠项目id
                    updateProject.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    this.unionPreferentialProjectService.updateById(updateProject);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

    /**
     * 每天00:40:00执行
     * 优惠服务在优惠项目不存在时，自动变成删除状态
     */
    @Scheduled(cron = "0 40 0 * * ?")
    public void disableExpiredItem() {
        try {
            List<UnionPreferentialItem> expiredItemList = this.unionPreferentialItemService.listExpired();
            if (ListUtil.isNotEmpty(expiredItemList)) {
                for (UnionPreferentialItem expiredItem : expiredItemList) {
                    UnionPreferentialItem updateItem = new UnionPreferentialItem();
                    updateItem.setId(expiredItem.getId()); //优惠服务id
                    updateItem.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    this.unionPreferentialItemService.updateById(updateItem);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

}

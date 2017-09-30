package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMainNotice;
import com.gt.union.main.mapper.UnionMainNoticeMapper;
import com.gt.union.main.service.IUnionMainNoticeService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 联盟公告 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainNoticeServiceImpl extends ServiceImpl<UnionMainNoticeMapper, UnionMainNotice> implements IUnionMainNoticeService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、盟员身份id和联盟公告内容，更新保存联盟公告信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param content  {not null} 联盟公告内容
     * @return
     */
    @Override
    public void updateOrSaveByBusIdAndMemberId(Integer busId, Integer memberId, String content) throws Exception {
        if (busId == null || memberId == null || content == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断是否是盟主
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)校验更新内容
        if (StringUtil.isEmpty(content)) {
            throw new BusinessException("联盟公告内容不能为空");
        }
        if (StringUtil.getStringLength(content) > MainConstant.NOTICE_MAX_LENGTH) {
            throw new BusinessException("联盟公告内容不可超过50字");
        }
        //(6)更新操作
        List<UnionMainNotice> noticeList = this.listByUnionId(unionOwner.getUnionId());
        if (ListUtil.isNotEmpty(noticeList)) {
            UnionMainNotice updateNotice = new UnionMainNotice();
            updateNotice.setId(noticeList.get(0).getId()); //联盟公告id
            updateNotice.setModifytime(DateUtil.getCurrentDate()); //更新时间
            updateNotice.setContent(content);  //公告内容
            this.update(updateNotice);
        } else {
            UnionMainNotice saveNotice = new UnionMainNotice();
            saveNotice.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveNotice.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveNotice.setUnionId(unionOwner.getUnionId()); //联盟id
            saveNotice.setContent(content); //公告内容
            this.save(saveNotice);
        }
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    @Override
    public UnionMainNotice getById(Integer noticeId) throws Exception {
        if (noticeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainNotice result;
        //(1)cache
        String noticeIdKey = RedisKeyUtil.getNoticeIdKey(noticeId);
        if (this.redisCacheUtil.exists(noticeIdKey)) {
            String tempStr = this.redisCacheUtil.get(noticeIdKey);
            result = JSONArray.parseObject(tempStr, UnionMainNotice.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", noticeId);
        result = this.selectOne(entityWrapper);
        setCache(result, noticeId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionMainNotice> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainNotice> result;
        //(1)get in cache
        String unionIdKey = RedisKeyUtil.getNoticeUnionIdKey(unionId);
        if (this.redisCacheUtil.exists(unionIdKey)) {
            String tempStr = this.redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainNotice.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper();
        entityWrapper.eq("union_id", unionId);
        result = this.selectList(entityWrapper);
        setCache(result, unionId, MainConstant.REDIS_KEY_NOTICE_UNION_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionMainNotice newNotice) throws Exception {
        if (newNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newNotice);
        this.removeCache(newNotice);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionMainNotice> newNoticeList) throws Exception {
        if (newNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newNoticeList);
        this.removeCache(newNoticeList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer noticeId) throws Exception {
        if (noticeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMainNotice notice = this.getById(noticeId);
        removeCache(notice);
        //(2)remove in db logically
        UnionMainNotice removeNotice = new UnionMainNotice();
        removeNotice.setId(noticeId);
        removeNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeNotice);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> noticeIdList) throws Exception {
        if (noticeIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMainNotice> noticeList = new ArrayList<>();
        for (Integer noticeId : noticeIdList) {
            UnionMainNotice notice = this.getById(noticeId);
            noticeList.add(notice);
        }
        removeCache(noticeList);
        //(2)remove in db logically
        List<UnionMainNotice> removeNoticeList = new ArrayList<>();
        for (Integer noticeId : noticeIdList) {
            UnionMainNotice removeNotice = new UnionMainNotice();
            removeNotice.setId(noticeId);
            removeNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeNoticeList.add(removeNotice);
        }
        this.updateBatchById(removeNoticeList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionMainNotice updateNotice) throws Exception {
        if (updateNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer noticeId = updateNotice.getId();
        UnionMainNotice notice = this.getById(noticeId);
        removeCache(notice);
        //(2)update db
        this.updateById(updateNotice);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionMainNotice> updateNoticeList) throws Exception {
        if (updateNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> noticeIdList = new ArrayList<>();
        for (UnionMainNotice updateNotice : updateNoticeList) {
            noticeIdList.add(updateNotice.getId());
        }
        List<UnionMainNotice> noticeList = new ArrayList<>();
        for (Integer noticeId : noticeIdList) {
            UnionMainNotice notice = this.getById(noticeId);
            noticeList.add(notice);
        }
        removeCache(noticeList);
        //(2)update db
        this.updateBatchById(updateNoticeList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionMainNotice newNotice, Integer noticeId) {
        if (noticeId == null) {
            return; //do nothing,just in case
        }
        String noticeIdKey = RedisKeyUtil.getNoticeIdKey(noticeId);
        this.redisCacheUtil.set(noticeIdKey, newNotice);
    }

    private void setCache(List<UnionMainNotice> newNoticeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_NOTICE_UNION_ID:
                foreignIdKey = RedisKeyUtil.getNoticeUnionIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newNoticeList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMainNotice notice) {
        if (notice == null) {
            return;
        }
        Integer noticeId = notice.getId();
        String noticeIdKey = RedisKeyUtil.getNoticeIdKey(noticeId);
        this.redisCacheUtil.remove(noticeIdKey);
        Integer unionId = notice.getUnionId();
        if (unionId != null) {
            String unionIdKey = RedisKeyUtil.getNoticeUnionIdKey(unionId);
            this.redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMainNotice> noticeList) {
        if (ListUtil.isEmpty(noticeList)) {
            return;
        }
        List<Integer> noticeIdList = new ArrayList<>();
        for (UnionMainNotice notice : noticeList) {
            noticeIdList.add(notice.getId());
        }
        List<String> noticeIdKeyList = RedisKeyUtil.getNoticeIdKey(noticeIdList);
        this.redisCacheUtil.remove(noticeIdKeyList);
        List<String> unionIdKeyList = getForeignIdKeyList(noticeList, MainConstant.REDIS_KEY_NOTICE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            this.redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainNotice> noticeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_NOTICE_UNION_ID:
                for (UnionMainNotice notice : noticeList) {
                    Integer unionId = notice.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = RedisKeyUtil.getNoticeUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}

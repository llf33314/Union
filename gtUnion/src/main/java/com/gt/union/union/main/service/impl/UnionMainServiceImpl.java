package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainDict;
import com.gt.union.union.main.mapper.UnionMainMapper;
import com.gt.union.union.main.service.IUnionMainDictService;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.util.UnionMainCacheUtil;
import com.gt.union.union.main.vo.UnionMainVO;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@Service
public class UnionMainServiceImpl extends ServiceImpl<UnionMainMapper, UnionMain> implements IUnionMainService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMainVO getUnionMainVOByIdAndBusId(Integer unionId, Integer busId) throws Exception {
        if (unionId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (!isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(unionId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }

        UnionMainVO result = new UnionMainVO();
        UnionMain union = getById(unionId);
        result.setUnion(union);
        List<UnionMainDict> itemList = unionMainDictService.listByUnionId(unionId);
        result.setItemList(itemList);

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionMainVO> listOtherValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取有效的memberList
        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        List<Integer> unionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                unionIdList.add(member.getUnionId());
            }
        }
        // （2）	获取有效unionList
        EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .ge("validity", DateUtil.getCurrentDate())
                .notIn("id", unionIdList);

        List<UnionMain> unionList = selectList(entityWrapper);
        List<UnionMainVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionList)) {
            for (UnionMain union : unionList) {
                UnionMainVO vo = new UnionMainVO();
                vo.setUnion(union);
                List<UnionMainDict> itemList = unionMainDictService.listByUnionId(union.getId());
                vo.setItemList(itemList);
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<UnionMainVO> listMyValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取有效的memberList
        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        List<Integer> unionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                unionIdList.add(member.getUnionId());
            }
        }
        // （2）	获取有效unionList
        EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .ge("validity", DateUtil.getCurrentDate())
                .in("id", unionIdList);

        List<UnionMain> unionList = selectList(entityWrapper);
        List<UnionMainVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionList)) {
            for (UnionMain union : unionList) {
                UnionMainVO vo = new UnionMainVO();
                vo.setUnion(union);
                List<UnionMainDict> itemList = unionMainDictService.listByUnionId(union.getId());
                vo.setItemList(itemList);
                result.add(vo);
            }
        }

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Integer countSurplusByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain union = getById(unionId);
        if (union == null) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        List<UnionMember> memberList = unionMemberService.listReadByUnionId(unionId);

        Integer memberLimit = union.getMemberLimit();
        memberLimit = memberLimit != null ? memberLimit : 0;
        Integer memberCount = ListUtil.isNotEmpty(memberList) ? memberList.size() : 0;

        return memberLimit - memberCount;
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    @Override
    public boolean isUnionValid(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMain union = getById(unionId);

        return isUnionValid(union);
    }

    @Override
    public boolean isUnionValid(UnionMain union) throws Exception {
        if (union == null) {
            throw new ParamException(CommonConstant.UNION_INVALID);
        }

        return union.getValidity().compareTo(DateUtil.getCurrentDate()) >= 0;
    }

    //***************************************** Object As a Service - get **********************************************

    @Override
    public UnionMain getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain result;
        // (1)cache
        String idKey = UnionMainCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMain.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    //***************************************** Object As a Service - save *********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMain newUnionMain) throws Exception {
        if (newUnionMain == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMain);
        removeCache(newUnionMain);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMain> newUnionMainList) throws Exception {
        if (newUnionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainList);
        removeCache(newUnionMainList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMain unionMain = getById(id);
        removeCache(unionMain);
        // (2)remove in db logically
        UnionMain removeUnionMain = new UnionMain();
        removeUnionMain.setId(id);
        removeUnionMain.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMain);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMain> unionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain unionMain = getById(id);
            unionMainList.add(unionMain);
        }
        removeCache(unionMainList);
        // (2)remove in db logically
        List<UnionMain> removeUnionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain removeUnionMain = new UnionMain();
            removeUnionMain.setId(id);
            removeUnionMain.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainList.add(removeUnionMain);
        }
        updateBatchById(removeUnionMainList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMain updateUnionMain) throws Exception {
        if (updateUnionMain == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMain.getId();
        UnionMain unionMain = getById(id);
        removeCache(unionMain);
        // (2)update db
        updateById(updateUnionMain);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMain> updateUnionMainList) throws Exception {
        if (updateUnionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMain updateUnionMain : updateUnionMainList) {
            idList.add(updateUnionMain.getId());
        }
        List<UnionMain> unionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain unionMain = getById(id);
            unionMainList.add(unionMain);
        }
        removeCache(unionMainList);
        // (2)update db
        updateBatchById(updateUnionMainList);
    }

    @Override
    public void updateUnionMainVOByIdAndBusId(Integer unionId, Integer busId, UnionMainVO vo) throws Exception {
        if (unionId == null || busId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        if (!isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	校验表单
        UnionMain updateUnion = new UnionMain();
        updateUnion.setId(unionId);
        updateUnion.setModifyTime(DateUtil.getCurrentDate());
        UnionMain voUnion = vo.getUnion();
        if (voUnion == null) {
            throw new BusinessException("请填写联盟设置信息");
        }
        // （2-1）联盟名称
        String unionName = voUnion.getName();
        if (StringUtil.isEmpty(unionName)) {
            throw new BusinessException("联盟名称不能为空");
        }
        if (StringUtil.getStringLength(unionName) > 5) {
            throw new BusinessException("联盟名称字数不能大于5");
        }
        updateUnion.setName(unionName);
        // （2-2）联盟图标
        String unionImg = voUnion.getImg();
        if (StringUtil.isEmpty(unionImg)) {
            throw new BusinessException("联盟图标不能为空");
        }
        updateUnion.setImg(unionImg);
        // （2-3）联盟说明
        String unionIllustration = voUnion.getIllustration();
        if (StringUtil.isEmpty(unionIllustration)) {
            throw new BusinessException("联盟说明不能为空");
        }
        if (StringUtil.getStringLength(unionIllustration) > 20) {
            throw new BusinessException("联盟说明字数不能大于20");
        }
        updateUnion.setIllustration(unionIllustration);
        // （2-4）加盟方式
        Integer unionJoinType = voUnion.getJoinType();
        if (unionJoinType == null) {
            throw new BusinessException("加盟方式不能为空");
        }
        if (UnionConstant.JOIN_TYPE_RECOMMEND != unionJoinType && UnionConstant.JOIN_TYPE_APPLY_RECOMMEND != unionJoinType) {
            throw new BusinessException("加盟方式参数值有误");
        }
        updateUnion.setJoinType(unionJoinType);
        // （2-5）是否开启积分
        UnionMain union = getById(unionId);
        if (UnionConstant.IS_INTEGRAL_YES != union.getIsIntegral()) {
            Integer unionIsIntegral = voUnion.getIsIntegral();
            if (UnionConstant.IS_INTEGRAL_YES == unionIsIntegral) {
                updateUnion.setIsIntegral(UnionConstant.IS_INTEGRAL_YES);
            }
        }
        // （2-6）入盟申请必填信息
        List<Integer> removeDictIdList = new ArrayList<>();
        List<UnionMainDict> dictList = unionMainDictService.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(dictList)) {
            for (UnionMainDict dict : dictList) {
                removeDictIdList.add(dict.getId());
            }
        }
        List<UnionMainDict> saveDictList = vo.getItemList();
        for (UnionMainDict dict : saveDictList) {
            if (!dict.getUnionId().equals(unionId)) {
                throw new BusinessException("存在错误的入盟申请必填信息");
            }
        }

        // 事务操作
        update(updateUnion);
        unionMainDictService.removeBatchById(removeDictIdList);
        unionMainDictService.saveBatch(saveDictList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMain newUnionMain, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMain);
    }

    private void setCache(List<UnionMain> newUnionMainList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainList);
        }
    }

    private void removeCache(UnionMain unionMain) {
        if (unionMain == null) {
            return;
        }
        Integer id = unionMain.getId();
        String idKey = UnionMainCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionMain> unionMainList) {
        if (ListUtil.isEmpty(unionMainList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMain unionMain : unionMainList) {
            idList.add(unionMain.getId());
        }
        List<String> idKeyList = UnionMainCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionMain> unionMainList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }
}
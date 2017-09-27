package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMainNotice;
import com.gt.union.main.mapper.UnionMainNoticeMapper;
import com.gt.union.main.service.IUnionMainNoticeService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id，获取联盟公告
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainNotice getByUnionId(Integer unionId) {
        EntityWrapper wrapper = new EntityWrapper<UnionMainNotice>();
        wrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(wrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------
    //------------------------------------------------- update --------------------------------------------------------

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
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)校验更新内容
        if (StringUtil.isEmpty(content)) {
            throw new BusinessException("联盟公告内容不能为空");
        }
        if (StringUtil.getStringLength(content) > MainConstant.NOTICE_MAX_LENGTH) {
            throw new BusinessException("联盟公告内容不可超过50字");
        }
        //(5)更新操作
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        UnionMainNotice unionMainNotice = this.getByUnionId(unionOwner.getUnionId());
        if (unionMainNotice != null) {
            UnionMainNotice updateNotice = new UnionMainNotice();
            updateNotice.setId(unionMainNotice.getId()); //联盟公告id
            updateNotice.setModifytime(DateUtil.getCurrentDate()); //更新时间
            updateNotice.setContent(content);  //公告内容
            this.updateById(updateNotice);
        } else {
            UnionMainNotice saveNotice = new UnionMainNotice();
            saveNotice.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveNotice.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveNotice.setUnionId(unionOwner.getUnionId()); //联盟id
            saveNotice.setContent(content); //公告内容
            this.insert(saveNotice);
        }
    }

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

}

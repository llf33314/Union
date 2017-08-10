package com.gt.union.vo.basic;

import com.gt.union.entity.basic.UnionApply;
import com.gt.union.entity.basic.UnionApplyInfo;

/**
 * Created by Administrator on 2017/7/24 0024.
 */
public class UnionApplyVO {
    /**
     * 盟员商家帐号
     */
    private String userName = null;

    /**
     * 入盟申请
     */
    private UnionApply unionApply = null;

    /**
     * 入盟申请信息
     */
    UnionApplyInfo unionApplyInfo = null;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UnionApply getUnionApply() {
        return unionApply;
    }

    public void setUnionApply(UnionApply unionApply) {
        this.unionApply = unionApply;
    }

    public UnionApplyInfo getUnionApplyInfo() {
        return unionApplyInfo;
    }

    public void setUnionApplyInfo(UnionApplyInfo unionApplyInfo) {
        this.unionApplyInfo = unionApplyInfo;
    }
}

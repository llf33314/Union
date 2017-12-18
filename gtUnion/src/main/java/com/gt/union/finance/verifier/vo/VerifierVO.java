package com.gt.union.finance.verifier.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 佣金管理人员
 *
 * @author linweicong
 * @version 2017-12-18 08:47:39
 */
@ApiModel(value = "佣金管理人员VO")
public class VerifierVO {
    @ApiModelProperty(value = "超级管理者")
    private UnionVerifier adminVerifier;

    @ApiModelProperty(value = "管理人员列表")
    private List<UnionVerifier> verifierList;

    public UnionVerifier getAdminVerifier() {
        return adminVerifier;
    }

    public void setAdminVerifier(UnionVerifier adminVerifier) {
        this.adminVerifier = adminVerifier;
    }

    public List<UnionVerifier> getVerifierList() {
        return verifierList;
    }

    public void setVerifierList(List<UnionVerifier> verifierList) {
        this.verifierList = verifierList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}

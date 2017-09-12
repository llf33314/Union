package com.gt.union.main.vo;

import com.gt.union.member.vo.UnionMemberVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
@ApiModel(value = "UnionMainCreateVO", description = "创建联盟实体")
@Data
public class UnionMainCreateVO {
    /**
     * 联盟信息
     */
    @Valid
    private UnionMainVO unionMainVO;

    /**
     * 盟主信息
     */
    @Valid
    private UnionMemberVO unionMemberVO;

    /**
     * 联盟服务许可id
     */
    private Integer permitId;

    public UnionMainVO getUnionMainVO() {
        return unionMainVO;
    }

    public void setUnionMainVO(UnionMainVO unionMainVO) {
        this.unionMainVO = unionMainVO;
    }

    public UnionMemberVO getUnionMemberVO() {
        return unionMemberVO;
    }

    public void setUnionMemberVO(UnionMemberVO unionMemberVO) {
        this.unionMemberVO = unionMemberVO;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }
}

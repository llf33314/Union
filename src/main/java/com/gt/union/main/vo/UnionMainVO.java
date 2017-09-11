package com.gt.union.main.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import com.gt.union.main.entity.UnionMainDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
@ApiModel(value = "UnionMainVO", description = "联盟主实体")
@Data
public class UnionMainVO {
    /**
     * 联盟名称
     */
    @ApiModelProperty(value = "联盟名称，不可超过5字", required = true)
    @NotNull(message = "联盟名称内容不能为空")
    @StringLengthValid(length = 5, message = "联盟名称内容不可超过5字")
    private String unionName;

    /**
     * 联盟图标
     */
    @NotBlank(message = "请设置联盟图标")
    @ApiModelProperty(value = "联盟图标，从素材库选择", required = true)
    private String unionImg;

    /**
     * 加盟方式（1：推荐 2：申请、推荐）
     */
    @ApiModelProperty(value = "加盟方式（1：推荐 2：申请、推荐）", required = true)
    @NotNull(message = "请选择加入方式")
    private Integer joinType;

    /**
     * 联盟说明
     */
    @NotBlank(message = "联盟说明内容不能为空")
    @ApiModelProperty(value = "联盟说明内容，不可超过20字", required = true)
    @StringLengthValid(length = 20, message = "联盟说明内容不可超过20字")
    private String unionIllustration;

    /**
     * 联盟是否开启积分（0：否 1：是）
     */
    @NotNull(message = "请选择是否开启积分")
    @ApiModelProperty(value = "联盟是否开启积分（0：否 1：是）", required = true)
    private Integer isIntegral;

    /**
     * 联盟升级收费实体
     */
    private UnionMainChargeVO unionMainChargeVO;

    /**
     * 联盟申请/推荐收集信息列表
     */
    @NotNull(message = "请设置收集信息")
    @ApiModelProperty(value = "联盟申请/推荐收集信息列表", required = true)
    private List<UnionMainDict> unionMainDictList;

    public String getUnionName() {
        return unionName;
    }

    public void setUnionName(String unionName) {
        this.unionName = unionName;
    }

    public String getUnionImg() {
        return unionImg;
    }

    public void setUnionImg(String unionImg) {
        this.unionImg = unionImg;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }

    public String getUnionIllustration() {
        return unionIllustration;
    }

    public void setUnionIllustration(String unionIllustration) {
        this.unionIllustration = unionIllustration;
    }

    public Integer getIsIntegral() {
        return isIntegral;
    }

    public void setIsIntegral(Integer isIntegral) {
        this.isIntegral = isIntegral;
    }

    public UnionMainChargeVO getUnionMainChargeVO() {
        return unionMainChargeVO;
    }

    public void setUnionMainChargeVO(UnionMainChargeVO unionMainChargeVO) {
        this.unionMainChargeVO = unionMainChargeVO;
    }

    public List<UnionMainDict> getUnionMainDictList() {
        return unionMainDictList;
    }

    public void setUnionMainDictList(List<UnionMainDict> unionMainDictList) {
        this.unionMainDictList = unionMainDictList;
    }
}

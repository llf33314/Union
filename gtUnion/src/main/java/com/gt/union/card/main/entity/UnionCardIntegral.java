package com.gt.union.card.main.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 联盟积分
 *
 * @author linweicong
 * @version 2017-09-01 11:34:16
 */
@ApiModel(value = "联盟积分")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_card_integral")
public class UnionCardIntegral extends Model<UnionCardIntegral> {
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    
    /**
     * 是否删除(0:否 1:是)
     */
    @ApiModelProperty(value = "是否删除(0:否 1:是)")
    @TableField("del_status")
    private Integer delStatus;
    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;
    
    /**
     * 粉丝id
     */
    @ApiModelProperty(value = "粉丝id")
    @TableField("fan_id")
    private Integer fanId;
    
    /**
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;
    
    /**
     * 可用积分
     */
    @ApiModelProperty(value = "可用积分")
    @TableField("integral")
    private Double integral;
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }
    
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getFanId() {
        return fanId;
    }

    public void setFanId(Integer fanId) {
        this.fanId = fanId;
    }
    
    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }
    
    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
package com.gt.union.member.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 退盟
 * 
 * @author linweicong
 * @version 2017-11-24 18:21:32
 */
@ApiModel(value = "退盟")
public class OutVO {
    @ApiModelProperty(value = "退盟id")
    private Integer outId;
    
    @ApiModelProperty(value = "退盟申请时间")
    private Date outCreateTime;
    
    @ApiModelProperty(value = "盟员名称")
    private String memberName;
    
    @ApiModelProperty(value = "退盟理由")
    private String outReason;
    
    @ApiModelProperty(value = "退盟类型(1:自己申请 2:盟主移出)")
    private Integer outType;
    
    @ApiModelProperty(value = "退盟确认时间")
    private Date confirmOutTime;
    
    @ApiModelProperty(value = "退盟过渡期剩余天数")
    private Integer periodDay;
    
}

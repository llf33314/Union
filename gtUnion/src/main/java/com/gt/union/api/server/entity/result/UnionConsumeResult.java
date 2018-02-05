package com.gt.union.api.server.entity.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 联盟卡核销结果
 *
 * @author hongjiye
 * @time 2017/9/4 0004.
 */
@ApiModel(value = "UnionConsumeResult", description = "联盟卡核销结果实体")
@Data
public class UnionConsumeResult {

    @ApiModelProperty(value = "提示信息")
    private String message;

    @ApiModelProperty(value = "消费状态 true：成功 false：失败")
    private boolean success;

    public UnionConsumeResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public UnionConsumeResult() {
        this.success = false;
        this.message = "核销失败";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

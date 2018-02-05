package com.gt.union.api.server.entity.result;

import io.swagger.annotations.ApiModelProperty;

/**
 * 联盟卡退款结果
 *
 * @author hongjiye
 * @time 2017/9/4 0004.
 */
public class UnionRefundResult {

    @ApiModelProperty(value = "退还状态 true：成功 false：失败")
    private boolean success;

    @ApiModelProperty(value = "提示信息")
    private String message;

    public UnionRefundResult() {
        this.success = false;
        this.message = "退还失败";
    }

    public UnionRefundResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

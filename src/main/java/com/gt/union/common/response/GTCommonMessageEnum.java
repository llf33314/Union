package com.gt.union.common.response;

/**
 * 公共的枚举类
 *
 * @create 2017/5/5
 */
public enum GTCommonMessageEnum {

    FAIL("失败"),
    SUCCESS("成功");

    /*描述*/
    private String message;


    GTCommonMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

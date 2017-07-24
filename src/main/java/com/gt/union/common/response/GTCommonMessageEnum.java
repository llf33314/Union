package com.gt.union.common.response;

/**
 * 公共的枚举类
 *
 * @create 2017/5/5
 */
public enum GTCommonMessageEnum {

    FAIL(0, "失败"),
    SUCCESS(1, "成功"),
    SYS_ERROR(9999, "系统异常");

    /*代码*/
    private int code;
    /*描述*/
    private String message;


    GTCommonMessageEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取 msg 值
     *
     * @param messageEnum code
     * @return msg
     */
    public static String getReturnMsg(GTCommonMessageEnum messageEnum) {
        for (GTCommonMessageEnum msg : values()) {
            if (msg.getCode() == messageEnum.getCode()) {
                return msg.getMessage();
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

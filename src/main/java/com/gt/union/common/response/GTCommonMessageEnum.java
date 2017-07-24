package com.gt.common.message;

/**
 * 公共的枚举类
 *
 * @author zhangmz
 * @create 2017/5/5
 */
public enum GTCommonMessageEnum {
    SUCCESS(1, "成功"),
    FAIL(0, "失败"),
    FAILED_TO_GET_THE_PRINTER(201, "获取打印机设置失败"),
    PRINTER_CONFIGURATION_ALREADY_EXISTENCE(202, "打印机设置已存在"),
    PRINTER_DOES_NOT_EXIST(203, "打印机设置不存在"),
    SYS_ERROR(9999, "系统异常");

    /*代码*/
    private int code;
    /*描述*/
    private String msg;


    GTCommonMessageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
                return msg.getMsg();
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

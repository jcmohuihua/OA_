package com.web.oa.exception;

/**
 * @author mhh
 * 2020/8/29 0029 - 上午 10:04
 * <p>
 * 异常分装
 */
public enum ErrorCode {
    NULL_OBJ("001", "对象为空"),
    UNKNOWN_ERROR("002", "系统繁忙，请稍后再试..."),
    ;

    private String value;
    private String desc;

    ErrorCode(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "[" + value + "]" + desc;
    }
}

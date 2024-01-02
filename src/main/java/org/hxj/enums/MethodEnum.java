package org.hxj.enums;

/**
 * @authorxiaojun
 * @date2024/1/2 21:17
 */
public enum MethodEnum {
    SELECT_BY_ID("selectById","查询"),DELETE_BY_ID("deleteById","删除");

    private String methodName;
    private String methodDesc;

    MethodEnum(String methodName, String methodDesc) {
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    public String getMethodName() {
        return methodName;
    }


    public String getMethodDesc() {
        return methodDesc;
    }

}

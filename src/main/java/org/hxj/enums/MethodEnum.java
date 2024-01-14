package org.hxj.enums;

/**
 * @authorxiaojun
 * @date2024/1/2 21:17
 */
public enum MethodEnum {
    SELECT_BY_ID("selectById","根据主键查询"),SELECT_BY_PARAM("selectByParam","根据字段查询")
    ,DELETE_BY_ID("deleteById","根据主键删除"),UPDATE_BY_ID("updateById","根据ID修改");

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

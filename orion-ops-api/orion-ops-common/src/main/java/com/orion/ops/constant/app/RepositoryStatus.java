package com.orion.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用版本仓库状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/28 14:06
 */
@AllArgsConstructor
@Getter
public enum RepositoryStatus {

    /**
     * 未初始化
     */
    UNINITIALIZED(10),

    /**
     * 初始化中
     */
    INITIALIZING(20),

    /**
     * 正常
     */
    OK(30),

    /**
     * 失败
     */
    ERROR(40),

    ;

    /**
     * 状态
     */
    private final Integer status;

    public static RepositoryStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (RepositoryStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}

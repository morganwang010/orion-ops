package com.orion.ops.constant.webhook;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * webhook 类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 17:39
 */
@Getter
@AllArgsConstructor
public enum WebhookType {

    /**
     * 钉钉机器人
     */
    DING_ROBOT(10),

    ;

    private final Integer type;

    public static WebhookType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (WebhookType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}

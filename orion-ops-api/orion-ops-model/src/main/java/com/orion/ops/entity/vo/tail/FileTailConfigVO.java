package com.orion.ops.entity.vo.tail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * tail配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/2 0:50
 */
@Data
@ApiModel(value = "tail配置响应")
@SuppressWarnings("ALL")
public class FileTailConfigVO {

    /**
     * @see com.orion.ops.constant.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * @see com.orion.ops.constant.Const#UTF_8
     */
    @ApiModelProperty(value = "文件编码")
    private String charset;

    /**
     * @see com.orion.ops.constant.machine.MachineEnvAttr#TAIL_DEFAULT_COMMAND
     */
    @ApiModelProperty(value = "命令")
    private String command;

}

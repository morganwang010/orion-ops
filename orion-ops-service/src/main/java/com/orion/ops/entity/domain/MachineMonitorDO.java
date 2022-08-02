package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器监控配置表
 *
 * @author Jiahang Li
 * @since 2022-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "机器监控配置表")
@TableName("machine_monitor")
public class MachineMonitorDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.monitor.InstallStatus
     */
    @ApiModelProperty(value = "插件安装状态 1未安装 2安装中 3已安装")
    @TableField("install_status")
    private Integer installStatus;

    @ApiModelProperty(value = "请求 api url")
    @TableField("monitor_url")
    private String monitorUrl;

    @ApiModelProperty(value = "请求 api accessToken")
    @TableField("access_token")
    private String accessToken;

    @ApiModelProperty(value = "插件版本")
    @TableField("agent_version")
    private String agentVersion;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}

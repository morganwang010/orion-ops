package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度任务执行明细机器详情
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "调度任务执行明细机器详情")
@TableName("scheduler_task_machine_record")
@SuppressWarnings("ALL")
public class SchedulerTaskMachineRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "明细id")
    @TableField("record_id")
    private Long recordId;

    @ApiModelProperty(value = "任务机器id")
    @TableField("task_machine_id")
    private Long taskMachineId;

    @ApiModelProperty(value = "执行机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    @TableField("machine_name")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    @TableField("machine_host")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    @TableField("machine_tag")
    private String machineTag;

    @ApiModelProperty(value = "执行命令")
    @TableField("exec_command")
    private String execCommand;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskMachineStatus
     */
    @ApiModelProperty(value = "执行状态 10待调度 20调度中 30调度成功 40调度失败 50已跳过 60已停止")
    @TableField("exec_status")
    private Integer execStatus;

    @ApiModelProperty(value = "退出码")
    @TableField("exit_code")
    private Integer exitCode;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "日志路径")
    @TableField("log_path")
    private String logPath;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}

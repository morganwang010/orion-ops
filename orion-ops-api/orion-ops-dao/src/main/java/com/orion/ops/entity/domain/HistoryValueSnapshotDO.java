package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史值快照表
 *
 * @author Jiahang Li
 * @since 2021-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "历史值快照表")
@TableName("history_value_snapshot")
@SuppressWarnings("ALL")
public class HistoryValueSnapshotDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "值id")
    @TableField("value_id")
    private Long valueId;

    /**
     * @see com.orion.ops.constant.history.HistoryOperator
     */
    @ApiModelProperty(value = "操作类型 1新增 2修改 3删除")
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * @see com.orion.ops.constant.history.HistoryValueType
     */
    @ApiModelProperty(value = "值类型 10机器环境变量 20应用环境变量")
    @TableField("value_type")
    private Integer valueType;

    @ApiModelProperty(value = "原始值")
    @TableField("before_value")
    private String beforeValue;

    @ApiModelProperty(value = "新值")
    @TableField("after_value")
    private String afterValue;

    @ApiModelProperty(value = "修改人id")
    @TableField("update_user_id")
    private Long updateUserId;

    @ApiModelProperty(value = "修改人用户名")
    @TableField("update_user_name")
    private String updateUserName;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}

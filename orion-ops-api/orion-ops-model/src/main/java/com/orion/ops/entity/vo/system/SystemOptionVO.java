package com.orion.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 22:53
 */
@Data
@ApiModel(value = "系统配置响应")
@SuppressWarnings("ALL")
public class SystemOptionVO {

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#ENABLE_AUTO_CLEAN_FILE
     */
    @ApiModelProperty(value = "是否启用自动清理")
    private Boolean autoCleanFile;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#FILE_CLEAN_THRESHOLD
     */
    @ApiModelProperty(value = "文件清理阈值")
    private Integer fileCleanThreshold;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#ALLOW_MULTIPLE_LOGIN
     */
    @ApiModelProperty(value = "允许多端登陆")
    private Boolean allowMultipleLogin;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOGIN_FAILURE_LOCK
     */
    @ApiModelProperty(value = "是否启用登陆失败锁定")
    private Boolean loginFailureLock;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOGIN_IP_BIND
     */
    @ApiModelProperty(value = "是否启用登陆IP绑定")
    private Boolean loginIpBind;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOGIN_TOKEN_AUTO_RENEW
     */
    @ApiModelProperty(value = "是否启用登陆IP绑定")
    private Boolean loginTokenAutoRenew;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOGIN_TOKEN_EXPIRE
     */
    @ApiModelProperty(value = "登陆凭证有效期")
    private Integer loginTokenExpire;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOGIN_FAILURE_LOCK_THRESHOLD
     */
    @ApiModelProperty(value = "登陆失败锁定阈值")
    private Integer loginFailureLockThreshold;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOGIN_TOKEN_AUTO_RENEW_THRESHOLD
     */
    @ApiModelProperty(value = "登陆自动续签阈值")
    private Integer loginTokenAutoRenewThreshold;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#RESUME_ENABLE_SCHEDULER_TASK
     */
    @ApiModelProperty(value = "自动恢复启用的调度任务")
    private Boolean resumeEnableSchedulerTask;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#SFTP_UPLOAD_THRESHOLD
     */
    @ApiModelProperty(value = "SFTP 上传文件最大阈值 (MB)")
    private Integer sftpUploadThreshold;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#STATISTICS_CACHE_EXPIRE
     */
    @ApiModelProperty(value = "统计缓存有效时间 (分)")
    private Integer statisticsCacheExpire;

}

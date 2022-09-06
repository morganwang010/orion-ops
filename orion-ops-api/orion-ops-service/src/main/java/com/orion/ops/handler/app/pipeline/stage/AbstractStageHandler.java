package com.orion.ops.handler.app.pipeline.stage;

import com.orion.lang.utils.Exceptions;
import com.orion.ops.constant.app.PipelineDetailStatus;
import com.orion.ops.constant.app.PipelineLogStatus;
import com.orion.ops.constant.app.StageType;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.utils.UserHolder;
import com.orion.ops.dao.ApplicationPipelineTaskDetailDAO;
import com.orion.ops.dao.ApplicationPipelineTaskLogDAO;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskLogDO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.spring.SpringHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;

/**
 * 流水线阶段处理器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 17:03
 */
@Slf4j
public abstract class AbstractStageHandler implements IStageHandler {

    protected static final ApplicationPipelineTaskDetailDAO applicationPipelineTaskDetailDAO = SpringHolder.getBean(ApplicationPipelineTaskDetailDAO.class);

    protected static final ApplicationPipelineTaskLogDAO applicationPipelineTaskLogDAO = SpringHolder.getBean(ApplicationPipelineTaskLogDAO.class);

    protected static final UserInfoDAO userInfoDAO = SpringHolder.getBean(UserInfoDAO.class);

    protected Long detailId;

    protected ApplicationPipelineTaskDO task;

    protected ApplicationPipelineTaskDetailDO detail;

    protected StageType stageType;

    @Getter
    protected volatile PipelineDetailStatus status;

    protected volatile boolean terminated;

    public AbstractStageHandler(ApplicationPipelineTaskDO task, ApplicationPipelineTaskDetailDO detail) {
        this.task = task;
        this.detail = detail;
        this.detailId = detail.getId();
        this.status = PipelineDetailStatus.WAIT;
    }

    @Override
    public void exec() {
        log.info("流水线阶段操作-开始执行 detailId: {}", detailId);
        // 状态检查
        if (!this.checkCanRunnable()) {
            return;
        }
        Exception ex = null;
        // 执行
        try {
            // 更新状态
            this.updateStatus(PipelineDetailStatus.RUNNABLE);
            // 执行操作
            this.execStageTask();
        } catch (Exception e) {
            ex = e;
        }
        // 回调
        try {
            if (terminated) {
                // 停止回调
                this.terminatedCallback();
            } else if (ex == null) {
                // 成功回调
                this.successCallback();
            } else {
                // 异常回调
                this.exceptionCallback(ex);
                throw Exceptions.runtime(ex.getMessage(), ex);
            }
        } finally {
            this.close();
        }
    }

    /**
     * 执行操作任务
     */
    protected abstract void execStageTask();

    /**
     * 检查是否可执行
     *
     * @return 是否可执行
     */
    protected boolean checkCanRunnable() {
        ApplicationPipelineTaskDetailDO detail = applicationPipelineTaskDetailDAO.selectById(detailId);
        if (detail == null) {
            return false;
        }
        return PipelineDetailStatus.WAIT.getStatus().equals(detail.getExecStatus());
    }

    /**
     * 停止回调
     */
    protected void terminatedCallback() {
        log.info("流水线阶段操作-终止回调 detailId: {}", detailId);
        // 修改状态
        this.updateStatus(PipelineDetailStatus.TERMINATED);
    }

    /**
     * 成功回调
     */
    protected void successCallback() {
        log.info("流水线阶段操作-成功回调 detailId: {}", detailId);
        // 修改状态
        this.updateStatus(PipelineDetailStatus.FINISH);
    }

    /**
     * 异常回调
     *
     * @param ex ex
     */
    protected void exceptionCallback(Exception ex) {
        log.error("流水线阶段操作-异常回调 detailId: {}", detailId, ex);
        // 修改状态
        this.updateStatus(PipelineDetailStatus.FAILURE);
    }

    /**
     * 更新状态
     *
     * @param status 状态
     */
    protected void updateStatus(PipelineDetailStatus status) {
        this.status = status;
        Date now = new Date();
        ApplicationPipelineTaskDetailDO update = new ApplicationPipelineTaskDetailDO();
        update.setId(detailId);
        update.setExecStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                update.setExecStartTime(now);
                break;
            case FINISH:
            case FAILURE:
            case TERMINATED:
                update.setExecEndTime(now);
                break;
            default:
                break;
        }
        // 更新
        applicationPipelineTaskDetailDAO.updateById(update);
        // 插入日志
        String appName = detail.getAppName();
        switch (status) {
            case FINISH:
                this.addLog(PipelineLogStatus.SUCCESS, appName);
                break;
            case FAILURE:
                this.addLog(PipelineLogStatus.FAILURE, appName);
                break;
            case SKIPPED:
                this.addLog(PipelineLogStatus.SKIP, appName);
                break;
            case TERMINATED:
                this.addLog(PipelineLogStatus.TERMINATED, appName);
                break;
            default:
                break;
        }
    }

    @Override
    public void terminate() {
        log.info("流水线阶段操作-终止 detailId: {}", detailId);
        this.terminated = true;
    }

    @Override
    public void skip() {
        log.info("流水线阶段操作-跳过 detailId: {}", detailId);
        if (PipelineDetailStatus.WAIT.equals(status)) {
            // 只能跳过等待中的任务
            this.updateStatus(PipelineDetailStatus.SKIPPED);
        }
    }

    /**
     * 设置引用id
     *
     * @param relId relId
     */
    protected void setRelId(Long relId) {
        ApplicationPipelineTaskDetailDO update = new ApplicationPipelineTaskDetailDO();
        update.setId(detailId);
        update.setRelId(relId);
        update.setUpdateTime(new Date());
        applicationPipelineTaskDetailDAO.updateById(update);
    }

    /**
     * 添加日志
     *
     * @param logStatus logStatus
     * @param params    日志参数
     */
    protected void addLog(PipelineLogStatus logStatus, Object... params) {
        ApplicationPipelineTaskLogDO log = new ApplicationPipelineTaskLogDO();
        log.setTaskId(task.getId());
        log.setTaskDetailId(detail.getId());
        log.setLogStatus(logStatus.getStatus());
        log.setStageType(stageType.getType());
        log.setLogInfo(logStatus.format(stageType, params));
        applicationPipelineTaskLogDAO.insert(log);
    }

    /**
     * 设置执行人用户上下文
     */
    protected void setExecuteUserContext() {
        UserInfoDO userInfo = userInfoDAO.selectById(task.getExecUserId());
        UserDTO user = new UserDTO();
        user.setId(task.getExecUserId());
        user.setUsername(task.getExecUserName());
        Integer roleType = Optional.ofNullable(userInfo)
                .map(UserInfoDO::getRoleType)
                .orElseGet(() -> task.getExecUserId().equals(task.getAuditUserId())
                        ? RoleType.ADMINISTRATOR.getType()
                        : RoleType.DEVELOPER.getType());
        user.setRoleType(roleType);
        UserHolder.set(user);
    }

    /**
     * 释放资源
     */
    protected void close() {
        UserHolder.remove();
    }

}

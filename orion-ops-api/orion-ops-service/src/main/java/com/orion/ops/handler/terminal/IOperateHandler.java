package com.orion.ops.handler.terminal;

import com.orion.lang.able.SafeCloseable;
import com.orion.ops.constant.terminal.TerminalOperate;
import com.orion.ops.entity.config.TerminalConnectConfig;
import com.orion.ops.handler.terminal.manager.TerminalManagementHandler;
import com.orion.ops.handler.terminal.watcher.ITerminalWatcherProcessor;

/**
 * 操作处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 22:56
 */
public interface IOperateHandler extends TerminalManagementHandler, SafeCloseable {

    /**
     * 建立连接
     */
    void connect();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 处理消息
     *
     * @param operate 操作
     * @param body    body
     * @throws Exception ex
     */
    void handleMessage(TerminalOperate operate, String body) throws Exception;

    /**
     * 心跳是否结束
     *
     * @return true结束
     */
    boolean isDown();

    /**
     * 获取token
     *
     * @return token
     */
    String getToken();

    /**
     * 获取终端配置
     *
     * @return 终端配置
     */
    TerminalConnectConfig getHint();

    /**
     * 获取监视器
     *
     * @return processor
     */
    ITerminalWatcherProcessor getWatcher();

}

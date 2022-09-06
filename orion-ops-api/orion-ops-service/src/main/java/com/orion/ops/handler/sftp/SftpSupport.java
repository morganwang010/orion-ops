package com.orion.ops.handler.sftp;

import com.orion.lang.id.UUIds;
import com.orion.lang.utils.io.Files1;
import com.orion.net.remote.channel.sftp.SftpExecutor;
import com.orion.ops.constant.sftp.SftpTransferStatus;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.handler.sftp.impl.UploadFileProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * sftp工具
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/13 11:05
 */
@Slf4j
public class SftpSupport {

    private SftpSupport() {
    }

    /**
     * 检查远程机器和本机是否是同一台机器
     *
     * @param executor executor
     * @return 是否为本机
     */
    public static boolean checkUseFileSystem(SftpExecutor executor) {
        // 创建一个临时文件
        String checkPath = Files1.getPath(SystemEnvAttr.TEMP_PATH.getValue(), UUIds.random32() + ".ck");
        File checkFile = new File(checkPath);
        Files1.touch(checkFile);
        checkFile.deleteOnExit();
        // 查询远程机器是否有此文件 如果有则证明传输机器和宿主机是同一台
        boolean exist = executor.getFile(checkFile.getAbsolutePath()) != null;
        Files1.delete(checkFile);
        return exist;
    }

    /**
     * 使用 file system copy
     *
     * @param processor processor
     */
    public static void usingFsCopy(FileTransferProcessor processor) {
        String remoteFile = processor.record.getRemoteFile();
        String localFile = processor.record.getLocalFile();
        String localAbsolutePath = Files1.getPath(SystemEnvAttr.SWAP_PATH.getValue(), localFile);
        log.info("sftp文件传输-使用FSC fileToken: {}, machineId: {}, local: {}, remote: {}",
                processor.fileToken, processor.machineId, localAbsolutePath, remoteFile);
        // 复制
        File sourceFile;
        File targetFile;
        if (processor instanceof UploadFileProcessor) {
            sourceFile = new File(localAbsolutePath);
            targetFile = new File(remoteFile);
        } else {
            sourceFile = new File(remoteFile);
            targetFile = new File(localAbsolutePath);
        }
        Files1.copy(sourceFile, targetFile);
        // 通知进度
        long fileSize = sourceFile.length();
        processor.notifyProgress(Files1.getSize(fileSize), Files1.getSize(fileSize), "100");
        // 通知状态
        processor.updateStatusAndNotify(SftpTransferStatus.FINISH.getStatus(), 100D, fileSize);
    }

}

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.service.impl;

import com.huotu.huobanplus.base.service.imp.FileObjectFunction;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author CJ
 */
@Service
public class VFSHelper {

    private final FileSystemOptions options = new FileSystemOptions();
    private boolean passive;

    public VFSHelper() {
        super();
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 30000);

        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        FtpFileSystemConfigBuilder.getInstance().setDataTimeout(options, 30000);
        FtpFileSystemConfigBuilder.getInstance().setSoTimeout(options, 30000);
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive);
    }

    public <R> R handle(String name, FileObjectFunction<FileObject, R> function) throws IOException {
        StandardFileSystemManager manager = new StandardFileSystemManager();
        manager.init();
        FileSystem fileSystem = null;
        try {
            FileObject file = resolveFile(name, manager);
            fileSystem = file.getFileSystem();
            try {
                if (function != null)
                    return function.apply(file);
                return null;
            } catch (FileSystemException ex) {
                togglePassive();
                return function.apply(file);
            } finally {
                file.close();
            }
        } finally {
            if (fileSystem != null)
                manager.closeFileSystem(fileSystem);
            manager.close();
        }
    }

    public FileObject resolveFile(String name, FileSystemManager manager) throws FileSystemException {
        return manager.resolveFile(name, options);
    }

    public void togglePassive() {
        passive = !passive;
        FtpFileSystemConfigBuilder.getInstance().setPassiveMode(options, passive);
    }
}

package com.starcloud.ops.business.app.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.NOT_ZIP_PACKAGE;

public class UnpackUtils {

    public static void unpack(File packFile, File outDir) throws IOException, RarException, InterruptedException {
        String[] fileNameSplit = packFile.getName().split("\\.");
        if (fileNameSplit.length < 2) {
            throw exception(NOT_ZIP_PACKAGE);
        }
        switch (fileNameSplit[1]) {
            case "zip":
                unzip(packFile, outDir);
                break;
            case "rar":
                unrarCommand(packFile, outDir);
                break;
            default:
                throw exception(NOT_ZIP_PACKAGE);
        }
    }

    public static void unzip(File zipFile, File outDir) {
        try {
            ZipUtil.unzip(zipFile, outDir, StandardCharsets.UTF_8);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            ZipUtil.unzip(zipFile, outDir, CharsetUtil.CHARSET_GBK);
        }
    }


    public static void unrarCommand(File rarFilePath, File outDir) throws IOException, InterruptedException {
        String command = "rar x " + rarFilePath.getAbsolutePath() + " " + outDir.getAbsolutePath();
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }

    public static void junrar(File rarFile, File outDir) throws RarException, IOException {
        Junrar.extract(rarFile, outDir);
    }
}

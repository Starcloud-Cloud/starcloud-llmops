package com.starcloud.ops.business.app.service.upload.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.file.core.client.FileClient;
import cn.iocoder.yudao.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileConfigDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileConfigMapper;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.service.upload.UploadRequest;
import com.starcloud.ops.business.app.service.upload.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    /**
     * 魔法 AI OSS 客户端名称
     */
    private static final String MOFAAI_OSS = "mofaai-oss";

    /**
     * 矩阵 OSS 客户端名称
     */
    private static final String JUZHEN_OSS = "mofaai-juzhen-oss";

    /**
     * 上传客户端类型映射，租户和客户端名称映射
     */
    private static final Map<Long, String> UPLOAD_CLIENT_TYPE_MAP = new ConcurrentHashMap<>();

    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileConfigMapper fileConfigMapper;

    @Resource
    private FileConfigService fileConfigService;

    static {
        UPLOAD_CLIENT_TYPE_MAP.put(AppConstants.MOFAAI_TENANT_ID, MOFAAI_OSS);
        UPLOAD_CLIENT_TYPE_MAP.put(AppConstants.JUZHEN_TENANT_ID, JUZHEN_OSS);
    }

    /**
     * 根据条件获取文件客户端
     *
     * @param tenantId 租户ID
     */
    @Override
    public FileClient getFileClient(Long tenantId) {
        String clientType = UPLOAD_CLIENT_TYPE_MAP.getOrDefault(tenantId, MOFAAI_OSS);
        Long configId = this.getFileClientConfigId(clientType);
        if (Objects.nonNull(configId)) {
            log.info("获取上传文件客户端配置：当前租户ID: {}， 配置名称: {}", tenantId, clientType);
            return fileConfigService.getFileClient(configId);
        }
        // 如果没有获取到，则返回默认的客户端
        log.info("获取上传文件客户端配置：当前租户ID: {}， 未找到配置，使用默认配置", tenantId);
        return fileConfigService.getMasterFileClient();
    }

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param request 上传请求
     * @return 文件路径
     */
    @Override
    public String createFile(UploadRequest request) throws Exception {
        Long tenantId = request.getTenantId();
        String name = request.getName();
        String path = request.getPath();
        byte[] content = request.getContent();

        // 计算默认的 path 名
        String type = FileTypeUtils.getMineType(content, name);
        if (StrUtil.isEmpty(path)) {
            path = FileUtils.generatePath(content, name);
        }
        // 如果 name 为空，则使用 path 填充
        if (StrUtil.isEmpty(name)) {
            name = path;
        }

        // 上传到文件存储器
        FileClient client = getFileClient(tenantId);
        Assert.notNull(client, "文件上传客户端不能为空");
        String url = client.upload(content, path, type);

        // 保存到数据库
        FileDO file = new FileDO();
        file.setConfigId(client.getId());
        file.setName(name);
        file.setPath(path);
        file.setUrl(url);
        file.setType(type);
        file.setSize(content.length);
        fileMapper.insert(file);
        return url;
    }

    /**
     * 获取文件内容
     *
     * @param path 文件路径
     * @return 文件内容
     */
    @Override
    public byte[] getContent(Long tenantId, String path) throws Exception {
        FileClient client = getFileClient(tenantId);
        Assert.notNull(client, "客户端 不能为空");
        return client.getContent(path);
    }

    /**
     * 获取配置ID
     *
     * @param configName 配置名称
     * @return 配置ID
     */
    private Long getFileClientConfigId(String configName) {
        LambdaQueryWrapper<FileConfigDO> wrapper = Wrappers.lambdaQuery(FileConfigDO.class);
        wrapper.eq(FileConfigDO::getName, configName);
        FileConfigDO fileConfig = fileConfigMapper.selectOne(wrapper);
        if (Objects.isNull(fileConfig)) {
            fileConfig = fileConfigMapper.selectByMaster();
        }
        return fileConfig.getId();
    }

}

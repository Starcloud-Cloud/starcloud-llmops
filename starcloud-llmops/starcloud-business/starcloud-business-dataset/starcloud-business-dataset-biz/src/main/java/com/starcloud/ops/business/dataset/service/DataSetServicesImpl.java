package com.starcloud.ops.business.dataset.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.file.core.client.FileClient;
import cn.iocoder.yudao.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.mq.producer.DatasetSourceDataCleanProducer;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.util.dataset.DataSetSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据集 流程处理
 */

@Slf4j
@Service
public class DataSetServicesImpl {

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private DatasetStorageService datasetStorageService;

    @Resource
    private DatasetSourceDataCleanProducer dataCleanProducer;


    @Resource
    private FileMapper fileMapper;

    public String uploadSourceData(DatasetStorageRespVO respVO) {
        MultipartFile file = respVO.getFile();
        String name = file.getOriginalFilename();
        String extension = DataSetSourceUtil.getExtension(file);
        String mimeType = DataSetSourceUtil.getMimeType(extension);
        byte[] content;
        try (InputStream inputStream = file.getInputStream()) {
            content = IoUtil.readBytes(inputStream);
        } catch (IOException e) {
            log.error("[uploadSourceData][数据流转换失败：文件名({})|文件地址({})|用户名({})", name, respVO.getPath(), null);
            throw new RuntimeException(e);
        }

        // 文件上传到指定配置路径
        Map<Long, Long> fileMap = uploadFile(content, name, respVO.getPath());
        Long key = fileMap.keySet().iterator().next();

        // 计算文件 hash
        String hash = DigestUtil.sha256Hex(content);

        DatasetStorageCreateReqVO createReqVO = new DatasetStorageCreateReqVO();

        createReqVO.setName(name);
        createReqVO.setStorageKey(String.valueOf(key));
        createReqVO.setType(extension.toUpperCase());
        createReqVO.setStorageType(String.valueOf(fileMap.get(key)));
        createReqVO.setSize(file.getSize());
        createReqVO.setMimeType(mimeType.toUpperCase());
        createReqVO.setUsed(false);
        createReqVO.setHash(hash);

        // 数据入库
        return datasetStorageService.addSourceData(createReqVO);
    }

    private Map<Long,Long> uploadFile(byte[] content, String name, String path) {

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
        FileClient client = fileConfigService.getMasterFileClient();
        Assert.notNull(client, "客户端(master) 不能为空");
        String url;
        try {
            url = client.upload(content, path, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 保存到数据库
        FileDO fileDO = new FileDO();
        fileDO.setConfigId(client.getId());
        fileDO.setName(name);
        fileDO.setPath(path);
        fileDO.setUrl(url);
        fileDO.setType(type);
        fileDO.setSize(content.length);
        fileMapper.insert(fileDO);

        HashMap<Long, Long> fileMap = new HashMap<>();
        fileMap.put(fileDO.getId(),client.getId());

        return fileMap;

    }


}

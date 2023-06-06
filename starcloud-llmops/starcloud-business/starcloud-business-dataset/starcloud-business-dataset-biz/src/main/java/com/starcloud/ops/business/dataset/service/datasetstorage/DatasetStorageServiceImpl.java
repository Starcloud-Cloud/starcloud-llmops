package com.starcloud.ops.business.dataset.service.datasetstorage;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.file.core.client.FileClient;
import cn.iocoder.yudao.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.convert.datasetstorage.DatasetStorageConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_STORAGE_NOT_EXISTS;

/**
 * 数据集源数据存储 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class DatasetStorageServiceImpl implements DatasetStorageService {

    @Resource
    private FileConfigService fileConfigService;
    @Resource
    private FileMapper fileMapper;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;


    /**
     *
     * @param respVO 源数据上传
     * @return UID
     */
    @Override
    public String uploadSourceData(DatasetStorageRespVO respVO) throws IOException {
        MultipartFile file = respVO.getFile();
        String path = respVO.getPath();
        String name = file.getOriginalFilename();
        String extension = getExtension(file);
        String mimeType = getMimeType(extension);
        byte[] content = new byte[0];
        try {
            content = IoUtil.readBytes(file.getInputStream());
            //try (BufferedReader reader = new BufferedReader(
            //        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            //    return IoUtil.read(reader).length();
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
        } catch (IOException e) {
            log.error("[uploadSourceData][数据流转换失败：文件名({})|文件地址({})|用户名({})", name, path, null);
            throw new RuntimeException(e);
        }

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
        String url = null;
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

        //计算文件hash
        String hash = DigestUtil.sha256Hex(file.getInputStream());
        String uid = DatasetUID.getDatasetUID();

        DatasetStorageDO datasetStorageDO = new DatasetStorageDO();
        datasetStorageDO.setUid(uid);
        datasetStorageDO.setName(name);
        datasetStorageDO.setStorageKey(String.valueOf(fileDO.getId()));
        datasetStorageDO.setType(extension.toUpperCase());
        datasetStorageDO.setStorageType(String.valueOf(client.getId()));
        datasetStorageDO.setSize(file.getSize());
        datasetStorageDO.setMimeType(mimeType.toUpperCase());
        datasetStorageDO.setUsed(false);
        datasetStorageDO.setHash(hash);

        //数据入库
        datasetStorageMapper.insert(datasetStorageDO);
        return uid;
    }

    /**
     * 根据文件编号获取文件存储信息
     *
     * @param UID
     *
     * @return
     */
    @Override
    public DatasetStorageUpLoadRespVO getDatasetStorageByUID(String UID) {
        //封装查询条件
        LambdaQueryWrapper<DatasetStorageDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetStorageDO::getUid, UID);
        wrapper.eq(DatasetStorageDO::getTenantId, getLoginUser().getId());

        DatasetStorageDO datasetStorageDO = datasetStorageMapper.selectOne(wrapper);
        //文件不存在，则报错
        if (ObjectUtil.isEmpty(datasetStorageDO)){
            log.error("[getDatasetStorageInfo][获取源数据失败，文件不存在：文件UID({})|用户ID({})|租户ID({})", UID, getLoginUser().getId(), getLoginUser().getTenantId());
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
        //根据ID获取文件信息
        FileDO fileDO = fileMapper.selectById(datasetStorageDO.getStorageKey());
        if (ObjectUtil.isEmpty(fileDO)){
            log.error("[getDatasetStorageInfo][获取源数据失败，文件不存在：文件UID({})|用户ID({})|租户ID({})", UID, getLoginUser().getId(), getLoginUser().getTenantId());
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
        DatasetStorageUpLoadRespVO datasetStorageUpLoadRespVO = DatasetStorageConvert.convert2LoadRespVO(datasetStorageDO);
        datasetStorageUpLoadRespVO.setStorageKey(fileDO.getUrl());
        //数据转换
        return datasetStorageUpLoadRespVO;
    }

    private void validateDatasetStorageExists(Long id) {
        if (datasetStorageMapper.selectById(id) == null) {
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
    }
    /**
     * 获取文件扩展名
     * @param file
     * @return
     */
    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 获取文件MimeType
     * @param extension
     * @return
     */
    private String getMimeType(String extension) {
        switch (extension) {
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }


}
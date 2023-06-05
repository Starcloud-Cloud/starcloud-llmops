package com.starcloud.ops.business.dataset.service.datasetstorage;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import cn.iocoder.yudao.framework.file.core.client.FileClient;
import cn.iocoder.yudao.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStoragePageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasetstorage.DatasetStorageConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.datasetstorage.enums.ErrorCodeConstants.DATASET_STORAGE_NOT_EXISTS;

/**
 * 数据集源数据存储 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DatasetStorageServiceImpl implements DatasetStorageService {

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;

    @Override
    public Long createDatasetStorage(DatasetStorageCreateReqVO createReqVO) {
        // 插入
        DatasetStorageDO datasetStorage = DatasetStorageConvert.INSTANCE.convert(createReqVO);
        datasetStorageMapper.insert(datasetStorage);
        // 返回
        return datasetStorage.getId();
    }

    @Override
    public void updateDatasetStorage(DatasetStorageUpdateReqVO updateReqVO) {
        // 校验存在
        validateDatasetStorageExists(updateReqVO.getId());
        // 更新
        DatasetStorageDO updateObj = DatasetStorageConvert.INSTANCE.convert(updateReqVO);
        datasetStorageMapper.updateById(updateObj);
    }

    @Override
    public void deleteDatasetStorage(Long id) {
        // 校验存在
        validateDatasetStorageExists(id);
        // 删除
        datasetStorageMapper.deleteById(id);
    }

    private void validateDatasetStorageExists(Long id) {
        if (datasetStorageMapper.selectById(id) == null) {
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
    }

    @Override
    public DatasetStorageDO getDatasetStorage(Long id) {
        return datasetStorageMapper.selectById(id);
    }

    @Override
    public List<DatasetStorageDO> getDatasetStorageList(Collection<Long> ids) {
        return datasetStorageMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<DatasetStorageDO> getDatasetStoragePage(DatasetStoragePageReqVO pageReqVO) {
        return datasetStorageMapper.selectPage(pageReqVO);
    }

    @Override
    public Boolean uploadSourceData(DatasetStorageRespVO respVO) {
        MultipartFile file = respVO.getFile();
        String path = respVO.getPath();
        String name = file.getOriginalFilename();
        byte[] content = new byte[0];
        try {
            content = IoUtil.readBytes(file.getInputStream());
        } catch (IOException e) {
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

        return null;
    }

}
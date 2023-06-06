package com.starcloud.ops.business.dataset.service.datasets;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsExportReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasets.DatasetsMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.*;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASETS_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
* {@link DatasetsServiceImpl} 的单元测试类
*
* @author 芋道源码
*/
@Import(DatasetsServiceImpl.class)
public class DatasetsServiceImplTest extends BaseDbUnitTest {

    @Resource
    private DatasetsServiceImpl datasetsService;

    @Resource
    private DatasetsMapper datasetsMapper;

    @Test
    public void testCreateDatasets_success() {
        // 准备参数
        DatasetsCreateReqVO reqVO = randomPojo(DatasetsCreateReqVO.class);

        // 调用
        String datasetsId = datasetsService.createDatasets(reqVO);
        // 断言
        assertNotNull(datasetsId);
        // 校验记录的属性是否正确
        DatasetsDO datasets = datasetsMapper.selectById(datasetsId);
        assertPojoEquals(reqVO, datasets);
    }

    @Test
    public void testUpdateDatasets_success() {
        // mock 数据
        DatasetsDO dbDatasets = randomPojo(DatasetsDO.class);
        datasetsMapper.insert(dbDatasets);// @Sql: 先插入出一条存在的数据
        // 准备参数
        DatasetsUpdateReqVO reqVO = randomPojo(DatasetsUpdateReqVO.class, o -> {
            o.setId(dbDatasets.getId()); // 设置更新的 ID
        });

        // 调用
        datasetsService.updateDatasets(reqVO);
        // 校验是否更新正确
        DatasetsDO datasets = datasetsMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, datasets);
    }

    @Test
    public void testUpdateDatasets_notExists() {
        // 准备参数
        DatasetsUpdateReqVO reqVO = randomPojo(DatasetsUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> datasetsService.updateDatasets(reqVO), DATASETS_NOT_EXISTS);
    }

    @Test
    public void testDeleteDatasets_success() {
        // mock 数据
        DatasetsDO dbDatasets = randomPojo(DatasetsDO.class);
        datasetsMapper.insert(dbDatasets);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbDatasets.getId();

        // 调用
        datasetsService.deleteDatasets(id);
       // 校验数据不存在了
       assertNull(datasetsMapper.selectById(id));
    }

    @Test
    public void testDeleteDatasets_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> datasetsService.deleteDatasets(id), DATASETS_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetDatasetsPage() {
       // mock 数据
       DatasetsDO dbDatasets = randomPojo(DatasetsDO.class, o -> { // 等会查询到
           o.setUid(null);
           o.setName(null);
           o.setDescription(null);
           o.setProvider(null);
           o.setPermission(null);
           o.setSourceType(null);
           o.setIndexingModel(null);
           o.setIndexStruct(null);
           o.setCreateTime(null);
           o.setEnabled(null);
       });
       datasetsMapper.insert(dbDatasets);
       // 测试 uid 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setUid(null)));
       // 测试 name 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setName(null)));
       // 测试 description 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setDescription(null)));
       // 测试 provider 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setProvider(null)));
       // 测试 permission 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setPermission(null)));
       // 测试 sourceType 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setSourceType(null)));
       // 测试 indexingModel 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setIndexingModel(null)));
       // 测试 indexStruct 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setIndexStruct(null)));
       // 测试 createTime 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setCreateTime(null)));
       // 测试 enabled 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setEnabled(null)));
       // 准备参数
       DatasetsPageReqVO reqVO = new DatasetsPageReqVO();
       reqVO.setUid(null);
       reqVO.setName(null);
       reqVO.setDescription(null);
       reqVO.setProvider(null);
       reqVO.setPermission(null);
       reqVO.setSourceType(null);
       reqVO.setIndexingModel(null);
       reqVO.setIndexStruct(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setEnabled(null);

       // 调用
       PageResult<DatasetsDO> pageResult = datasetsService.getDatasetsPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbDatasets, pageResult.getList().get(0));
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetDatasetsList() {
       // mock 数据
       DatasetsDO dbDatasets = randomPojo(DatasetsDO.class, o -> { // 等会查询到
           o.setUid(null);
           o.setName(null);
           o.setDescription(null);
           o.setProvider(null);
           o.setPermission(null);
           o.setSourceType(null);
           o.setIndexingModel(null);
           o.setIndexStruct(null);
           o.setCreateTime(null);
           o.setEnabled(null);
       });
       datasetsMapper.insert(dbDatasets);
       // 测试 uid 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setUid(null)));
       // 测试 name 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setName(null)));
       // 测试 description 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setDescription(null)));
       // 测试 provider 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setProvider(null)));
       // 测试 permission 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setPermission(null)));
       // 测试 sourceType 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setSourceType(null)));
       // 测试 indexingModel 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setIndexingModel(null)));
       // 测试 indexStruct 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setIndexStruct(null)));
       // 测试 createTime 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setCreateTime(null)));
       // 测试 enabled 不匹配
       datasetsMapper.insert(cloneIgnoreId(dbDatasets, o -> o.setEnabled(null)));
       // 准备参数
       DatasetsExportReqVO reqVO = new DatasetsExportReqVO();
       reqVO.setUid(null);
       reqVO.setName(null);
       reqVO.setDescription(null);
       reqVO.setProvider(null);
       reqVO.setPermission(null);
       reqVO.setSourceType(null);
       reqVO.setIndexingModel(null);
       reqVO.setIndexStruct(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setEnabled(null);

       // 调用
       List<DatasetsDO> list = datasetsService.getDatasetsList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbDatasets, list.get(0));
    }

}
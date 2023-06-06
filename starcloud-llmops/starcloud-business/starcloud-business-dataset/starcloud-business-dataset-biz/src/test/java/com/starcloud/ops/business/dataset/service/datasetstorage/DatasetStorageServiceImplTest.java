package com.starcloud.ops.business.dataset.service.datasetstorage;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import cn.iocoder.yudao.module.infra.service.file.FileConfigServiceImpl;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * {@link DatasetStorageServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import({DatasetStorageServiceImpl.class, FileConfigServiceImpl.class , AdapterRuoyiProConfiguration.class})
public class DatasetStorageServiceImplTest extends BaseDbUnitTest {


    @MockBean
    private FileConfigService fileConfigService;

    @MockBean
    private DatasetStorageServiceImpl datasetStorageService;

    @MockBean
    private DatasetStorageMapper datasetStorageMapper;

    @Test
    public void testGetDatasetStorage_success() {

        // 调用
        DatasetStorageUpLoadRespVO reqVO1 = datasetStorageService.getDatasetStorageByUID("1665918527166627840");
        // 断言
        System.out.println(reqVO1);
    }

}


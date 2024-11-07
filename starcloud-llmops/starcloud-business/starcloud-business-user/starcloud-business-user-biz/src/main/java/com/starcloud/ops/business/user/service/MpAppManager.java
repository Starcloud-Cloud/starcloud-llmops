package com.starcloud.ops.business.user.service;

import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.starcloud.ops.business.user.enums.DictTypeConstants.MP_LOGIN_APP;
import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_APP;

@Component
public class MpAppManager implements InitializingBean {


    @Resource
    private DictDataService dictDataService;

    private static final Map<Long, String> wxMp = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        DictDataExportReqVO reqVO = new DictDataExportReqVO();
        reqVO.setDictType(WECHAT_APP);
        reqVO.setLabel(MP_LOGIN_APP);

        List<DictDataDO> dictDataDOList = dictDataService.getDictDataList(reqVO);
        for (DictDataDO dictDataDO : dictDataDOList) {
            try {
                wxMp.put(Long.valueOf(dictDataDO.getValue()), dictDataDO.getRemark());
            } catch (Exception ignored) {
            }
        }
    }

    public static String getMpAppId(Long tenantId) {
        return wxMp.get(tenantId);
    }
}

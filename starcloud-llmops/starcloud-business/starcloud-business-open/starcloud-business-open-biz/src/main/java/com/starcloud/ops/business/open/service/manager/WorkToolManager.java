package com.starcloud.ops.business.open.service.manager;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.open.service.manager.dto.WorkToolRobotDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class WorkToolManager {

    @Resource
    private DictDataService dictDataService;


    /**
     * hash值取吗 分配机器
     * @param mobile 手机号
     * @return
     */
    public WorkToolRobotDTO getRobotId(String mobile) {
        List<DictDataDO> dictDataList = allRobot();
        int groupHash = mobile.hashCode();
        DictDataDO dictDataDO = dictDataList.get(groupHash % dictDataList.size());
        String value = dictDataDO.getValue();
        return JSONUtil.toBean(value,WorkToolRobotDTO.class);
    }



    public String getRobotName(String robotId) {
        List<DictDataDO> dictDataList = allRobot();
        for (DictDataDO dictDataDO : dictDataList) {
            WorkToolRobotDTO bean = JSONUtil.toBean(dictDataDO.getValue(), WorkToolRobotDTO.class);
            if (Objects.equals(bean.getRobotId(), robotId)) {
                return bean.getRobotName();
            }
        }
        return StringUtils.EMPTY;
    }

    private List<DictDataDO> allRobot() {
        DictDataExportReqVO dataExportReqVO = new DictDataExportReqVO();
        dataExportReqVO.setDictType("wecom_robot");
        dataExportReqVO.setStatus(0);
        return dictDataService.getDictDataList(dataExportReqVO);
    }
}

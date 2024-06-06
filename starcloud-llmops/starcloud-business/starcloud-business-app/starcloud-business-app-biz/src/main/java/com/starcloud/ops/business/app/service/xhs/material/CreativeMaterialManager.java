package com.starcloud.ops.business.app.service.xhs.material;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMaterialMapper;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class CreativeMaterialManager {

    @Resource
    private CreativePlanMaterialMapper creativePlanMaterialMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppMapper appMapper;


    public List<Map<String, Object>> getMaterialList(String appUid, String source) {
        AppValidate.notBlank(appUid, "应用UID为必填项！");
        AppValidate.notBlank(source, "创作计划来源为必填项！");
        if (CreativePlanSourceEnum.isApp(source)) {
            AppDO appDO = appMapper.getMaterial(appUid);
            AppValidate.notNull(appDO,"我的应用不存在");
            return appDO.getMaterialList();
        } else {
            AppMarketDO material = appMarketMapper.getMaterial(appUid);
            AppValidate.notNull(material,"应用市场不存在");
            return material.getMaterialList();
        }
    }

    public List<Map<String, Object>> getPlanMaterialList(String planUid) {
        AppValidate.notBlank(planUid, "执行计划UID为必填项！");
        return creativePlanMaterialMapper.getMaterial(planUid).getMaterialList();
    }


}

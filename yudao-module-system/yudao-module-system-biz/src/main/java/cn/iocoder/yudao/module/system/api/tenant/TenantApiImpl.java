package cn.iocoder.yudao.module.system.api.tenant;

import cn.iocoder.yudao.module.system.api.tenant.dto.TenantInfoDTO;
import cn.iocoder.yudao.module.system.convert.tenant.TenantConvert;
import cn.iocoder.yudao.module.system.service.tenant.TenantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 多租户的 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class TenantApiImpl implements TenantApi {

    @Resource
    private TenantService tenantService;

    @Override
    public List<Long> getTenantIdList() {
        return tenantService.getTenantIdList();
    }

    @Override
    public void validateTenant(Long id) {
        tenantService.validTenant(id);
    }

    /**
     * 根据 ID获取指定的租户信息
     *
     * @param id
     * @return 租户编号数组
     */
    @Override
    public TenantInfoDTO getTenantById(Long id) {
        return TenantConvert.INSTANCE.convertDTO(tenantService.getTenant(id));
    }

}

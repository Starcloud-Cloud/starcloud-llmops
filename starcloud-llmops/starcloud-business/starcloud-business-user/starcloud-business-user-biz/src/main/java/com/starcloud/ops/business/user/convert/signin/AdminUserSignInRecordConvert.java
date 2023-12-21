package com.starcloud.ops.business.user.convert.signin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AdminUserSignInRecordRespVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AppAdminUserSignInRecordRespVO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 签到记录 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface AdminUserSignInRecordConvert {

    AdminUserSignInRecordConvert INSTANCE = Mappers.getMapper(AdminUserSignInRecordConvert.class);

    default PageResult<AdminUserSignInRecordRespVO> convertPage(PageResult<AdminUserSignInRecordDO> pageResult, List<AdminUserDO> users) {
        PageResult<AdminUserSignInRecordRespVO> voPageResult = convertPage(pageResult);
        // user 拼接
        Map<Long, AdminUserDO> userMap = convertMap(users, AdminUserDO::getId);
        voPageResult.getList().forEach(record -> MapUtils.findAndThen(userMap, record.getUserId(),
                memberUserRespDTO -> record.setNickname(memberUserRespDTO.getNickname())));
        return voPageResult;
    }

    PageResult<AdminUserSignInRecordRespVO> convertPage(PageResult<AdminUserSignInRecordDO> pageResult);

    PageResult<AppAdminUserSignInRecordRespVO> convertPage02(PageResult<AdminUserSignInRecordDO> pageResult);

    AppAdminUserSignInRecordRespVO coverRecordToAppRecordVo(AdminUserSignInRecordDO memberSignInRecordDO);

    default AdminUserSignInRecordDO convert(Long userId, AdminUserSignInRecordDO lastRecord, List<AdminUserSignInConfigDO> configs) {
        // 1. 计算是第几天签到
        configs.sort(Comparator.comparing(AdminUserSignInConfigDO::getDay));
        AdminUserSignInConfigDO lastConfig = CollUtil.getLast(configs); // 最大签到天数配置
        // 1.2. 计算今天是第几天签到
        int day = 1;
        // TODO @puhui999：要判断是不是昨天签到的；是否是昨天的判断，可以抽个方法到 util 里
        if (lastRecord != null) {
            day = lastRecord.getDay() + 1;
        }
        // 1.3 判断是否超出了最大签到配置
        if (day > lastConfig.getDay()) {
            day = 1; // 超过最大配置的天数，重置到第一天。(也就是说开启下一轮签到)
        }

        // 2.1 初始化签到信息
        AdminUserSignInRecordDO record = new AdminUserSignInRecordDO().setUserId(userId)
                .setDay(day).setMagicBean(0).setMagicImage(0);
        // 2.2 获取签到对应的积分
        AdminUserSignInConfigDO config = CollUtil.findOne(configs, item -> ObjUtil.equal(item.getDay(), record.getDay()));
        if (config == null) {
            return record;
        }
        record.setMagicBean(config.getMagicBean());
        record.setMagicImage(config.getMagicImage());
        return record;
    }

}

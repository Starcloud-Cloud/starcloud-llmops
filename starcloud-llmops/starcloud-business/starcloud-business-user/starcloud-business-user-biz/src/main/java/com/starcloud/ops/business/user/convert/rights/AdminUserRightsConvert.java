package com.starcloud.ops.business.user.convert.rights;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AppAdminUserRightsRespVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 用户积分记录 Convert
 *
 * @author QingX
 */
@Mapper
public interface AdminUserRightsConvert {

    AdminUserRightsConvert INSTANCE = Mappers.getMapper(AdminUserRightsConvert.class);

    default PageResult<AdminUserRightsRespVO> convertPage(PageResult<AdminUserRightsDO> pageResult, List<AdminUserDO> users) {
        PageResult<AdminUserRightsRespVO> voPageResult = convertPage(pageResult);
        // user 拼接
        Map<Long, AdminUserDO> userMap = convertMap(users, AdminUserDO::getId);
        voPageResult.getList().forEach(record -> MapUtils.findAndThen(userMap, record.getUserId(),
                memberUserRespDTO -> record.setNickname(memberUserRespDTO.getNickname())));
        return voPageResult;
    }

    PageResult<AdminUserRightsRespVO> convertPage(PageResult<AdminUserRightsDO> pageResult);

    PageResult<AppAdminUserRightsRespVO> convertPage02(PageResult<AdminUserRightsDO> pageResult);

    default AdminUserRightsDO convert01(AddRightsDTO addRightsDTO, Integer bizType, LocalDateTime startTime, LocalDateTime endTime) {

        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);

        AdminUserRightsDO bean = new AdminUserRightsDO();
        bean.setUserId(addRightsDTO.getUserId());
        bean.setBizId(addRightsDTO.getBizId());
        bean.setBizType(bizType);
        bean.setTitle(bizTypeEnum.getName());
        bean.setDescription(StrUtil.format(bizTypeEnum.getDescription(), addRightsDTO.getMagicBean(), addRightsDTO.getMagicImage(), addRightsDTO.getMatrixBean()));
        bean.setMagicBean(addRightsDTO.getMagicBean());
        bean.setMagicImage(addRightsDTO.getMagicImage());
        bean.setMatrixBean(addRightsDTO.getMatrixBean());
        bean.setMagicBeanInit(addRightsDTO.getMagicBean());
        bean.setMagicImageInit(addRightsDTO.getMagicImage());
        bean.setMatrixBeanInit(addRightsDTO.getMatrixBean());
        bean.setUserLevelId(addRightsDTO.getLevelId());
        bean.setValidStartTime(startTime);
        bean.setValidEndTime(endTime);
        bean.setStatus(AdminUserRightsStatusEnum.NORMAL.getType());
        return bean;

    }


    default AdminUserRightsDO convert(Long userId, String bizId, Integer bizType,
                                          Integer magicBean, Integer magicImage, Integer matrixBean,
                                          LocalDateTime startTime, LocalDateTime endTime, Long levelId) {

        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        return AdminUserRightsDO.builder()
                .userId(userId)
                .bizId(bizId)
                .bizType(bizType)
                .title(bizTypeEnum.getName())
                .description(StrUtil.format(bizTypeEnum.getDescription(), magicBean, magicImage))
                .magicBean(magicBean)
                .magicImage(magicImage)
                .matrixBean(matrixBean)
                .magicBeanInit(magicBean)
                .magicImageInit(magicImage)
                .matrixBeanInit(matrixBean)
                .userLevelId(levelId)
                .validStartTime(startTime)
                .validEndTime(endTime)
                .status(AdminUserRightsStatusEnum.NORMAL.getType())
                .build();
    }
}

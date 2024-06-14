package com.starcloud.ops.business.poster.service.material;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqsVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialSaveReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.PosterMaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.PosterMaterialDO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.PosterMaterialCategoryDO;
import com.starcloud.ops.business.poster.dal.mysql.material.PosterMaterialMapper;
import com.starcloud.ops.business.poster.service.materialcategory.PosterMaterialCategoryService;
import com.starcloud.ops.business.user.util.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.mp.enums.ErrorCodeConstants.MATERIAL_NOT_EXISTS;
import static com.starcloud.ops.business.poster.dal.dataobject.materialcategory.PosterMaterialCategoryDO.CATEGORY_LEVEL;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.POSTER_MATERIAL_NOT_EXISTS;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.SPU_MATERIAL_FAIL_CATEGORY_LEVEL_ERROR;

/**
 * 海报素材 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class PosterMaterialServiceImpl implements PosterMaterialService {

    @Resource
    private PosterMaterialCategoryService posterMaterialCategoryService;

    @Resource
    private PosterMaterialMapper posterMaterialMapper;

    @Override
    public Long createMaterial(PosterMaterialSaveReqVO createReqVO) {

        // 校验分类
        validateCategory(createReqVO.getCategoryId());

        // 插入
        PosterMaterialDO material = BeanUtils.toBean(createReqVO, PosterMaterialDO.class);
        material.setUid(IdUtil.fastSimpleUUID());
        material.setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
        posterMaterialMapper.insert(material);
        // 返回
        return material.getId();
    }

    @Override
    public void updateMaterial(PosterMaterialSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialExists(updateReqVO.getId());
        // 更新
        PosterMaterialDO updateObj = BeanUtils.toBean(updateReqVO, PosterMaterialDO.class);
        posterMaterialMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterial(Long id) {
        // 校验存在
        validateMaterialExists(id);
        // 删除
        posterMaterialMapper.deleteById(id);
    }

    private void validateMaterialExists(Long id) {
        if (posterMaterialMapper.selectById(id) == null) {
            throw exception(POSTER_MATERIAL_NOT_EXISTS);
        }
    }

    @Override
    public PosterMaterialDO getMaterial(Long id) {
        return posterMaterialMapper.selectById(id);
    }

    @Override
    public PageResult<PosterMaterialDO> getMaterialPage(PosterMaterialPageReqsVO pageReqVO) {
        return posterMaterialMapper.selectPage(pageReqVO);
    }

    /**
     * 获取当前分类下素材数量
     *
     * @param categoryId 素材分类 ID
     * @return
     */
    @Override
    public Long getMaterialCountByCategoryId(Long categoryId) {
        return posterMaterialMapper.selectCount(PosterMaterialDO::getCategoryId, categoryId);
    }



    @Override
    public PageResult<PosterMaterialDO> getPosterMaterialPage(PosterMaterialPageReqVO pageReqVO) {
        // 查找时，如果查找某个分类编号，则包含它的子分类。因为顶级分类不包含素材
        Set<Long> categoryIds = new HashSet<>();
        if (pageReqVO.getCategoryId() != null && pageReqVO.getCategoryId() > 0) {
            categoryIds.add(pageReqVO.getCategoryId());
            List<PosterMaterialCategoryDO> categoryChildren = posterMaterialCategoryService.getEnableCategoryList(new PosterMaterialCategoryListReqVO()
                    .setParentId(pageReqVO.getCategoryId()).setStatus(CommonStatusEnum.ENABLE.getStatus()));
            categoryIds.addAll(CollectionUtils.convertList(categoryChildren, PosterMaterialCategoryDO::getId));
        }
        // 分页查询

        return posterMaterialMapper.selectPage(pageReqVO, categoryIds, getLoginUserId());
    }


    /**
     * 校验商品分类是否合法
     *
     * @param id 商品分类编号
     */
    private void validateCategory(Long id) {
        posterMaterialCategoryService.validateCategory(id);
        // 校验层级
        if (posterMaterialCategoryService.getCategoryLevel(id) < CATEGORY_LEVEL) {
            throw exception(SPU_MATERIAL_FAIL_CATEGORY_LEVEL_ERROR);
        }
    }
}
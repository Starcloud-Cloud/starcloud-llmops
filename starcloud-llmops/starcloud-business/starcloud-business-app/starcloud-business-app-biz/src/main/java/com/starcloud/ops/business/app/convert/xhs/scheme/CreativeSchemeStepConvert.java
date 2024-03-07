package com.starcloud.ops.business.app.convert.xhs.scheme;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.AssembleSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.CustomSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.VariableSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.ParagraphSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.TitleSchemeStepDTO;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.AssembleSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.BaseSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.CustomSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.VariableSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.ParagraphSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.PosterSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.TitleSchemeStepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Mapper
public interface CreativeSchemeStepConvert {

    CreativeSchemeStepConvert INSTANCE = Mappers.getMapper(CreativeSchemeStepConvert.class);

    /**
     * 转换方案步骤DTO
     *
     * @param schemeStep 方案步骤实体
     * @return 转换方案步骤DTO
     */
    default BaseSchemeStepDTO convert(BaseSchemeStepEntity schemeStep) {
        if (schemeStep instanceof VariableSchemeStepEntity) {
            return convertEmpty((VariableSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof TitleSchemeStepEntity) {
            return convertTitle((TitleSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof CustomSchemeStepEntity) {
            return convertContent((CustomSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof ParagraphSchemeStepEntity) {
            return convertParagraph((ParagraphSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof AssembleSchemeStepEntity) {
            return convertAssemble((AssembleSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof PosterSchemeStepEntity) {
            return convertPoster((PosterSchemeStepEntity) schemeStep);
        }
        throw new RuntimeException("不支持的方案步骤");
    }

    /**
     * 空方案实体转换为空方案DTO
     *
     * @param schemeStep 空方案实体
     * @return 空方案DTO
     */
    VariableSchemeStepDTO convertEmpty(VariableSchemeStepEntity schemeStep);

    /**
     * 标题方案实体转换为标题方案DTO
     *
     * @param schemeStep 标题方案实体
     * @return 标题方案DTO
     */
    TitleSchemeStepDTO convertTitle(TitleSchemeStepEntity schemeStep);

    /**
     * 内容方案实体转换为内容方案DTO
     *
     * @param schemeStep 内容方案实体
     * @return 内容方案DTO
     */
    CustomSchemeStepDTO convertContent(CustomSchemeStepEntity schemeStep);

    /**
     * 段落方案实体转换为段落方案DTO
     *
     * @param schemeStep 段落方案实体
     * @return 段落方案DTO
     */
    ParagraphSchemeStepDTO convertParagraph(ParagraphSchemeStepEntity schemeStep);

    /**
     * 拼接方案实体转换为拼接方案DTO
     *
     * @param schemeStep 拼接方案实体
     * @return 拼接方案DTO
     */
    AssembleSchemeStepDTO convertAssemble(AssembleSchemeStepEntity schemeStep);

    /**
     * 图片方案实体转换为图片方案DTO
     *
     * @param schemeStep 图片方案实体
     * @return 图片方案DTO
     */
    PosterSchemeStepDTO convertPoster(PosterSchemeStepEntity schemeStep);

    /**
     * 转换方案步骤DTO
     *
     * @param schemeStep 方案步骤DTO
     * @return 方案步骤实体
     */
    default BaseSchemeStepEntity convert(BaseSchemeStepDTO schemeStep) {
        if (schemeStep instanceof VariableSchemeStepDTO) {
            return convertEmpty((VariableSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof TitleSchemeStepDTO) {
            return convertTitle((TitleSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof CustomSchemeStepDTO) {
            return convertContent((CustomSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof ParagraphSchemeStepDTO) {
            return convertParagraph((ParagraphSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof AssembleSchemeStepDTO) {
            return convertAssemble((AssembleSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof PosterSchemeStepDTO) {
            return convertPoster((PosterSchemeStepDTO) schemeStep);
        }
        throw new RuntimeException("不支持的方案步骤");
    }

    /**
     * 空方案DTO转换为空方案实体
     *
     * @param schemeStep 空方案DTO
     * @return 空方案实体
     */
    VariableSchemeStepEntity convertEmpty(VariableSchemeStepDTO schemeStep);

    /**
     * 标题方案DTO转换为标题方案实体
     *
     * @param schemeStep schemeStep
     * @return 标题实体
     */
    TitleSchemeStepEntity convertTitle(TitleSchemeStepDTO schemeStep);

    /**
     * 内容方案DTO转换为内容方案实体
     *
     * @param schemeStep 内容方案DTO
     * @return 内容方案实体
     */
    CustomSchemeStepEntity convertContent(CustomSchemeStepDTO schemeStep);

    /**
     * 段落方案DTO转换为段落方案实体
     *
     * @param schemeStep 段落方案DTO
     * @return 段落方案实体
     */
    ParagraphSchemeStepEntity convertParagraph(ParagraphSchemeStepDTO schemeStep);

    /**
     * 拼接方案DTO转换为拼接方案实体
     *
     * @param schemeStep schemeStep
     * @return 拼接方案实体
     */
    AssembleSchemeStepEntity convertAssemble(AssembleSchemeStepDTO schemeStep);

    /**
     * 图片方案DTO转换为图片方案实体
     *
     * @param schemeStep 图片方案DTO
     * @return 图片方案实体
     */
    PosterSchemeStepEntity convertPoster(PosterSchemeStepDTO schemeStep);

    List<VariableItemEntity> convertToEntity(List<VariableItemRespVO> variableList);

    List<VariableItemRespVO> convertToResponse(List<VariableItemEntity> variableList);
}

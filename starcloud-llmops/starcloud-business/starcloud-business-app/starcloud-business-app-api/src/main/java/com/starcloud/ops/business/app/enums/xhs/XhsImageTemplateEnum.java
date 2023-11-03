package com.starcloud.ops.business.app.enums.xhs;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Getter
public enum XhsImageTemplateEnum implements IEnumable<String> {

    /**
     * 小红书-(1张图)
     */
    ONE_BOX_GRID("641a9013326a4389", "小红书-(1张图)") {
        @Override
        public List<VariableItemRespVO> variables() {
            return Collections.singletonList(
                    VariableItemRespVO.ofImageVariable("IMAGE_1", "图片1")
            );
        }
    },

    /**
     * 小红书-(2张图)
     */
    TWO_BOX_GRID("0e790a29767a4e8b", "小红书-(2张图)") {
        @Override
        public List<VariableItemRespVO> variables() {
            return Arrays.asList(
                    VariableItemRespVO.ofImageVariable("IMAGE_1", "图片1"),
                    VariableItemRespVO.ofImageVariable("IMAGE_2", "图片2")
            );
        }
    },

    /**
     * 小红书-(6宫格+标题+子标题)
     */
    SIX_BOX_GRID("58cf40e9a2d14a5b", "小红书-(6宫格+标题+子标题)") {
        @Override
        public List<VariableItemRespVO> variables() {
            return Arrays.asList(
                    VariableItemRespVO.ofTextVariable("TITLE", "主标题"),
                    VariableItemRespVO.ofTextVariable("SUB_TITLE", "副标题"),
                    VariableItemRespVO.ofImageVariable("IMAGE_1", "图片1"),
                    VariableItemRespVO.ofImageVariable("IMAGE_2", "图片2"),
                    VariableItemRespVO.ofImageVariable("IMAGE_3", "图片3"),
                    VariableItemRespVO.ofImageVariable("IMAGE_4", "图片4"),
                    VariableItemRespVO.ofImageVariable("IMAGE_5", "图片5"),
                    VariableItemRespVO.ofImageVariable("IMAGE_6", "图片6")
            );
        }
    },

    /**
     * 小红书-(8宫格3*2*3+标题)
     */
    EIGHT_BOX_GRID("d14e5942442e4811", "小红书-(8宫格3*2*3+标题)") {
        @Override
        public List<VariableItemRespVO> variables() {
            return Arrays.asList(
                    VariableItemRespVO.ofTextVariable("TITLE", "标题"),
                    VariableItemRespVO.ofImageVariable("IMAGE_1", "图片1"),
                    VariableItemRespVO.ofImageVariable("IMAGE_2", "图片2"),
                    VariableItemRespVO.ofImageVariable("IMAGE_3", "图片3"),
                    VariableItemRespVO.ofImageVariable("IMAGE_4", "图片4"),
                    VariableItemRespVO.ofImageVariable("IMAGE_5", "图片5"),
                    VariableItemRespVO.ofImageVariable("IMAGE_6", "图片6"),
                    VariableItemRespVO.ofImageVariable("IMAGE_7", "图片7"),
                    VariableItemRespVO.ofImageVariable("IMAGE_8", "图片8")
            );
        }
    };

    private final String code;

    private final String label;

    public abstract List<VariableItemRespVO> variables();

    XhsImageTemplateEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 根据 name 获取枚举
     *
     * @param name 名称
     * @return XhsImageTemplateEnum
     */
    public static XhsImageTemplateEnum of(String name) {
        return Arrays.stream(XhsImageTemplateEnum.values())
                .filter(item -> item.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取模板列表
     *
     * @return List<XhsImageTemplateDTO>
     */
    public static List<XhsImageTemplateDTO> templateList() {
        return Arrays.stream(values()).map(item -> {
            XhsImageTemplateDTO dto = new XhsImageTemplateDTO();
            dto.setId(item.name());
            dto.setName(item.getLabel());
            dto.setVariables(item.variables());
            return dto;
        }).collect(Collectors.toList());
    }
}

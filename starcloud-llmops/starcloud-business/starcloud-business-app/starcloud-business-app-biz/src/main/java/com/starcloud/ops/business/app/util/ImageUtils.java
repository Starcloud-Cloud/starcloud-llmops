package com.starcloud.ops.business.app.util;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.request.GenerateImageRequest;
import com.starcloud.ops.business.app.api.image.vo.request.UpscaleImageRequest;
import com.starcloud.ops.business.app.api.image.vo.request.VariantsImageRequest;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.image.ImageTaskConfigTypeEnum;
import com.starcloud.ops.business.app.enums.image.ProductImageTypeEnum;
import com.starcloud.ops.business.app.enums.vsearch.EngineEnum;
import com.starcloud.ops.business.app.enums.vsearch.GuidancePresetEnum;
import com.starcloud.ops.business.app.enums.vsearch.ImageSizeEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplesEnum;
import com.starcloud.ops.business.app.enums.vsearch.StylePresetEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 处理图片元数据工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
@SuppressWarnings("all")
public class ImageUtils {

    /**
     * SD 价格
     */
    public static final BigDecimal SD_PRICE = new BigDecimal("0.01");

    /**
     * ClipDrop 价格
     */
    public static final BigDecimal CD_PRICE = new BigDecimal("0.026784");

    /**
     * 获取 engine
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> engineList() {
        return Arrays.stream(EngineEnum.values())
                .filter(item -> "IMAGE".equals(item.getEngineType()))
                .map(item -> of(item.getCode(), item.getLabel(), item.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 engine
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> upscalingEngineList() {
        return Arrays.stream(EngineEnum.values())
                .filter(item -> "UPSCALING".equals(item.getEngineType()))
                .map(item -> of(item.getCode(), item.getLabel(), item.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 samples
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> samplesList() {
        return Arrays.stream(SamplesEnum.values())
                .map(item -> of(item.getCode(), item.getLabel(), item.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 imageSize
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> imageSizeList() {
        return Arrays.stream(ImageSizeEnum.values())
                .map(item -> of(item.getCode(), item.getLabel(), item.getDescription(), item.getScale()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 guidancePreset
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> guidancePresetList() {
        return Arrays.stream(GuidancePresetEnum.values())
                .map(item -> ofByMessage(item.getCode(), item.getLabel(), item.getDescription(), item.getImage()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 sampler
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> samplerList() {
        return Arrays.stream(SamplerEnum.values())
                .map(item -> ofByMessage(item.getCode(), item.getLabel(), item.getDescription(), item.getImage()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 stylePreset
     *
     * @return ImageMetaDTO
     */
    public static List<ImageMetaDTO> stylePresetList() {
        return Arrays.stream(StylePresetEnum.values())
                .map(item -> ofByMessage(item.getCode(), item.getLabel(), item.getDescription(), item.getImage()))
                .collect(Collectors.toList());
    }

    /**
     * 商品图模板
     *
     * @return 商品图模板
     */
    public static List<ImageMetaDTO> productTemplates() {
        return Arrays.stream(ProductImageTypeEnum.values()).filter(item -> StringUtils.isNotBlank(item.getPrompt())).map(item -> {
            ImageMetaDTO metadata = new ImageMetaDTO();
            metadata.setValue(item.getCode());
            metadata.setLabel(item.getLabel());
            Locale locale = LocaleContextHolder.getLocale();
            if (!Locale.CHINA.equals(locale)) {
                metadata.setLabel(item.getLabelEn());
            }
            metadata.setImage(item.getImage());
            return metadata;
        }).collect(Collectors.toList());
    }

    /**
     * 任务配置类型
     *
     * @return 任务配置类型
     */
    public static List<ImageMetaDTO> configTaskType() {
        return Arrays.stream(ImageTaskConfigTypeEnum.values()).map(item -> {
            ImageMetaDTO metadata = new ImageMetaDTO();
            metadata.setValue(item.getCode());
            metadata.setLabel(item.getLabel());
            Locale locale = LocaleContextHolder.getLocale();
            if (!Locale.CHINA.equals(locale)) {
                metadata.setLabel(item.getLabelEn());
            }
            return metadata;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为 ImageMetaDTO
     *
     * @param code        枚举值
     * @param name        名称
     * @param description 描述
     * @return ImageMetaDTO
     */
    public static ImageMetaDTO of(Object code, String name, String description) {
        ImageMetaDTO meta = new ImageMetaDTO();
        meta.setValue(code);
        meta.setLabel(name);
        meta.setDescription(description);
        return meta;
    }

    /**
     * 转换为 ImageMetaDTO
     *
     * @param code        枚举值
     * @param name        名称
     * @param description 描述
     * @param scale       图片比例
     * @return ImageMetaDTO
     */
    public static ImageMetaDTO of(Object code, String name, String description, String scale) {
        ImageMetaDTO meta = new ImageMetaDTO();
        meta.setValue(code);
        meta.setLabel(name);
        meta.setDescription(description);
        meta.setScale(scale);
        return meta;
    }

    /**
     * 转换为 ImageMetaDTO
     *
     * @param code            枚举值
     * @param nameCode        名称国际化code
     * @param descriptionCode 描述国际化code
     * @return ImageMetaDTO
     */
    public static ImageMetaDTO ofByMessage(Object code, String nameCode, String descriptionCode, String image) {
        ImageMetaDTO meta = new ImageMetaDTO();
        meta.setValue(code);
        meta.setLabel(MessageUtil.getMessage(nameCode));
        meta.setDescription(MessageUtil.getMessage(descriptionCode));
        meta.setImage(image);
        return meta;
    }

    /**
     * 计算回答图片消耗的 SD 点数
     *
     * @param request 请求参数
     * @return token数量
     */
    public static BigDecimal countAnswerCredits(String engine, Integer steps, Integer width, Integer height) {
        if (steps == null) {
            steps = 50;
        }
        if (width == null) {
            width = 512;
        }
        if (height == null) {
            height = 512;
        }
        BigDecimal stepsDecimal = new BigDecimal(steps.toString());
        BigDecimal multiplier = new BigDecimal("100");

        // SDXL 0.9 || SDXL 1.0
        if (EngineEnum.STABLE_DIFFUSION_XL_1024_V0_9.getCode().equals(engine) || EngineEnum.STABLE_DIFFUSION_XL_1024_V1_0.getCode().equals(engine)) {
            BigDecimal factor;
            if (steps == 30) {
                factor = new BigDecimal("0.016");
            } else if (steps == 50) {
                factor = new BigDecimal("0.02");
            } else {
                BigDecimal factorFirst = new BigDecimal("0.0122");
                BigDecimal factorSecond = new BigDecimal("0.000127").multiply(stepsDecimal);
                BigDecimal factorThird = new BigDecimal("0.000000623").multiply(stepsDecimal).multiply(stepsDecimal);
                factor = factorFirst.add(factorSecond).add(factorThird);
            }
            return multiplier.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        }

        // Upscaler
        BigDecimal factorFirst = new BigDecimal(width.toString()).multiply(new BigDecimal(height.toString()));
        if (EngineEnum.STABLE_DIFFUSION_X4_LATENT_UPSCALER.getCode().equals(engine)) {
            return factorFirst.compareTo(new BigDecimal("262144")) > 0 ? new BigDecimal("12") : new BigDecimal("8");
        }
        if (EngineEnum.ESRGAN_V1_X2PLUS.getCode().equals(engine)) {
            return new BigDecimal("0.2");
        }

        factorFirst = factorFirst.subtract(new BigDecimal("169527"));
        factorFirst = factorFirst.multiply(stepsDecimal);
        factorFirst = factorFirst.divide(new BigDecimal("30"), 10, RoundingMode.HALF_UP);

        // SDXL Beta
        if (EngineEnum.STABLE_DIFFUSION_XL_BETA_V2_2_2.getCode().equals(engine)) {
            BigDecimal factorSecond = new BigDecimal("5.4e-8");
            return factorFirst.multiply(factorSecond).multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }

        // Other
        BigDecimal factorSecond = new BigDecimal("2.16e-8");
        return factorFirst.multiply(factorSecond).multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算回答图片消耗的 SD 点数
     *
     * @param request 请求参数
     * @return token数量
     */
    public static BigDecimal countAnswerCredits(VariantsImageRequest request) {
        String engine = request.getEngine();
        Integer steps = request.getSteps();
        Integer width = request.getWidth();
        Integer height = request.getHeight();
        return countAnswerCredits(engine, steps, width, height);
    }

    /**
     * 计算回答图片消耗的 SD 点数
     *
     * @param request 请求参数
     * @return token数量
     */
    public static BigDecimal countAnswerCredits(GenerateImageRequest request) {
        String engine = request.getEngine();
        Integer steps = request.getSteps();
        Integer width = request.getWidth();
        Integer height = request.getHeight();
        return countAnswerCredits(engine, steps, width, height);
    }

    /**
     * 计算回答图片消耗的 SD 点数
     *
     * @param request 请求参数
     * @return token数量
     */
    public static BigDecimal countAnswerCredits(UpscaleImageRequest request) {
        return new BigDecimal("0.2");
    }

    /**
     * 计算回答图片的token数量
     *
     * @param credits 消耗点数
     * @return token数量
     */
    public static Integer countAnswerTokens(BigDecimal credits) {
        return credits.multiply(new BigDecimal("100")).intValue();
    }

    /**
     * 处理反义词
     *
     * @param negativePrompt 反义词
     * @return 处理后的反义词
     */
    public static String handleNegativePrompt(String negativePrompt, boolean isJoin) {
        if (isJoin) {
            if (StringUtils.isBlank(negativePrompt)) {
                return AppConstants.DEFAULT_IMAGE_NEGATIVE_PROMPT;
            } else {
                if (StringUtils.startsWith(negativePrompt, AppConstants.DEFAULT_IMAGE_NEGATIVE_PROMPT)) {
                    return negativePrompt;
                }
                return AppConstants.DEFAULT_IMAGE_NEGATIVE_PROMPT + ", " + negativePrompt;
            }
        }
        if (StringUtils.startsWith(negativePrompt, AppConstants.DEFAULT_IMAGE_NEGATIVE_PROMPT)) {
            if (StringUtils.equals(negativePrompt, AppConstants.DEFAULT_IMAGE_NEGATIVE_PROMPT)) {
                return "";
            } else {
                String negative = negativePrompt.substring(AppConstants.DEFAULT_IMAGE_NEGATIVE_PROMPT.length()).trim();
                if (StringUtils.startsWith(negative, ",") || StringUtils.startsWith(negative, "，") || StringUtils.startsWith(negative, ".") || StringUtils.startsWith(negative, "。")) {
                    return negative.substring(1).trim();
                }
            }
        }
        return negativePrompt;
    }

    /**
     * 处理 Prompt
     *
     * @param prompt Prompt
     * @param isJoin 是否拼接
     * @return 处理后的 Prompt
     */
    public static String handlePrompt(String prompt, boolean isJoin) {
        if (isJoin) {
            if (StringUtils.isBlank(prompt)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.PROMPT_IS_REQUIRED);
            } else {
                if (StringUtils.startsWith(prompt, AppConstants.DEFAULT_IMAGE_PROMPT)) {
                    return prompt;
                }
                return AppConstants.DEFAULT_IMAGE_PROMPT + prompt;
            }
        }
        if (StringUtils.startsWith(prompt, AppConstants.DEFAULT_IMAGE_PROMPT)) {
            if (StringUtils.equals(prompt, AppConstants.DEFAULT_IMAGE_PROMPT)) {
                return "";
            } else {
                return prompt.substring(AppConstants.DEFAULT_IMAGE_PROMPT.length()).trim();
            }
        }
        return prompt;
    }

    /**
     * 处理图片base64
     *
     * @param base64Image base64
     * @return 处理后的base64
     */
    public static String handlerBase64Image(String base64Image) {
        int commaIndex = base64Image.indexOf(',');
        if (commaIndex != -1) {
            return base64Image.substring(commaIndex + 1);
        } else {
            return base64Image;
        }
    }

    public static String encodeBase64(BufferedImage image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
        } catch (IOException exception) {
            throw new IllegalArgumentException("图片转换base64失败", exception);
        }

        byte[] bytes = bos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }
}

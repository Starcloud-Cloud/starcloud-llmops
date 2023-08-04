package com.starcloud.ops.business.app.util;

import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.vsearch.EngineEnum;
import com.starcloud.ops.business.app.enums.vsearch.GuidancePresetEnum;
import com.starcloud.ops.business.app.enums.vsearch.ImageSizeEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplesEnum;
import com.starcloud.ops.business.app.enums.vsearch.StylePresetEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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
    public static BigDecimal countAnswerCredits(ImageRequest request) {
        String engine = request.getEngine();
        Integer steps = request.getSteps();
        Integer width = request.getWidth();
        Integer height = request.getHeight();
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

        // SDXL 0.9
        if (EngineEnum.STABLE_DIFFUSION_XL_1024_V0_9.getCode().equals(engine)) {
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
     * 计算回答图片的token数量
     *
     * @param credits 消耗点数
     * @return token数量
     */
    public static Integer countAnswerTokens(BigDecimal credits) {
        return credits.multiply(new BigDecimal("100")).intValue();
    }

    /**
     * 计算消息中的token数量
     *
     * @param input 消息
     * @return token数量
     */
    public static int countMessageTokens(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        int count = 0;
        int length = bytes.length;

        for (int i = 0; i < length; i++) {
            byte currentByte = bytes[i];

            if ((currentByte & 0b10000000) == 0b00000000) {
                // 1-byte UTF-8 character
                count++;
            } else if ((currentByte & 0b11100000) == 0b11000000) {
                // 2-byte UTF-8 character
                count++;
                i++;
            } else if ((currentByte & 0b11110000) == 0b11100000) {
                // 3-byte UTF-8 character
                count++;
                i += 2;
            } else if ((currentByte & 0b11111000) == 0b11110000) {
                // 4-byte UTF-8 character
                count++;
                i += 3;
            }
        }

        return count;
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
                return AppConstants.DEFAULT_NEGATIVE_PROMPT;
            } else {
                if (StringUtils.endsWith(negativePrompt, ".")) {
                    negativePrompt = negativePrompt.substring(0, negativePrompt.length() - 1);
                }
                return negativePrompt + ", " + AppConstants.DEFAULT_NEGATIVE_PROMPT;
            }
        }
        if (StringUtils.endsWith(negativePrompt, AppConstants.DEFAULT_NEGATIVE_PROMPT)) {
            if (StringUtils.equals(negativePrompt, AppConstants.DEFAULT_NEGATIVE_PROMPT)) {
                return "";
            } else {
                return negativePrompt.substring(0, negativePrompt.length() - AppConstants.DEFAULT_NEGATIVE_PROMPT.length() - 2) + ".";
            }
        }
        return negativePrompt;
    }

    /**
     * 处理图片base64
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
}

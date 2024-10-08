package com.starcloud.ops.business.app.service.image.impl.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starcloud.ops.business.app.enums.image.pixabay.PixabayCategoryEnum;
import com.starcloud.ops.business.app.enums.image.pixabay.PixabayOrderEnum;
import com.starcloud.ops.business.app.enums.image.pixabay.PixabayRequestLangueEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Pixabay 图片请求 DTO
 * PixabayImageRequestDTO
 */
@Data
public class PixabayCommonRequestDTO {

    @Schema(description = "Pixabay API key")
    protected String key;

    @Schema(description = "URL编码的搜索词。如果省略，则返回所有图像。该值不能超过100个字符。", maxLength = 100)
    @Length(max = 100,message = "关键词长度最长不可超过 100")
    protected String q;

    //取值范围:cs、da、de、en、es、fr、id、it、hu、nl、no、pl、pt、ro、sk、fi、sv、tr、vi、th、bg、ru、el、ja、ko、zh
    @Schema(description = "要搜索的语言")
    @InEnum(value = PixabayRequestLangueEnum.class)
    protected String lang;

    @Schema(description = "单个图像ID")
    protected String id;


    /**
     * Accepted values: backgrounds, fashion, nature, science, education, feelings, health, people, religion, places, animals, industry, computer, food, sports, transportation, travel, buildings, business, music <br/>
     * 接受的价值观:背景、时尚、自然、科学、教育、情感、健康、人、宗教、地方、动物、工业、计算机、食品、体育、交通、旅游、建筑、商业、音乐
     */
    @Schema(description = "按类别筛选")
    @InEnum(value = PixabayCategoryEnum.class)
    protected String category;


    @Schema(description = "最小图像宽度", defaultValue = "0")
    @JsonProperty(value = "min_width")
    protected int min_width;

    @Schema(description = "通过颜色属性过滤图像", defaultValue = "0")
    @JsonProperty(value = "min_height")
    protected int min_height;

    /**
     * Select images that have received an Editor's Choice award.
     * Accepted values: "true", "false"
     * Default: "false" <br><br/>
     * 选择获得编辑选择奖的图片。
     * 可接受值:"true"， "false"
     * 默认值:“假”
     */
    @Schema(description = "选择获得编辑选择奖的图片。")
    @JsonProperty(value = "editors_choice", defaultValue = "false")
    protected Boolean editorsChoice;


    /**
     * A flag indicating that only images suitable for all ages should be returned.
     * Accepted values: "true", "false"
     * Default: "false" <br/>
     * 表示只返回适合所有年龄的图像的标志。
     * 可接受值:"true"， "false"
     * 默认值:“假”
     */
    @Schema(description = "只返回适合所有年龄的图像", defaultValue = "false")
    @JsonProperty(value = "safesearch")
    protected Boolean safeSearch;


    /**
     * How the results should be ordered.
     * Accepted values: "popular", "latest"
     * Default: "popular"
     * <br/>
     * 结果应该如何排序。
     * 接受值:“流行”、“最新”
     * 默认值:“popular”
     */
    @Schema(description = "结果应该如何排序。", defaultValue = "popular")
    @InEnum(value = PixabayOrderEnum.class)
    protected String order;


    /**
     * Returned search results are paginated.  Use this parameter to select the page number.
     * Default: 1
     * <br/>
     * 对返回的搜索结果进行分页。使用此参数选择页码。
     * 默认值 1
     */
    @Schema(description = "按类别筛选", defaultValue = "1")
    @Min(value = 1, message = "页码的结果数量最小值为3")
    protected int page;

    /**
     * Determine the number of results per page.
     * Accepted values: 3 - 200
     * Default: 20
     * <br/>
     * 确定每页的结果数量。
     * 接受值:3 - 200
     * 默认值:20
     */
    @Schema(description = "每页的结果数量", defaultValue = "20")
    @JsonProperty(value = "per_page")
    @Max(value = 200, message = "每页的结果数量最大值为200")
    @Min(value = 3, message = "每页的结果数量最小值为3")
    protected String perPage;

    /**
     * JSONP回调函数名
     */
    @Schema(description = "JSONP回调函数名")
    protected String callback;

    /**
     * Indent JSON output. This option should not be used in production.
     * Accepted values: "true", "false"
     * Default: "false"
     * <br/>
     * 缩进JSON输出。这个选项不应该在生产环境中使用。
     * 可接受值:"true"， "false"
     * 默认值:“假”
     */
    @Schema(description = "缩进JSON输出", defaultValue = "false")
    protected Boolean pretty;

}

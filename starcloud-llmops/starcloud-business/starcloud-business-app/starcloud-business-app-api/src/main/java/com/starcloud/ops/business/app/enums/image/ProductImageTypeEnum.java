package com.starcloud.ops.business.app.enums.image;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Getter
public enum ProductImageTypeEnum implements IEnumable<Integer> {

    /**
     * 海边沙滩椰子树
     */
    SEASIDE_BEACH_COCONUT_TREE(1, "海边沙滩椰子树", "Seaside Beach And Coconut Tree", "https://download.hotsalecloud.com/mofaai/images/template/product/seaside-beach-coconut-tree.jpeg",
            "222222"),

    /**
     * 纯色摄影棚
     */
    SOLID_COLOR_STUDIO(2, "纯色摄影棚", "Solid Color Studio", "https://download.hotsalecloud.com/mofaai/images/template/product/solid-color-studio.jpeg",
            "3333333"),

    /**
     * 客厅
     */
    LIVING_ROOM(3, "客厅", "Living Room", "https://download.hotsalecloud.com/mofaai/images/template/product/living-room.jpeg",
            "44444"),

    /**
     * 卧室
     */
    BEDROOM(4, "卧室", "Bedroom", "https://download.hotsalecloud.com/mofaai/images/template/product/bedroom.jpeg",
            "555555"),

    /**
     * 咖啡店
     */
    COFFEE_SHOP(5, "咖啡店", "Coffee Shop", "https://download.hotsalecloud.com/mofaai/images/template/product/coffee-shop.jpeg",
            "666666"),

    /**
     * 健身房
     */
    FITNESS_ROOM(6, "健身房", "Fitness Room", "https://download.hotsalecloud.com/mofaai/images/template/product/fitness-room.jpeg",
            "77777"),

    /**
     * 厨房
     */
    KITCHEN(7, "厨房", "Kitchen", "https://download.hotsalecloud.com/mofaai/images/template/product/kitchen.jpeg", ""),

    /**
     * 办公室
     */
    OFFICE(8, "办公室", "Office", "https://download.hotsalecloud.com/mofaai/images/template/product/office.jpeg", ""),

    /**
     * 岩石鲜花
     */
    ROCK_FLOWER(9, "岩石鲜花", "Rock Flower", "https://download.hotsalecloud.com/mofaai/images/template/product/rock-flower.jpeg", ""),

    /**
     * 湖水植物
     */
    LAKE_PLANT(10, "湖水植物", "Lake Plant", "https://download.hotsalecloud.com/mofaai/images/template/product/lake-plant.jpeg", ""),

    /**
     * 渐变背景
     */
    GRADIENT_BACKGROUND(11, "渐变背景", "Gradient Background", "https://download.hotsalecloud.com/mofaai/images/template/product/gradient-background.jpeg", ""),

    /**
     * 雪景
     */
    SNOW_SCAPE(12, "雪景", "Snow Scape", "https://download.hotsalecloud.com/mofaai/images/template/produc.jpeg", ""),

    /**
     * 峡谷树河
     */
    CANYON_TREE_RIVER(13, "峡谷树河", "Canyon Tree River", "https://download.hotsalecloud.com/mofaai/images/template/product/canyon-tree-river.jpeg", ""),

    /**
     * 潮湿环境
     */
    WET_ENVIRONMENT(14, "潮湿环境", "Wet Environment", "https://download.hotsalecloud.com/mofaai/images/template/product/wet-environment.jpeg", ""),

    /**
     * 小黄花
     */
    SMALL_YELLOW_FLOWER(15, "小黄花", "Small Yellow Flower", "https://download.hotsalecloud.com/mofaai/images/template/product/small-yellow-flower.jpeg", ""),

    /**
     * 大理石烟雾
     */
    MARBLE_SMOKE(16, "大理石烟雾", "Marble Smoke", "https://download.hotsalecloud.com/mofaai/images/template/product/marble-smoke.jpeg", ""),

    /**
     * 树花日升
     */
    TREE_FLOWER_SUNRISE(17, "树花日升", "Tree Flower Sunrise", "https://download.hotsalecloud.com/mofaai/images/template/product/tree-flower-sunrise.jpeg", ""),

    /**
     * 加油站
     */
    GAS_STATION(18, "加油站", "Gas Station", "https://download.hotsalecloud.com/mofaai/images/template/product/gas-station.jpeg", ""),

    /**
     * 热带植物
     */
    TROPICAL_PLANT(19, "热带植物", "Tropical Plant", "https://download.hotsalecloud.com/mofaai/images/template/product/tropical-plant.jpeg", ""),

    /**
     * 西部公路
     */
    WESTERN_ROAD(20, "西部公路", "Western Road", "https://download.hotsalecloud.com/mofaai/images/template/product/western-road.jpeg", ""),

    /**
     * 废弃工厂
     */
    ABANDONED_FACTORY(21, "废弃工厂", "Abandoned Factory", "https://download.hotsalecloud.com/mofaai/images/template/product/abandoned-factory.jpeg", ""),

    /**
     * 樱花
     */
    CHERRY_BLOSSOM(22, "樱花", "Cherry Blossom", "https://download.hotsalecloud.com/mofaai/images/template/product/cherry-blossom.jpeg", ""),

    /**
     * 太空站
     */
    SPACE_STATION(23, "太空站", "Space Station", "https://download.hotsalecloud.com/mofaai/images/template/product/space-station.jpeg", ""),


    ;
    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 标签英文
     */
    private final String labelEn;

    /**
     * 图片
     */
    private final String image;

    /**
     * 提示
     */
    private final String prompt;

    /**
     * 构造方法
     *
     * @param code    编码
     * @param label   标签
     * @param labelEn 标签英文
     * @param image   图片
     * @param prompt  提示
     */
    ProductImageTypeEnum(Integer code, String label, String labelEn, String image, String prompt) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
        this.image = image;
        this.prompt = prompt;
    }
}

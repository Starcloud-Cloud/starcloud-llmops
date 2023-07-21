package com.starcloud.ops.business.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.LanguageEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.parameters.P;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class AppServiceTest extends BaseDbUnitTest {


    @Resource
    private AppService appService;

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @MockBean
    private DictDataService dictDataService;

    @MockBean
    private UserBenefitsService userBenefitsService;

    private static final List<DictDataDO> DICT_LIST = Arrays.asList(
            of("Amazon", "AMAZON", 1, "{\"icon\":\"amazon\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/amazon.jpg\",\"label\":{\"zh_CN\":\"亚马逊\",\"en_US\":\"Amazon\"},\"desc\":{\"zh_CN\":\"亚马逊Listing、产品分析及店铺管理等模板\",\"en_US\":\"Templates for Amazon Listing, product analysis and store management\"}}"),
            of("独立站", "WEBSITE", 2, "{\"icon\":\"website\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/website.jpg\",\"label\":{\"zh_CN\":\"独立站\",\"en_US\":\"Shopify\"},\"desc\":{\"zh_CN\":\"独立站建站所需品牌创建，产品洞察，产品说明等\",\"en_US\":\"Brand creation, product insights, product descriptions, etc. required for independent website construction\"}}"),
            of("社交媒体", "SOCIAL_MEDIA", 2, "{\"icon\":\"social-media\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/social-media.jpg\",\"label\":{\"zh_CN\":\"社交媒体\",\"en_US\":\"Social Media\"},\"desc\":{\"zh_CN\":\"国内外社交媒体文案创作\",\"en_US\":\"Social media copywriting at home and abroad\"}}"),
            of("邮件营销", "EMAIL", 2, "{\"icon\":\"email\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/email.jpg\",\"label\":{\"zh_CN\":\"邮件营销\",\"en_US\":\"Email\"},\"desc\":{\"zh_CN\":\"国内外社交媒体文案创作\",\"en_US\":\"Develop customers through foreign trade emails, build trust and sell products\"}}"),
            of("广告文案", "ADVERTISING", 2, "{\"icon\":\"advertising\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/advertising.jpg\",\"label\":{\"zh_CN\":\"广告文案\",\"en_US\":\"Advertising\"},\"desc\":{\"zh_CN\":\"撰写宣传广告的文字内容，用于推销产品或服务\",\"en_US\":\"Writing the text of an ad promoting a product or service\"}}"),
            of("办公助手", "OFFICE_ASSISTANT", 2, "{\"icon\":\"office-assistant\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/office-assistant.jpg\",\"label\":{\"zh_CN\":\"办公助手\",\"en_US\":\"Office Assistant\"},\"desc\":{\"zh_CN\":\"提供各种办公文案内容生成的工具\",\"en_US\":\"Provide tools for generating various office copy content\"}}"),
            of("SEO写作", "SEO_WRITING", 2, "{\"icon\":\"seo\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/seo.jpg\",\"label\":{\"zh_CN\":\"SEO写作\",\"en_US\":\"SEO Writing\"},\"desc\":{\"zh_CN\":\"关于写作方面的文案，有标题、大纲、续写及多步骤操作等\",\"en_US\":\"Regarding writing copy, there are titles, outlines, continuation, and multi-step operations\"}}"),
            of("日常生活", "DAILY_USE", 2, "{\"icon\":\"daily-use\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/daily-use.jpg\",\"label\":{\"zh_CN\":\"日常生活\",\"en_US\":\"Daily Use\"},\"desc\":{\"zh_CN\":\"日常生活中常用场景和活动\",\"en_US\":\"Common scenes and activities in daily life\"}}"),
            of("生成图片", "IMAGE", 2, "{\"icon\":\"image\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/image.jpg\",\"label\":{\"zh_CN\":\"生成图片\",\"en_US\":\"Image\"},\"desc\":{\"zh_CN\":\"按照描述生成对应的图片\",\"en_US\":\"Generate corresponding images according to the description\"}}"),
            of("有趣好玩", "FUN", 2, "{\"icon\":\"fun\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/fun.jpg\",\"label\":{\"zh_CN\":\"有趣好玩\",\"en_US\":\"Fun\"},\"desc\":{\"zh_CN\":\"可以让你放松一刻的好玩的文案\",\"en_US\":\"Fun copywriting that can make you relax for a moment\"}}"),
            of("准备面试", "RESUME", 2, "{\"icon\":\"resume\",\"image\":\"https://download.hotsalecloud.com/mofaai/images/category/resume.jpg\",\"label\":{\"zh_CN\":\"准备面试\",\"en_US\":\"Resume\"},\"desc\":{\"zh_CN\":\"用于创建和优化求职简历\",\"en_US\":\"For creating and optimizing resumes for job applications\"}}")
    );

    public static DictDataDO of(String lable, String value, Integer sort, String remark) {
        DictDataDO dictDataDO = new DictDataDO();
        dictDataDO.setLabel(lable);
        dictDataDO.setValue(value);
        dictDataDO.setSort(sort);
        dictDataDO.setRemark(remark);
        return dictDataDO;
    }

    @Test
    public void publishTest() {

    }

    @Test
    public void bathPublishTest() {
        AppPageQuery appPageQuery = new AppPageQuery();
        appPageQuery.setPageNo(1);
        appPageQuery.setPageSize(1000);
        PageResp<AppRespVO> page = appService.page(appPageQuery);
        List<AppRespVO> list = page.getList();
        CollectionUtil.emptyIfNull(list).forEach(item -> {
            AppPublishReqVO request = new AppPublishReqVO();
            request.setUid(item.getUid());
            String name = item.getName();
            request.setLanguage(detectLanguage(name));
            request.setCategories(item.getCategories());
            publish(request);
        });
    }

    @Test
    public void bathPublishTest2() {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .select(AppDO::getUid, AppDO::getName, AppDO::getCategories)
                .in(AppDO::getName, Arrays.asList("小红书文案",
                        "星座运势",
                        "今日头条文章",
                        "社媒帖子标题",
                        "独立站商品描述",
                        "生成PPT大纲",
                        "Generate Text",
                        "Generate Images"))
                .eq(AppDO::getDeleted, Boolean.FALSE);
        List<AppDO> appDOList = appMapper.selectList(wrapper);
        appDOList.forEach(item -> {
            AppPublishReqVO request = new AppPublishReqVO();
            request.setUid(item.getUid());
            String name = item.getName();
            request.setLanguage(detectLanguage(name));
            request.setCategories(Arrays.asList(item.getCategories().split(",")));
            publish(request);
        });
    }

    public void publish(AppPublishReqVO request) {

        DictDataExportReqVO requestd = new DictDataExportReqVO();
        requestd.setDictType(AppConstants.APP_CATEGORY_DICT_TYPE);
        requestd.setStatus(StateEnum.ENABLE.getCode());
        Mockito.when(dictDataService.getDictDataList(requestd)).thenReturn(DICT_LIST);

        appService.publish(request);

        AppDO byUid = appMapper.getByUid(request.getUid(), false);

        // 更新应用表
        appMapper.update(null, Wrappers.lambdaUpdate(AppDO.class)
                .set(AppDO::getUpdater, "1")
                .set(AppDO::getUpdateTime, LocalDateTime.now())
                .eq(AppDO::getUid, byUid.getUid())
        );

        // 更新市场表
        appMarketMapper.update(null, Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getCreator, "1")
                .set(AppMarketDO::getUpdater, "1")
                .set(AppMarketDO::getTenantId, 1L)
                .set(AppMarketDO::getExample, byUid.getExample())
                .set(AppMarketDO::getCreateTime, LocalDateTime.now())
                .set(AppMarketDO::getUpdateTime, LocalDateTime.now())
                .set(AppMarketDO::getAudit, AppMarketAuditEnum.APPROVED.getCode())
                .eq(AppMarketDO::getUid, AppUtils.obtainUid(byUid.getPublishUid()))
                .eq(AppMarketDO::getVersion, AppUtils.obtainVersion(byUid.getPublishUid()))
        );
        log.info("Publish app success");
    }


    public static String detectLanguage(String input) {
        boolean containsChinese = false;

        for (char c : input.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                containsChinese = true;
                break;
            }
        }

        if (containsChinese) {
            return LanguageEnum.ZH_CN.getCode();
        } else {
            return LanguageEnum.EN_US.getCode();
        }
    }

}

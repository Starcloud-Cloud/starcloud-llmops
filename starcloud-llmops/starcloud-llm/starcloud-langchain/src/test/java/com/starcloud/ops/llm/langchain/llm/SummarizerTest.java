package com.starcloud.ops.llm.langchain.llm;

import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.memory.summary.SummarizerMixin;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class SummarizerTest extends SpringBootTests {


    @Test
    public void summarizerMixinPromptTest() {

        String content = "浅谈亚马逊新品的广告架构，明确各个广告组的目的 - 知无不言跨境电商社区 \n" +
                "![](https://www.wearesellers.com/static/common/no-js.jpg)\n" +
                "你的浏览器禁用了JavaScript, 请开启后刷新浏览器获得更好的体验!\n" +
                "[![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/common/logo.png)](#)\n" +
                "===========================================================================\n" +
                "![](static/images/arrow.png) \n" +
                "* [本站](javascript:;)\n" +
                "* 百度\n" +
                "* 谷歌\n" +
                "* bing\n" +
                "* Yahoo\n" +
                " \n" +
                "[登录](https://www.wearesellers.com/account/login/)[注册](https://www.wearesellers.com/account/register/)\n" +
                "* [发现](https://www.wearesellers.com/)\n" +
                "* [排名](https://www.wearesellers.com/people/)\n" +
                "* [头条](https://www.wearesellers.com/headline/)\n" +
                "* [资源](https://www.wearesellers.com/source/)\n" +
                "* [市场](https://www.wearesellers.com/supply/)\n" +
                "* [圈子](https://www.wearesellers.com/topic/)\n" +
                "* [TikTok](https://imtiktoker.com)\n" +
                "* [活动![](/static/images/paid_read.png)](https://www.wearesellers.com/activity/)\n" +
                "* [帮助](javascript:;)\n" +
                " \n" +
                "[社区](/) [发现](/) [Amazon](/category-1) [浅谈亚马逊新品的广告架构，明确各个广告组...](https://www.wearesellers.com/question/75677)\n" +
                "------------------------------------------------------------------------------------------------------------\n" +
                "所在分类: [Amazon](/category-1) 所属圈子: [Amazon](https://www.wearesellers.com/topic/Amazon) [Amazon PPC](https://www.wearesellers.com/topic/Amazon+PPC)\n" +
                "浅谈亚马逊新品的广告架构，明确各个广告组的目的\n" +
                "=======================\n" +
                "发帖11次 被置顶2次 被推荐2次 质量分1星 回帖互动690次 历史交流热度5.27% 历史交流深度0%\n" +
                " \n" +
                "**新品广告前期广告词库的建立** \n" +
                "[![https://assert.wearesellers.com/questions/20230605/f20b66d3fa7a517a6850ca5b259b7877.png](https://assert.wearesellers.com/questions/20230605/f20b66d3fa7a517a6850ca5b259b7877.png \"https://assert.wearesellers.com/questions/20230605/f20b66d3fa7a517a6850ca5b259b7877.png\")](https://assert.wearesellers.com/questions/20230605/f20b66d3fa7a517a6850ca5b259b7877.png) \n" +
                "结合品牌分析建立关键词词库 上新之前搜集类目搜索词 按照搜索排名的方式进行排序 \n" +
                "搜索排名 1万以内 作为一级词 核心关键词 推排名 \n" +
                "搜索排名 2-5万 作为二级词 拓展流量入口 \n" +
                "搜索排名 6-10万 作为三级词 转化词降ACOS \n" +
                "PS 关键词转化共享前三位 >50 % 的词已经高度垄断 不建议前期 \n" +
                "切入 \n" +
                " \n" +
                " \n" +
                "初步广告架构的构建 \n" +
                "明确各广告组的目的，我建议分为以下主要目的的广告组 \n" +
                "1\\. 一级核心关键词的排名推进组 （关键词） \n" +
                "2.二级关键词扩展流量入口组 （关键词） \n" +
                "3.三级关键词 总体acos降低组 （关键词） \n" +
                "4..竞品流量关联组 （ASIN） \n" +
                "5.自我流量闭环组 （ASIN） \n" +
                "6\\. 品牌广告核心词补充位 （关键词 老带新 一推多） \n" +
                "7.展示型 购买在营销/人群 （ 强曝光 流量广泛） \n" +
                " \n" +
                " \n" +
                "按照新品前期的关键词分类做好不同的广告架构组合 \n" +
                " \n" +
                "1\\. 一级核心关键词的排名推进组 可以选择ABA排名前一万的关键词进行推广 每组建议选3-5个 竞价可以选择固定竞价 以及 提高/降低 最快跑出去 测试转化 \n" +
                "找出转化最好的关键词 进行第二步核心推广 （目的最快的提升该关键词下面的搜索排名） 这类广告组 不看acos 只关注转化和广告坑位 \n" +
                " \n" +
                "建议选择的广告类型 SP-MT-KT-EX Ads \n" +
                " \n" +
                " \n" +
                "2.二级关键词扩展流量入口组 可以选择ABA排名前2-5万的关键词 竞价建议选择固定 以及只降低 这个组 只有能跑出去 每天有曝光 算ok ，关注转化和acos 定期砍掉转化不好的关键词 \n" +
                " \n" +
                "建议选择的广告类型 SP-MT-KT-EX Ads SP-MT-KT-PH Ads \n" +
                " \n" +
                "3\\. 三级关键词 总体acos降低组 可以选择ABA排名前5-10万的关键词 建议选择广泛 竞价选择只降低来跑 这个组不看转化 只关注acos 它的目的就是用来降低总体的广告acos ，acos 不好直接砍掉 同时定期更换关键词 \n" +
                " \n" +
                "建议选择的广告类型 SP-MT-KT-BR Ads \n" +
                " \n" +
                " \n" +
                "4..竞品流量关联组 这个很好理解 直接挂竞品ASIIN 吸引竞品流量 \n" +
                " \n" +
                "建议选择的广告类型 SP-MT-PT-IP （SP广告定位） SB-PC-NLP-PT-IP （SB广告定位） SD-PT-IP Ads （SD广告定位） \n" +
                " \n" +
                " \n" +
                "5.自我流量闭环组 挂自己的ASIN 两个目的 防止别人吸你的流量 / 和自己的老品形成流量闭环 （做品线的可以大力投这类型的广告 可以提高整体的店铺销售） \n" +
                " \n" +
                "建议选择的广告类型 SP-MT-PT-IP （SP广告定位） SB-PC-NLP-PT-IP （SB广告定位） SD-PT-IP Ads （SD广告定位） \n" +
                " \n" +
                "6.品牌广告核心词补充位 SP广告测试表现好的关键词 在品牌广告上面进行拓展 其一可以增加广告坑位 其二可以起到老带新的作用 . \n" +
                " \n" +
                "建议选择的广告类型 SB-PC-NLP-KT Ads \n" +
                " \n" +
                " \n" +
                "7.展示型 购买在营销/人群 这类组合 通常在基本的广告架构完善下 想要更多的流量 可以增加这类型的广告组 这类流量虽然比较泛 但是有点好处 就是PPC均价低 曝光更大 （可以同时在站外获得曝光 亚马逊唯一可以获得站外流量的广告） \n" +
                " \n" +
                "建议选择的广告类型 SD-AU-VRC Ads SD-AU-VRP Ads SD-AU-AA \n" +
                " \n" +
                " \n" +
                " \n" +
                "[![https://assert.wearesellers.com/questions/20230605/84dd7f4cdb3ca7b1e5da9c8b307745cc.png](https://assert.wearesellers.com/questions/20230605/84dd7f4cdb3ca7b1e5da9c8b307745cc.png \"https://assert.wearesellers.com/questions/20230605/84dd7f4cdb3ca7b1e5da9c8b307745cc.png\")](https://assert.wearesellers.com/questions/20230605/84dd7f4cdb3ca7b1e5da9c8b307745cc.png) \n" +
                " \n" +
                "觉得有用点个赞或感谢 后面有机会就分享架构的优化策略以及链接不同阶段 广告的侧重点..........................\n" +
                "2023-06-05 35 条评论\n" +
                "分享\n" +
                "* [微信](# \"分享到微信\")\n" +
                "* [QQ](# \"分享到QQ\")\n" +
                "* [微博](# \"分享到新浪微博\")\n" +
                "* [空间](# \"分享到QQ空间\")\n" +
                "没有找到相关结果\n" +
                "已邀请:\n" +
                "与内容相关的链接\n" +
                "--------\n" +
                " 提交\n" +
                " [![](https://assert.wearesellers.com/ad/b47a0d301c6e4df025624b8a242962f2.jpg)](https://global.lianlianpay.com/activity/MercadoCard?invitecode=3B2V8Y) \n" +
                "89 个回复\n" +
                "------\n" +
                " [![](https://assert.wearesellers.com/avatar/000/14/83/05_avatar_mid.jpg?rand1695637953)](https://www.wearesellers.com/people/%E9%86%92%E6%97%B6%E6%98%8E%E6%9C%88%E9%86%89%E6%B8%85%E9%A3%8E) \n" +
                "[醒时明月醉清风](https://www.wearesellers.com/people/%E9%86%92%E6%97%B6%E6%98%8E%E6%9C%88%E9%86%89%E6%B8%85%E9%A3%8E) \\- 风城玫瑰，永不凋零\n" +
                "赞同来自: [芝芝知了](https://www.wearesellers.com/people/%E8%8A%9D%E8%8A%9D%E7%9F%A5%E4%BA%86) _、_ [久久jojo22](https://www.wearesellers.com/people/%E4%B9%85%E4%B9%85jojo22) _、_ [Vickie文](https://www.wearesellers.com/people/Vickie%E6%96%87) _、_ [麦当劳吃泡芙](https://www.wearesellers.com/people/%E9%BA%A6%E5%BD%93%E5%8A%B3%E5%90%83%E6%B3%A1%E8%8A%99) _、_ [香葱小当家](https://www.wearesellers.com/people/%E9%A6%99%E8%91%B1%E5%B0%8F%E5%BD%93%E5%AE%B6) _、_ [阿噗Irene](https://www.wearesellers.com/people/%E9%98%BF%E5%99%97Irene) _、_ [鼠小新](https://www.wearesellers.com/people/%E9%BC%A0%E5%B0%8F%E6%96%B0) _、_ [Tito123](https://www.wearesellers.com/people/Tito123) _、_ [起名很难系列](https://www.wearesellers.com/people/%E8%B5%B7%E5%90%8D%E5%BE%88%E9%9A%BE%E7%B3%BB%E5%88%97) _、_ [CDQLDP](https://www.wearesellers.com/people/CDQLDP) _、_ [江南小渔](https://www.wearesellers.com/people/%E6%B1%9F%E5%8D%97%E5%B0%8F%E6%B8%94) _、_ [泰泰lor](https://www.wearesellers.com/people/%E6%B3%B0%E6%B3%B0lor) _、_ [小小捡漏王](https://www.wearesellers.com/people/%E5%B0%8F%E5%B0%8F%E6%8D%A1%E6%BC%8F%E7%8E%8B) _、_ [浮生半日闲](https://www.wearesellers.com/people/%E6%B5%AE%E7%94%9F%E5%8D%8A%E6%97%A5%E9%97%B2) _、_ [奶凶兔爷](https://www.wearesellers.com/people/%E5%A5%B6%E5%87%B6%E5%85%94%E7%88%B7) _、_ [无名](https://www.wearesellers.com/people/gavinsu) _、_ [343437](https://www.wearesellers.com/people/343437) _、_ [哎咿呀](https://www.wearesellers.com/people/%E5%93%8E%E5%92%BF%E5%91%80) _、_ [深圳陈阿维](https://www.wearesellers.com/people/%E6%B7%B1%E5%9C%B3%E9%99%88%E9%98%BF%E7%BB%B4) _、_ [小三木1988](https://www.wearesellers.com/people/%E5%B0%8F%E4%B8%89%E6%9C%A81988) _、_ [半屏促](https://www.wearesellers.com/people/Ahu) _、_ [device](https://www.wearesellers.com/people/device) _、_ [娜不可露露](https://www.wearesellers.com/people/%E5%A8%9C%E4%B8%8D%E5%8F%AF%E9%9C%B2%E9%9C%B2) _、_ [NINO](https://www.wearesellers.com/people/NINO) _、_ [Eric运营小白](https://www.wearesellers.com/people/Eric%E8%BF%90%E8%90%A5%E5%B0%8F%E7%99%BD) _、_ [SanAAAAA](https://www.wearesellers.com/people/SanAAAAA) _、_ [Heytimi](https://www.wearesellers.com/people/Heytimi) _、_ [su1604466285](https://www.wearesellers.com/people/su1604466285) _、_ [孤星剑圣](https://www.wearesellers.com/people/%E5%AD%A4%E6%98%9F%E5%89%91%E5%9C%A3) _、_ [寂寞的风](https://www.wearesellers.com/people/%E5%AF%82%E5%AF%9E%E7%9A%84%E9%A3%8E) _、_ [Freedommmmm](https://www.wearesellers.com/people/Christineeeee) _、_ [Samuelzhang](https://www.wearesellers.com/people/Samuelzhang) _、_ [奈何的心情](https://www.wearesellers.com/people/%E5%A5%88%E4%BD%95%E7%9A%84%E5%BF%83%E6%83%85) _、_ [杨Young22](https://www.wearesellers.com/people/stillyoung) _、_ [知夏知秋](https://www.wearesellers.com/people/%E7%9F%A5%E5%A4%8F%E7%9F%A5%E7%A7%8B) _、_ [Angelamaby](https://www.wearesellers.com/people/Angelamaby) _、_ [大溪地风情绵云冷萃](https://www.wearesellers.com/people/BelleChen) _、_ [lovely](https://www.wearesellers.com/people/suus) _、_ [跨境小Z](https://www.wearesellers.com/people/%E8%B7%A8%E5%A2%83%E5%B0%8FZ) _、_ [Shirley153](https://www.wearesellers.com/people/Shirley153) _、_ [RunningPineapple](https://www.wearesellers.com/people/RunningPineapple) _、_ [假设我是好人](https://www.wearesellers.com/people/lie666) _、_ [kuajing2021](https://www.wearesellers.com/people/kuajing2021) _、_ [木木木木122](https://www.wearesellers.com/people/mumua) _、_ [Nicole1019](https://www.wearesellers.com/people/Nicole1019) _、_ [Oliviavia](https://www.wearesellers.com/people/Oliviavia) _、_ [kamen99](https://www.wearesellers.com/people/kamen99) _、_ [hrouou](https://www.wearesellers.com/people/hrouou) _、_ [MittyLee](https://www.wearesellers.com/people/MittyLee) _、_ [海盗船长哎哟](https://www.wearesellers.com/people/%E6%B5%B7%E7%9B%97%E8%88%B9%E9%95%BF%E5%93%8E%E5%93%9F) _、_ [常山小飞龙](https://www.wearesellers.com/people/%E5%B8%B8%E5%B1%B1%E5%B0%8F%E9%A3%9E%E9%BE%99) _、_ [椒香小排骨](https://www.wearesellers.com/people/%E6%A4%92%E9%A6%99%E5%B0%8F%E6%8E%92%E9%AA%A8) _、_ [小猪呀呀呀1](https://www.wearesellers.com/people/%E5%B0%8F%E7%8C%AA%E5%91%80%E5%91%80%E5%91%801) _、_ [001001](https://www.wearesellers.com/people/001001) _、_ [祖安状元郎](https://www.wearesellers.com/people/%E7%A5%96%E5%AE%89%E7%8A%B6%E5%85%83%E9%83%8E) _、_ [老狗David](https://www.wearesellers.com/people/%E8%80%81%E7%8B%97David) _、_ [Keduoli](https://www.wearesellers.com/people/Keduoli) [更多 »](javascript:;)\n" +
                "很详细，谢谢分享~ 不过这个建立在词库上的不过是我们主观上给自己产品的评价，并没有由产品经历的历程，会不会对产品定位产生偏差（卖家秀和买家秀），由于新品期，客户的不稳定性，个人认为广泛与词组广告在构架中可能是一种更加合适的方式。 \n" +
                " 简单说一下自己的思路：**个人愚见，请勿见笑。。。** \n" +
                " 新品上市，需要大量数据让亚马逊收录产品，同时也需要尽量让的产品拉到一个相对高一点位置可以更快地使其产生曝光转化，快速积累评论。开始搭建的时候，BID就不能过低（BID最低也要不能低于系统建议竞价），尽量先高bid测试数据，根据得到的转化，出单情况，在逐步调整BID.一开始出就出现过低或过高的BID,有可能无法做出产品真实情况的分析。 \n" +
                " 预算方面：新品上市阶段，过高的预算会带来大量的流量，但是新品的销量无法承载这么大流量，直接会拉低产品的转化率。对于电商平台，没有转化的流量，会拉低系统对于产品相关性的评估，有可能会导致广告出单占比居高不下，但自然位置反而持续下滑。（预算30-50USD可能是初期的合适预算，新品期订单量可能就在10单左右，个人经验浅见，大佬请勿见笑。） \n" +
                " 可以把流量层级相近的关键词开到同一组广告中，快速的测试，保留高点击，高转化的关键词，可以快速找到合适的新品期关键词。词根，短尾词，长尾词都要测一遍，预算少可以滚动测，关掉一部分，再增加一部分。新品期关键词出单有一定随机性，并不是别人的好词，你一定能出单，找到你的合适出单词，需要广泛的测试。否词一定要做，每天要持续的否词，有跑出来的出单词，也要尽快捞出来开新组。**开源节流才是王道！**\n" +
                "2023-06-05 [**57**](javascript:;) [**1**](javascript:;) [32](javascript:;)\n" +
                "分享\n" +
                "* [微信](javascript:; \"分享到微信\")\n" +
                "* [QQ](javascript:; \"分享到QQ\")\n" +
                "* [微博](javascript:; \"分享到新浪微博\")\n" +
                "* [空间](javascript:; \"分享到QQ空间\")\n" +
                "为什么被折叠? [0 个回复被折叠](javascript:;)\n" +
                "要回复问题请先[登录](https://www.wearesellers.com/account/login/)或[注册](https://www.wearesellers.com/account/register/)\n" +
                " \n" +
                "[![](https://www.wearesellers.com/static/common/avatar-mid-img.png?rand1695637953)](https://www.wearesellers.com/people/)\n" +
                " 关注问题 \n" +
                "### 发起人\n" +
                "[![爱吃荔枝的小羊](https://assert.wearesellers.com/avatar/000/08/30/70_avatar_mid.jpg?rand1695637953)](https://www.wearesellers.com/people/%E7%88%B1%E5%90%83%E8%8D%94%E6%9E%9D%E7%9A%84%E5%B0%8F%E7%BE%8A)\n" +
                "[爱吃荔枝的小羊](https://www.wearesellers.com/people/%E7%88%B1%E5%90%83%E8%8D%94%E6%9E%9D%E7%9A%84%E5%B0%8F%E7%BE%8A)\n" +
                "高阶知识分享群干货 进群私信\n" +
                "### 问题状态\n" +
                "* 最新活动: 2 天前\n" +
                "* 浏览: 33386\n" +
                "* 关注: 791 人 [更多 >>](javascript:;)\n" +
                "[![](https://assert.wearesellers.com/ad/9b620520234047d78149d0ae87810d2c.png)](https://www.sellerspace.com/blog/the-combined-package-of-sellerspace-and-sellersprite/) [![](https://assert.wearesellers.com/ad/64f1b028422b0d476f72e83c8ec66d79.jpg)](https://qr14.cn/FePsxY) [![](https://assert.wearesellers.com/ad/51f5fc24affe6c2763b571a341fd8888.jpg)](https://www.mumamail.com/?statId=21) [![](https://assert.wearesellers.com/ad/4ee641ab6fe9227dcc3c17955db09c2e.jpg)](https://www.fangtion.com/zwby) [![](https://assert.wearesellers.com/ad/78aad1f5166a99bbdd53c722be4ee826.png)](https://www.lingxing.com/chatgpt?invite=nrzwbychatd)\n" +
                "### 推荐问题\n" +
                "* [希望大佬们针对广告不同阶段周期说说广告结构和打法策略：目前的大概思路是新品前期针对曝光点击进行激活，重点关注转化情况，提高关键词排名以及对出价的峰值和低值进行测试……](https://www.wearesellers.com/question/70952)\n" +
                "* [广告架构和广告逻辑](https://www.wearesellers.com/question/18610)\n" +
                "* [一款新品即将开卖，包含7个变体。因为之前做的产品都是单一变体，该类目转化率在5%-15%之间。新品上架自动广告和手动广告都留足了充足的预算，诚挚向各位大神求解广告架构、思路及预算分配比例](https://www.wearesellers.com/question/43403)\n" +
                "* [关于如何分析链接的广告表现？需要根据不同时期产品所需要的养分，或者说是条件，来通过广告为它助力……](https://www.wearesellers.com/question/48734)\n" +
                "* [关于最近在知无不言学到的有关Amazon知识与疑问汇总，破而后立，晓喻新生，希望每天都是在进步的路上，2022祝新人快速成长，老鸟重振雄风](https://www.wearesellers.com/question/56445)\n" +
                "* [小卖家新品推广，广告曝光很高但单很少，请教广告架构应该从哪些方面去优化，如何破局](https://www.wearesellers.com/question/64895)\n" +
                "* [求一个新品广告投放的框架和思路，产品情况：白帽运营，竞争大，产品单价分别为20 50 100美金 ，求分享对应单价的广告策略](https://www.wearesellers.com/question/80553)\n" +
                "* [小卖家新品推广，广告曝光很高但单很少，请教广告架构应该从哪些方面去优化，如何破局](https://www.wearesellers.com/question/64894)\n" +
                "* [亚马逊广告运营手把手教学第六期——广告架构](https://www.wearesellers.com/question/61931)\n" +
                "### 优质话题\n" +
                "* [各位觉得多少钱就够未来的开支？感觉做够了亚马逊，我想存一笔钱，之后可能会在全国旅游的路上。](https://www.wearesellers.com/question/81028)\n" +
                "* [相亲啦~，本人17年做亚马逊，目前做运营主管，底薪1w,提成0.5-3w，后期有创业打算。来吧单身同胞们！！！](https://www.wearesellers.com/question/81209)\n" +
                "* [关于新品推广的一些猜想，我们经常看到一些爆款上架之后会在极短的时间内冲到很高的排名，并且很快拿下一些关键词的首页位置，为什么？凭什么？](https://www.wearesellers.com/question/80830)\n" +
                "* [威望涨了！可以发帖了！！这是周六搬砖中最高兴的一件事！！！](https://www.wearesellers.com/question/81117)\n" +
                "* [广告投放的难点（持续更新）](https://www.wearesellers.com/question/81792)\n" +
                "* [一切从零开始，5年职业开荒佬的自我救赎日记](https://www.wearesellers.com/question/81059)\n" +
                "* [Cervical Pillow——一款跌落神坛的产品](https://www.wearesellers.com/question/81166)\n" +
                "* [卖产品还是卖IP？就李佳琦这个事来看的话，产品捆绑大主播真的好吗？](https://www.wearesellers.com/question/81326)\n" +
                "* [单干！启动！快来给我浇点冷水，我总感觉我要发财了！救救救救救！帮我看看我的思路是不是对的！顺便分享几个全托管注册下号的资料给大家](https://www.wearesellers.com/question/80659)\n" +
                "* [第一次带新人，抛砖引玉，分享我的亚马逊运营助理新人培养计划](https://www.wearesellers.com/question/80635)\n" +
                "### 案例分析\n" +
                "* [\\# 案例分析 # 集思广益：一直表现良好的广告组突然没有曝光了！零曝光和几十曝光，是“突然”！到底是怎么回事？是被人搞了还是怎么回事？](https://www.wearesellers.com/question/78126)\n" +
                "* [#案例分析# 入职一个月，排名越做越差。.店铺有大约15个产品，每个产品有4-15个不等的变体……目前我的操作，大家麻烦看看为啥我越操作链接越差](https://www.wearesellers.com/question/76415)\n" +
                "* [\\# 案例分析 # 同时上了两个外观98%相似的同类目产品，主要区别的是内部的材质，但是产品的功能是差不多的，从外观和功能上看不出区别，怎么打？](https://www.wearesellers.com/question/76005)\n" +
                "* [\\# 案例分析 # 爆！亚马逊广告新功能！无效流量点击被单独列算出来了，怪不得最近半年总觉得广告在被人整了！现在果真验证了，又一个有效的整人listing方法，怎么样应对这种恶搞呢](https://www.wearesellers.com/question/75908)\n" +
                "* [\\# 经营案例分析 # 技术入股占28%的股份，老板出资开始说的有钱，没有说投多少，类目利润比其他类目高些，去年10月份有歧义，现在老板投的钱已经全部拿到手了，接下来应该怎么样给他谈](https://www.wearesellers.com/question/75774)\n" +
                "* [\\# 案例分析 # 做家纺类目的都知道这种品卖到这个价格有多赚。这种毯子真的是靠社媒吗？](https://www.wearesellers.com/question/75569)\n" +
                "### 超级话题\n" +
                "* [「 知无不言 」于 5月11日晚上19:00 起将会进行2023年的首次系统升级，如有发现网站使用问题，请及时在本帖下留言，衷心感谢大家的支持和理解！](https://www.wearesellers.com/question/74248)\n" +
                "* [\\# 超级话题 # 关于亚马逊合伙人的一些思考：我是站在运营的角度去看待这个问题……](https://www.wearesellers.com/question/73386)\n" +
                "* [\\# 超级话题 # 疫情之后的亚马逊选品：2023年如何规划自己的产品线？](https://www.wearesellers.com/question/69476)\n" +
                "* [\\# 超级话题 # 2022年末随笔，2023的亚马逊发展前景，欧洲是否可以回暖？](https://www.wearesellers.com/question/69035)\n" +
                "* [\\# 超级话题 # 2022年黑五网一的第一天，大家快来嗮出自己的业绩！](https://www.wearesellers.com/question/67563)\n" +
                "* [\\# 超级话题 # 2022马上过完了，年初定的目标还好吗，完成了吗，我先来](https://www.wearesellers.com/question/67520)\n" +
                "### 优质专栏\n" +
                "[![](http://www.wearesellers.com/uploads/column/456b15c658c232907f96ec2b3d2ccad0.jpg?8598)](https://www.wearesellers.com/column/details/20)\n" +
                "[闯盟跨境电商学院Wade](https://www.wearesellers.com/column/details/20)\n" +
                "**124** 个文章, **868** 人关注\n" +
                "[![](http://www.wearesellers.com/uploads/column/fdb0a8de87f940d7643c932a02bdd3d6.jpg?6360)](https://www.wearesellers.com/column/details/14)\n" +
                "[跨境电商无涯说](https://www.wearesellers.com/column/details/14)\n" +
                "**288** 个文章, **564** 人关注\n" +
                "[![](https://assert.wearesellers.com/column/048d832f6f1dbfe08f6b662733264b1e.jpg)](https://www.wearesellers.com/column/details/185)\n" +
                "[跟着阿杜学跨境电商](https://www.wearesellers.com/column/details/185)\n" +
                "**380** 个文章, **671** 人关注\n" +
                "[![](https://assert.wearesellers.com/article/20230908/db7736515c421972f7d0645339dc0874.jpg)](https://www.wearesellers.com/column/details/243)\n" +
                "[i拼帖 iGroupDeals](https://www.wearesellers.com/column/details/243)\n" +
                "**13** 个文章, **47** 人关注\n" +
                "[![](https://assert.wearesellers.com/column/0f26e2d916d6e3c53835808ce6906182.jpg)](https://www.wearesellers.com/column/details/29)\n" +
                "[积特知识产权](https://www.wearesellers.com/column/details/29)\n" +
                "**365** 个文章, **265** 人关注\n" +
                "[![](https://assert.wearesellers.com/article/20230719/bbaa069cde7793c8c70fa12ce0c1437e.jpeg)](https://www.wearesellers.com/column/details/209)\n" +
                "[牛牛站外](https://www.wearesellers.com/column/details/209)\n" +
                "**20** 个文章, **19** 人关注\n" +
                "[![](https://assert.wearesellers.com/column/edbd22d9856a06a477cc5d0111396da9.jpg)](https://www.wearesellers.com/column/details/128)\n" +
                "[亚易知识产权集团](https://www.wearesellers.com/column/details/128)\n" +
                "**341** 个文章, **127** 人关注\n" +
                "[![](https://assert.wearesellers.com/column/d21ac0fd80c4898623d004d47e4fbcba.jpg)](https://www.wearesellers.com/column/details/218)\n" +
                "[OgCloud自研企业出海SaaS平台](https://www.wearesellers.com/column/details/218)\n" +
                "**228** 个文章, **18** 人关注\n" +
                "[![](https://assert.wearesellers.com/column/5147d5ebd94aaae7e026a057e17182cd.jpg)](https://www.wearesellers.com/column/details/150)\n" +
                "[奇点出海](https://www.wearesellers.com/column/details/150)\n" +
                "**315** 个文章, **319** 人关注\n" +
                "[![](https://assert.wearesellers.com/article/20230906/55757e7b8c9bb776a379c5690ec919f2.jpg)](https://www.wearesellers.com/column/details/261)\n" +
                "[大拇指站外](https://www.wearesellers.com/column/details/261)\n" +
                "**1** 个文章, **0** 人关注\n" +
                "### 知无不言社区卖家微信群\n" +
                "* **现在扫码加入，超过19万跨境电商人关注......**\n" +
                "* ![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/common/join-qrcode.jpg) 今天， \n" +
                " 信息浩瀚，时光匆匆。 \n" +
                " 对品牌的向往、对使命的尊重， \n" +
                " 唯有勤奋者可以紧握时代脉搏。 \n" +
                " 博观约取，勇敢精进， \n" +
                " 不负青春荣光。\n" +
                "* ![](https://www.wearesellers.com/static/common/admin-people-logo.gif)\n" +
                "### 热门资源\n" +
                "[![](https://assert.wearesellers.com/article/20221015/8e4bf862d9b3ace5aa1f1ff21602fa93.gif)](https://www.wearesellers.com/source/100363)\n" +
                "#### [火龙果申诉/和解 亚马逊...](https://www.wearesellers.com/source/100363 \"火龙果申诉/和解 亚马逊账号解封/和解—专业律师团队\")\n" +
                "###### ￥ 3500.00-4000.00\n" +
                "_安徽河马网络信息咨询有限公司_ **27个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20230607/ec0973b4b006292d535d1ae909bc448d.jpg)](https://www.wearesellers.com/source/100073)\n" +
                "#### [索诺申诉/和解-专业解封...](https://www.wearesellers.com/source/100073 \"索诺申诉/和解-专业解封亚马逊账号/恢复listing\")\n" +
                "###### ￥ 3000.00-20000.00\n" +
                "_合肥索诺商务咨询服务有限公司_ **27个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20220117/f309b707477922030cc3473706d90a30.jpg)](https://www.wearesellers.com/source/100389)\n" +
                "#### [逊事无忧专业申诉美国团队...](https://www.wearesellers.com/source/100389 \"逊事无忧专业申诉美国团队解封账号及链接\")\n" +
                "###### ￥ 999.00-8000.00\n" +
                "_深圳市汇聚成林科技有限公司_ **25个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20210818/af026d28b5f5e727246c6380dd758395.jpg)](https://www.wearesellers.com/source/100387)\n" +
                "#### [鲸叹申诉-店铺解封/li...](https://www.wearesellers.com/source/100387 \"鲸叹申诉-店铺解封/listing申诉/侵权和解/紧急救号\")\n" +
                "###### ￥ 1299.00-3999.00\n" +
                "_鲸叹（嘉兴）网络有限责任公司_ **23个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20230608/55a9e5a0639385a961289eb2b539ec5b.jpg)](https://www.wearesellers.com/source/100221)\n" +
                "#### [海象专业申诉&...](https://www.wearesellers.com/source/100221 \" 海象专业申诉&和解-店铺解封/listing恢复/资金解冻\")\n" +
                "###### ￥ 2800.00-7800.00\n" +
                "_东莞市海象跨境电商有限公司_ **22个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20230613/9a5cc6b51ecca69b7adf7ee1aa290b21.jpg)](https://www.wearesellers.com/source/100354)\n" +
                "#### [雅玛森德法EPR包装法奥...](https://www.wearesellers.com/source/100354 \"雅玛森德法EPR包装法奥地利西班牙WEEE英国瑞典电池法\")\n" +
                "###### ￥ 399.00-5600.00\n" +
                "_东莞市雅玛森商务咨询有限公司_ **18个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20220822/fa633de955a2ea2da6e5c992e48c41c0.jpg)](https://www.wearesellers.com/source/100429)\n" +
                "#### [全球商标专利版权申请，美...](https://www.wearesellers.com/source/100429 \"全球商标专利版权申请，美标注册低至2399，R标低至4000\")\n" +
                "###### ￥ 1000.00-1000.00\n" +
                "_深圳市普鸥知识产权有限公司_ **17个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20220106/b8a9a942b683bc10882f51786d8e3105.jpg)](https://www.wearesellers.com/source/100022)\n" +
                "#### [卖家精灵-亚马逊大数据选...](https://www.wearesellers.com/source/100022 \"卖家精灵-亚马逊大数据选品运营工具\")\n" +
                "###### ￥ 368.00-4416.00\n" +
                "_成都云雅信息技术有限公司_ **17个评价** _4.9星_\n" +
                "[![](https://assert.wearesellers.com/article/20210707/002b9c9a71bcff970ea4fc900ec190b3.gif)](https://www.wearesellers.com/source/100369)\n" +
                "#### [Andvids→亚马逊主...](https://www.wearesellers.com/source/100369 \"Andvids→亚马逊主图视频、买家秀视频、品牌广告视频拍摄\")\n" +
                "###### ￥ 299.00-29999.00\n" +
                "_深圳嚞享跨境电商有限公司_ **16个评价** _5.0星_\n" +
                "[![](https://assert.wearesellers.com/article/20210609/4f30f18dbf395bdf2ff406c79cf1f70b.jpg)](https://www.wearesellers.com/source/100357)\n" +
                "#### [欧洲&中东VAT/墨西哥...](https://www.wearesellers.com/source/100357 \"欧洲&中东VAT/墨西哥RFC/日本JCT/欧盟IOSS注册\")\n" +
                "###### ￥ 999.00-999.00\n" +
                "_东莞市雅玛森商务咨询有限公司_ **10个评价** _5.0星_\n" +
                " \n" +
                "1、社区认证答主免费参与围观（限可公开的付费提问），在帖主选择中了答案后（即进入公示期），才可以参与围观回帖并参与回帖交流互动。即仍显示为“到期时间……”的是尚未进入公示期的，暂不能查看回帖内容。 \n" +
                " \n" +
                "2、 如果在悬赏结束后未及时看已围观的帖子内容，可以通过社区用户个人详情页列表中找到：点击右上角“个人头像”找到“围观记录”，此处有所有围观帖列表，点击进入后即可查看。 \n" +
                " \n" +
                "3、常规用户参与围观的基准费用，与有偿提问的金额大小、围观的先后次序、围观的人数有关： \n" +
                "（1） 提问奖金越高，则围观基准费用越高； \n" +
                "（2） 例如围观人数每增加10人，则围观基准费用增加0.5元。即越早参与围观，为围观支付的费用相对就越少。 \n" +
                " \n" +
                "4、围观费用10%支付给发起提问的帖主，60%纳入提问奖金并由被选中答案的答主共同分配，平台收取30%管理费用。 \n" +
                " \n" +
                "5、优秀的提问质量可以吸引更多人参与围观，以共同分摊付费发帖的费用支出。 \n" +
                " \n" +
                "6、私密悬赏帖（不公开悬赏答案的）目前仅限帖主、参与回帖互动的答主（在答案选择期前回帖的）、高活跃度威望值用户，在该帖进入公示期后可以查看该帖下的所有回帖。最佳答案选择期后参与回帖的无法查看该帖的所有回帖。私密悬赏帖结束30天以后，「知无不言」社区可以选择合适的内容通过适当的渠道进行推送。\n" +
                "本人详细阅读并充分理解和同意以下原创声明全部内容，并将予遵守执行： \n" +
                " \n" +
                "1\\. 关于“作品”： \n" +
                "1.1 是指依照《中华人民共和国著作权法》及其他相关法律法规受到法律保护且具有独创性的智力成果。 \n" +
                "1.2 作品的形式可以被「知无不言」社区识别，如文字、图片、音频或视频等。 \n" +
                "1.3 本人的“作品”在「知无不言」社区中通常是以发帖、回帖、评论的形式展现。 \n" +
                "1.4 在本声明中本人代指“个人”或以公司、协会、组织形成存在的各类“机构”，统一简称为“本人”。 \n" +
                " \n" +
                "2\\. 原创声明： \n" +
                "2.1 本人保证发布的作品由本人创作且本人享有发表权，或者获得原权利人合法、有效、完整的授权允许。 \n" +
                "2.2 如果本人所发布的作品有引用他人作品，本人将与原权利人在事前进行协商并取得授权同意，并将引用的内容在本人所发布的作品中进行来源说明。如有发生纠纷，「知无不言」社区不会也无法介入本人与授权人之间的纠纷解决。 \n" +
                " \n" +
                "3\\. 本人知悉：在如下情形出现时，不会对作品进行原创声明： \n" +
                "3.1 抄袭或剽窃的作品，或作品的构成元素（如视频作品的配音、音乐、图片）未获得合法授权导致权属争议的内容； \n" +
                "3.2 大篇幅引用他人的内容，或主要内容为他人创作，或通过整理、拼凑等形式使用他人的内容。 \n" +
                "3.3 翻译内容：没有获得作者授权的翻译内容。 \n" +
                "3.4 成果本身不是作品，或不受《中华人民共和国著作权法》保护，如法律、法规，国家机关的决议、决定、命令和其他具有立法、行政、司法性质的文件、时事新闻、历法、通用数表、通用表格和公式等； \n" +
                "3.5 营销性质的内容； \n" +
                "3.6 非独家授权或代理的作品； \n" +
                "3.7 色情低俗内容、暴力内容、不实信息等内容； \n" +
                "3.8 其他违反法律法规、政策及公序良俗、社会公德，违反「知无不言」社区《用户协议》 ，或干扰「知无不言」社区正常运营和侵犯其他用户或第三方合法权益的内容。 \n" +
                " \n" +
                "4\\. 特别声明： \n" +
                "4.1 本人理解并同意，如果发生以下情况，「知无不言」社区有权：立即暂停、中止、停止社区用户资格，并按照相关协议、规则对本人和本人发布的作品做出处理；暂停结算资金至本人的收款账户；同时，「知无不言」社区有权将本人用户账户中的资金剩余款项作为违约金或损害赔偿金予以扣除而不予返还，剩余款项不足以抵扣违约金或损害赔偿金的，「知无不言」社区有权继续向本人追偿： \n" +
                "4.1.1 本人的行为违反了「知无不言」社区《用户协议》 或相关约定； \n" +
                "4.1.2 本人发布的作品受到行政司法机关违法调查或受到第三方的侵权投诉，尚未解决； \n" +
                "4.1.3 本人未经「知无不言」社区事先书面同意，将获知的「知无不言」社区未向全部用户开放的信息告知其他任何第三方； \n" +
                "4.2 本人理解并同意：因本人主动提出或由社区提出中止答主认证时，产生的相关答主认证费用无需退还给本人。 \n" +
                "4.3 本人认可社区的悬赏规则并愿意配合执行：https://www.wearesellers.com/question/6492。 \n" +
                "4.4 本人认可依据社区管理规则因本人违反本声明或用户协议、社区指南、管理规则及公告指引等内容，导致或产生第三方主张的任何索赔、要求或损失的，由本人独立承担责任，与「知无不言」社区无关；「知无不言」社区因此遭受损失的，本人也应当一并赔偿。 \n" +
                " \n" +
                "5\\. 本声明内容同时包括「知无不言」社区可能不断发布的关于原创声明的相关声明、协议、社区指南、管理规则及公告指引等内容（统称为“原创声明”）。上述内容经正式发布后即成为本声明不可分割的组成部分。\n" +
                "在支付围观费用前，请详细查看围观规则并了解潜在的问题。 1. 帖主为该提问支付了费用，此帖付费后方可围观查看。 2. 风险提示：围观费用一经支付即不可取消或退还，可能存在悬赏帖无标准答案或理想答案的情形。 3. 围观费用将分配给帖主15%，其余的围观费用将纳入奖励金额。所围观的问题答案将会在悬赏结束后显示。 是否继续围观？\n" +
                "![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/web/16317155271740.jpg) _![](static/common/close.png)_ _倒计时：15_\n" +
                "![](static/images/newlogo.png)\n" +
                "跨境电商人的科学世界， \n" +
                "真知、灼见， \n" +
                "喜悦或快意的君子之争。 \n" +
                "见智慧，见性情。 \n" +
                "* 关于社区\n" +
                " \n" +
                " [社区用户使用指南](https://www.wearesellers.com/question/934) [如何高质量发帖](https://www.wearesellers.com/question/4975) [如何进行实名认证](https://www.wearesellers.com/question/4118) [如何成为知识社（答主）](https://www.wearesellers.com/question/461) [如何正确围观悬赏帖](https://www.wearesellers.com/question/3798) [如何入驻意见领袖专栏](https://www.wearesellers.com/question/1524)\n" +
                " \n" +
                "* 出海资源入驻\n" +
                " \n" +
                " [如何加入品牌实力展示](https://www.wearesellers.com/question/10607) [入驻步骤介绍](https://www.wearesellers.com/question/10826) [需要准备的资料](https://www.wearesellers.com/question/10827) [如何发布或修改资源信息](https://www.wearesellers.com/question/10833) [评论政策](https://www.wearesellers.com/page/reviewspolicy)\n" +
                " \n" +
                "* 法律声明\n" +
                " \n" +
                " 本站原创内容版权归作者和知无不言共同所有，未经本站许可，禁止以任何形式转载。\n" +
                " \n" +
                "* 微信公众号\n" +
                " \n" +
                " ![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/common/gzh_code.jpg)\n" +
                " \n" +
                " 微信服务号\n" +
                " \n" +
                " ![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/common/fwh_code.jpg?v=1)\n" +
                " \n" +
                " 视频号\n" +
                " \n" +
                " ![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/common/sph_code.png)\n" +
                " \n" +
                " 抖音号\n" +
                " \n" +
                " ![](https://wearesellers.oss-cn-shenzhen.aliyuncs.com/common/dy_code.jpg)\n" +
                " \n" +
                "* [用户协议](https://www.wearesellers.com/page/terms)\n" +
                "* [隐私政策](https://www.wearesellers.com/page/private)\n" +
                "* [使用指南](https://www.wearesellers.com/question/934)\n" +
                "* [广告合作](https://www.wearesellers.com/question/15238)\n" +
                "* [关于我们](https://www.wearesellers.com/page/aboutus)\n" +
                "* [联系我们](https://www.wearesellers.com/question/934)\n" +
                "* [群星计划](https://www.wearesellers.com/question/7683)\n" +
                "* [投诉中心](https://www.wearesellers.com/help/complaint_center/)\n" +
                "* [AMZ123](http://www.amz123.com/)\n" +
                "* [AMZ520](http://www.amz520.com/)\n" +
                "[深圳汤武之光技术有限公司 © 2015-2023 粤ICP备2022153043号](http://beian.miit.gov.cn) [ICP证粤B2-20230674号](http://dxzhgl.miit.gov.cn/)\n" +
                "[x](javascript:;) ![点击咨询](/static/images/kf.png)\n" +
                "[](javascript:;)";

        String query = "广告框架的搭建";


        BaseLLMResult baseLLMResult = SummarizerMixin.summaryContentCall(content, query, 500);

        log.info("out:\n{}", baseLLMResult.getText());

    }

}

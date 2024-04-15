package com.starcloud.ops.business.app.service.sensitiveword;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.service.sensitiveword.dto.JuYiResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 关键词实现类
 */
@Slf4j
@Service
public class SensitiveWordsImpl {


    /**
     * 句易网接口 API 地址（@Link <a href="http://www.ju1.cn/">句易网</a>）
     */
    private static final String JU_YI_DEFAULT_HOST = "http://www.ju1.cn";


    private static final String JU_YI_REQUEST_PATH = "/Index/add";
    /**
     * K3 达人社接口 操作 Params 地址（@Link <a href="http://foryet.net/default.html">K3 达人社</a>）
     */
    // 将分隔符和正则表达式定义为静态常量，以提高代码的可读性和可维护性
    private static final String SPLIT_DELIMITER = "<<<";

    private static final String SPAN_PATTERN_STRING = "<span.*?data-org=\"(.*?)\".*?>(.*?)</span>";

    // 将正则表达式的编译放在类级别，避免每次调用时重复编译
    private static final Pattern SPAN_PATTERN = Pattern.compile(SPAN_PATTERN_STRING);
    private static final int CONTENT_INDEX = 0;
    private static final int SENSITIVE_COUNT_INDEX = 1;
    private static final int PROHIBITED_COUNT_INDEX = 2;
    private static final int TOTAL_COUNT_INDEX = 3;
    private static final int DETAILS_INDEX = 4;


    @Resource
    private DictDataService dictDataService;


    /**
     * 判断文本中是否包含敏感词【调用 （ <a href="http://www.ju1.cn/">句易网</a>）接口】
     *
     * @param content 文本内容
     * @return 敏感词组
     */
    public String getJuYiResult(String content) {

        JuYiResultDTO juYiResultDTO = new JuYiResultDTO();

        HashMap<String, Object> formData = new HashMap<>();

        formData.put("mgtype", 1);
        formData.put("ty_wj_type", 1);
        formData.put("mz_wj_type", 1);
        formData.put("xw_wj_type", 1);
        formData.put("text", content);
        // 从字典 获取 cookie
        // fixme 待完善
        DictDataDO request = dictDataService.getDictData("request", "");

        if (request == null || request.getRemark() == null) {
            log.error("无法获取句易网请求所需的DictData");
            throw new RuntimeException("无法获取句易网请求所需的DictData");
        }

        String cookie = request.getRemark();

        log.info("开始获取关键词数据");
        try {

            HttpResponse response = HttpUtil.createPost(JU_YI_DEFAULT_HOST + JU_YI_REQUEST_PATH)
                    .form(formData)
                    .cookie(cookie)
                    .execute();
            if (!response.isOk()) {
                log.error("获取关键词失败，HTTP状态码：{}", response.getStatus());
                throw new RuntimeException("获取关键词异常，HTTP状态码：" + response.getStatus());
            }
            if (response.body().contains("跳转提示")&&response.body().contains("请登录！！")) {
                log.error("获取关键词失败，页面包含跳转提示");
                throw new RuntimeException("获取关键词异常，页面包含跳转提示");
            }
            // 解析结果
            return response.body();
        } catch (HttpException e) {
            log.error("【句易网请求获取敏感词失败，主要错误是:{}.】", e.getMessage(), e);
            throw new RuntimeException("【关键词判断异常，网络故障】");
        }
    }

    public static JuYiResultDTO convertToJson(String text) {

        if (StrUtil.isBlank(text)) {
            log.error("输入文本为空，无法进行处理。");
            throw new IllegalArgumentException("输入文本为空");
        }

        JuYiResultDTO juYiResultDTO = new JuYiResultDTO();

        // 根据符号进行数据分割
        String[] split = text.split(SPLIT_DELIMITER);

        if (split.length < 5) {
            // 处理分割后数组为空的情况
            System.out.println("输入文本未按预期格式分割。");
            return juYiResultDTO;
        }

        // 对 html 格式进行处理
        ArrayList<JuYiResultDTO.ContentDTO> contentDTOS = getContentList(split[0]);
        juYiResultDTO.setContentDTOS(contentDTOS);

        int sensitiveCount = Integer.parseInt(split[1]);
        int prohibitedCount = Integer.parseInt(split[2]);
        int totalCount = Integer.parseInt(split[3]);

        juYiResultDTO.setSensitiveCount(sensitiveCount);
        juYiResultDTO.setProhibitedCount(prohibitedCount);
        juYiResultDTO.setTotalCount(totalCount);

        List<JuYiResultDTO.DetailsDTO> detailsDTOS = BeanUtil.copyToList(JSONUtil.parseArray(split[4]), JuYiResultDTO.DetailsDTO.class);
        juYiResultDTO.setDetailsDTOS(detailsDTOS);
        return juYiResultDTO;
    }

    private static ArrayList<JuYiResultDTO.ContentDTO> getContentList(String content) {

        if (StrUtil.isBlank(content)) {
            return new ArrayList<>();
        }

        ArrayList<JuYiResultDTO.ContentDTO> contentDTOS = new ArrayList<>();

        String[] result = content.split("(?=" + SPAN_PATTERN_STRING + ")");

        for (String split : result) {
            JuYiResultDTO.ContentDTO contentDTO = new JuYiResultDTO.ContentDTO(split, null, null, false);
            if (SPAN_PATTERN.matcher(split).find()) {
                Document doc = Jsoup.parse(split);
                Elements spans = doc.select("span");
                contentDTO.setContent(spans.text());
                contentDTO.setSpell(PinyinUtil.getPinyin(contentDTO.getContent()));
                contentDTO.setStatus(true);
            }
            contentDTOS.add(contentDTO);
        }
        return contentDTOS;
    }

}

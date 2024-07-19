package com.starcloud.ops.business.other;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParserTest {


    public static void main(String[] args) {

//        String text = "{\n" +
//                "  \"data\": \"{'title': '燃情管理秘籍', 'reasons': ['高效领导力', '创新技术路', '洞见职场经', '大厂启示录', '时间管理术']}\"\n" +
//                "}";
////
////        String text = "{\n" +
////                "\t\n" +
////                "\t\"data\": \"{'title': '创业与管理精华，232323', 'ddaa': '|877111'}\"\n" +
////                "\n" +
////                "}";
//        JSON json = JSONUtil.parse(text);
//
//        log.info("json: {}", json);
//
//        JsonSchemaParser.handlerJSONValue2Str(json);
//
//        log.info("json2: {}", json);
//
//        System.out.println("Hello World");


        String sc = JsonSchemaUtils.generateJsonArraySchemaStr(OcrGeneralDTO.class);
        System.out.println(sc);

    }





}

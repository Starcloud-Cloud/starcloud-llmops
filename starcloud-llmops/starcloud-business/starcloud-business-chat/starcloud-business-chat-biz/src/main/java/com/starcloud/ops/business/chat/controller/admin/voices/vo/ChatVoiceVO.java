package com.starcloud.ops.business.chat.controller.admin.voices.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Azure 语音模型
 */
@Data
public class ChatVoiceVO implements Serializable {

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "DisplayName")
    private String displayName;

    @JsonProperty(value = "LocalName")
    private String localName;

    @JsonProperty(value = "ShortName")
    private String shortName;

    @JsonProperty(value = "Gender")
    private String gender;

    @JsonProperty(value = "Locale")
    private String locale;

    @JsonProperty(value = "LocaleName")
    private String localeName;

    @JsonProperty(value = "StyleList")
    private List<String> styleList;

    @JsonProperty(value = "SecondaryLocaleList")
    private List<String> secondaryLocaleList;

    @JsonProperty(value = "RolePlayList")
    private List<String> rolePlayList;

    @JsonProperty(value = "SampleRateHertz")
    private String sampleRateHertz;

    @JsonProperty(value = "VoiceType")
    private String voiceType;

    @JsonProperty(value = "Status")
    private String status;

    @JsonProperty(value = "ExtendedPropertyMap")
    private Map<String, Object> extendedPropertyMap;

    @JsonProperty(value = "WordsPerMinute")
    private String wordsPerMinute;


}

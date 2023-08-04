package com.starcloud.ops.business.chat.controller.admin.voices.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @JsonProperty(value = "DisplayName")
    private String displayName;

    @JsonProperty(value = "LocalName")
    private String localName;

    @JsonProperty(value = "ShortName")
    private String shortName;

    @JsonProperty(value = "Gender")
    private String gender;

    @JsonIgnore
    @JsonProperty(value = "Locale")
    private String locale;

    @JsonProperty(value = "LocaleName")
    private String localeName;

    @JsonProperty(value = "StyleList")
    private List<String> styleList;

    @JsonIgnore
    @JsonProperty(value = "SecondaryLocaleList")
    private List<String> secondaryLocaleList;

    @JsonIgnore
    @JsonProperty(value = "RolePlayList")
    private List<String> rolePlayList;

    @JsonIgnore
    @JsonProperty(value = "SampleRateHertz")
    private String sampleRateHertz;

    @JsonIgnore
    @JsonProperty(value = "VoiceType")
    private String voiceType;

    @JsonProperty(value = "Status")
    private String status;

    @JsonIgnore
    @JsonProperty(value = "ExtendedPropertyMap")
    private Map<String, Object> extendedPropertyMap;


    @JsonProperty(value = "WordsPerMinute")
    private String wordsPerMinute;


}

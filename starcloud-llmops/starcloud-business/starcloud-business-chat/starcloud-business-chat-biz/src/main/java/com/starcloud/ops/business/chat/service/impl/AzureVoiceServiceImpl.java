package com.starcloud.ops.business.chat.service.impl;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.starcloud.ops.business.app.controller.admin.chat.vo.SpeakConfigVO;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.ChatVoiceVO;
import com.starcloud.ops.business.core.config.BusinessChatProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AzureVoiceServiceImpl {

    private static final String DEF_VOICE_NAME = "zh-CN-YunyangNeural";

    private static ObjectMapper MAPPER = new ObjectMapper();

    private static List<ChatVoiceVO> allChatVoiceList = new ArrayList<>();

    private volatile static SpeechSynthesizer speechInstance;

    @Resource
    private BusinessChatProperties chatProperties;


    public void setEventCompletedConsumer(Consumer<byte[]> eventCompletedConsumer) {
        this.eventCompletedConsumer = eventCompletedConsumer;
    }

    /**
     * 回调实现
     */
    private Consumer<byte[]> eventCompletedConsumer;

    public void setEventSynthesizing(Consumer<byte[]> eventSynthesizing) {
        this.eventSynthesizing = eventSynthesizing;
    }

    private Consumer<byte[]> eventSynthesizing;

    /**
     * 选中的语音，男女各10各
     */
    private static List<String> showChatVoiceList = new ArrayList<String>() {{
        add("zh-CN-henan-YundengNeural");
        add("zh-CN-shandong-YunxiangNeural");
        add("zh-TW-YunJheNeural");
        add("wuu-CN-YunzheNeural");
        add("zh-CN-YunxiNeural");

        add("zh-CN-YunyangNeural");
//        add("zh-CN-YunhaoNeural");
        add("zh-CN-YunxiaNeural");
        add("zh-CN-YunyeNeural");

        add("zh-HK-WanLungNeural");
        add("zh-CN-sichuan-YunxiNeural");


    }};

    static {

        String json = ResourceUtil.readUtf8Str("azure/voices.json");
        JavaType jt = MAPPER.getTypeFactory().constructParametricType(ArrayList.class, ChatVoiceVO.class);

        try {
            //allChatVoiceList = JSONUtil.toList(json, ChatVoiceVO.class);
            allChatVoiceList = MAPPER.readValue(json, jt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<ChatVoiceVO> getVoiceList() {

        return allChatVoiceList;
    }

    public List<ChatVoiceVO> filterVoiceList(ChatVoiceVO chatVoice) {


        return Optional.ofNullable(allChatVoiceList).orElse(new ArrayList<>()).stream().filter(cv -> {

            return (StrUtil.isBlank(chatVoice.getShortName()) || chatVoice.getShortName().equals(cv.getShortName())) &&
                    (StrUtil.isBlank(chatVoice.getLocale()) || cv.getLocale().contains(chatVoice.getLocale())) &&
                    (StrUtil.isBlank(chatVoice.getGender()) || chatVoice.getGender().equals(cv.getGender())) &&
                    (StrUtil.isBlank(chatVoice.getStatus()) || chatVoice.getStatus().equals(cv.getStatus()));

        }).collect(Collectors.toList());

    }

    public ChatVoiceVO findVoice(String shortName) {

        return Optional.ofNullable(this.filterVoiceList(new ChatVoiceVO().setShortName(shortName))).orElse(new ArrayList<>()).stream().findFirst().orElse(null);
    }

    public List<ChatVoiceVO> findVoiceList(List<String> shortNameList) {

        shortNameList = Optional.ofNullable(shortNameList).orElse(showChatVoiceList);

        List<ChatVoiceVO> result = new ArrayList<>();

        Optional.ofNullable(shortNameList).orElse(new ArrayList<>()).forEach(s -> {

            result.add(Optional.ofNullable(allChatVoiceList).orElse(new ArrayList<>()).stream().filter(cv -> {
                return cv.getShortName().equals(s);
            }).findFirst().get());

        });

        return result;
    }

    @SneakyThrows
    public void speak(String text) {

        this.speak(text, this.findVoice(DEF_VOICE_NAME));

    }

    @SneakyThrows
    public void speak(String text, ChatVoiceVO chatVoiceVO) {

        SpeakConfigVO speakConfigVO = new SpeakConfigVO();

        speakConfigVO.setLocale(chatVoiceVO.getLocale());
        speakConfigVO.setShortName(chatVoiceVO.getShortName());

        this.speakSsml(this.createSsml(text, speakConfigVO));
    }

    @SneakyThrows
    public void speak(String text, ChatVoiceVO chatVoiceVO, SpeakConfigVO speakConfigVO) {

        speakConfigVO.setLocale(chatVoiceVO.getLocale());
        speakConfigVO.setShortName(chatVoiceVO.getShortName());

        this.speakSsml(this.createSsml(text, speakConfigVO));
    }

    @SneakyThrows
    public void speak(String text, SpeakConfigVO speakConfigVO) {

        this.speakSsml(this.createSsml(text, speakConfigVO));
    }


    public void speakSsml(String ssml) throws ExecutionException, InterruptedException {

        SpeechSynthesizer speechSynthesizer = getSpeechSynthesizer();

        speechSynthesizer.BookmarkReached.addEventListener((o, e) -> {

            log.info("BookmarkReached event:");
//            System.out.println("BookmarkReached event:");
//            System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
//            System.out.println("\tText: " + e.getText());
        });

        speechSynthesizer.SynthesisCanceled.addEventListener((o, e) -> {
            log.info("SynthesisCanceled event");

        });

        speechSynthesizer.SynthesisCompleted.addEventListener((o, e) -> {
            SpeechSynthesisResult result = e.getResult();
            byte[] audioData = result.getAudioData();

            log.info("SynthesisCompleted event, AudioData: {} bytes", audioData.length);
//            System.out.println("\tAudioData: " + audioData.length + " bytes");
//            System.out.println("\tAudioDuration: " + result.getAudioDuration());

            if (this.eventCompletedConsumer != null) {
                eventCompletedConsumer.accept(audioData);
            }

            result.close();
        });

        speechSynthesizer.SynthesisStarted.addEventListener((o, e) -> {
            log.info("SynthesisStarted event");
        });

        speechSynthesizer.Synthesizing.addEventListener((o, e) -> {
            SpeechSynthesisResult result = e.getResult();
            byte[] audioData = result.getAudioData();
            log.info("Synthesizing event, AudioData: {} bytes", audioData.length);

            if (this.eventSynthesizing != null) {
                eventSynthesizing.accept(audioData);
            }

            result.close();
        });

        speechSynthesizer.VisemeReceived.addEventListener((o, e) -> {
            log.info("VisemeReceived event:");
//            System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
//            System.out.println("\tVisemeId: " + e.getVisemeId());
        });

        speechSynthesizer.WordBoundary.addEventListener((o, e) -> {
            log.info("WordBoundary event:");
//            System.out.println("\tBoundaryType: " + e.getBoundaryType());
//            System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
//            System.out.println("\tDuration: " + e.getDuration());
//            System.out.println("\tText: " + e.getText());
//            System.out.println("\tTextOffset: " + e.getTextOffset());
//            System.out.println("\tWordLength: " + e.getWordLength());
        });

        // Synthesize the SSML
        log.info("SSML to synthesize: {}", ssml);

        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakSsmlAsync(ssml).get();

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            log.info("SynthesizingAudioCompleted result");

        } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);

            log.warn("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                log.error("CANCELED: ErrorCode={}, CANCELED: ErrorDetails=" + cancellation.getErrorCode(), cancellation.getErrorDetails());
            }
        }

        speechSynthesizer.close();

    }

    public String createSsml(String text, SpeakConfigVO speakConfigVO) {

        Assert.notBlank(speakConfigVO.getShortName(), "speakConfig shortName is required");
        Assert.notBlank(text, "speak is fail, The content cannot be empty");

        text = createProsody(text, speakConfigVO);
        text = createRole(text, speakConfigVO);

        String ssml = "<speak version=\"1.0\" xmlns=\"https://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"https://www.w3.org/2001/mstts\" xml:lang=\"" + speakConfigVO.getLocale() + "\">\n" +
                "<voice name=\"" + speakConfigVO.getShortName() + "\">\n " +
                text +
                "\n</voice>\n" +
                "</speak>";

        return ssml;
    }

    private String createProsody(String text, SpeakConfigVO speakConfigVO) {

        List<String> prosodyList = new ArrayList<>();

        if (StrUtil.isNotBlank(speakConfigVO.getProsodyRate())) {
            prosodyList.add("rate=\"" + speakConfigVO.getProsodyRate() + "\"");
        }

        if (StrUtil.isNotBlank(speakConfigVO.getProsodyVolume())) {
            prosodyList.add("volume=\"" + speakConfigVO.getProsodyVolume() + "\"");
        }

        if (StrUtil.isNotBlank(speakConfigVO.getProsodyPitch())) {
            prosodyList.add("pitch=\"" + speakConfigVO.getProsodyPitch() + "\"");
        }

        if (prosodyList.size() > 0) {

            return "<prosody " + StrUtil.join(" ", prosodyList) + ">\n " +
                    text +
                    "\n</prosody>\n";
        } else {
            return text;
        }

    }

    private String createRole(String text, SpeakConfigVO speakConfigVO) {

        List<String> prosodyList = new ArrayList<>();

        if (StrUtil.isNotBlank(speakConfigVO.getRole())) {
            prosodyList.add("role=\"" + speakConfigVO.getRole() + "\"");
        }

        if (StrUtil.isNotBlank(speakConfigVO.getStyle())) {
            prosodyList.add("style=\"" + speakConfigVO.getStyle() + "\"");
        }

        if (prosodyList.size() > 0) {

            return "<mstts:express-as " + StrUtil.join(" ", prosodyList) + ">\n " +
                    text +
                    "\n</mstts:express-as>\n";
        } else {
            return text;
        }

    }

    private synchronized SpeechSynthesizer getSpeechSynthesizer() {

        if (speechInstance == null) {

            SpeechConfig speechConfig = SpeechConfig.fromSubscription(chatProperties.getSpeechSubscriptionKey(), chatProperties.getSpeechRegion());
            // Set either the `SpeechSynthesisVoiceName` or `SpeechSynthesisLanguage`.

            speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz32KBitRateMonoMp3);
            // Required for WordBoundary event sentences.
            //speechConfig.setProperty(PropertyId.SpeechServiceResponse_RequestSentenceBoundary, "true");

            //AudioConfig audioConfig = AudioConfig.fromWavFileOutput("/tmp/file123.wav");

            SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

//            Connection connection = Connection.fromSpeechSynthesizer(speechSynthesizer);
//            connection.openConnection(true);

            speechInstance =  speechSynthesizer;
        }

        return speechInstance;
    }
}

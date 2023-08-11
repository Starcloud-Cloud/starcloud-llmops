package com.starcloud.ops.business.chat;


import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.app.controller.admin.chat.vo.SpeakConfigVO;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.ChatVoiceVO;
import com.starcloud.ops.business.chat.service.impl.AzureVoiceServiceImpl;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class ChatVoiceTest extends BaseDbUnitTest {

    @Autowired
    private AzureVoiceServiceImpl azureVoiceService;


    @Test
    public void getVoiceListTest() {

        List<ChatVoiceVO> chatVoiceVOS = azureVoiceService.filterVoiceList(new ChatVoiceVO().setLocale("zh-"));

        log.info("chatVoiceVOS: {}", chatVoiceVOS.size());

    }

    @Test
    public void findVoiceListTest() {

        List<ChatVoiceVO> chatVoiceVOS = azureVoiceService.findVoiceList(null);

        log.info("chatVoiceVOS: {}", chatVoiceVOS.size());

    }


    @Test
    public void demoTest() {

        azureVoiceService.speak("曹小宝跟我说，要买爱马仕。我现在加班苦钱，不要吵我！！");
    }


    @Test
    public void SpeechSynthesizerTest() {


        azureVoiceService.speak("曹小宝跟我说，要买爱马仕。我现在加班苦钱，不要吵我！！", azureVoiceService.findVoice("zh-CN-sichuan-YunxiNeural"));
    }


    @Test
    public void SpeechRolePlayTest() {

        SpeakConfigVO speakConfigVO = new SpeakConfigVO();

        speakConfigVO.setProsodyRate("-20%");
        //speakConfigVO.setProsodyVolume("-70%");
        //speakConfigVO.setProsodyPitch("20%");

        //speakConfigVO.setRole("Boy");

        azureVoiceService.speak("曹小宝跟我说，要买爱马仕。我现在加班苦钱，不要吵我！！", azureVoiceService.findVoice("zh-CN-YunxiNeural"), speakConfigVO);
    }

}

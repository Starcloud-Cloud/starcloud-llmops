package com.starcloud.ops.business.chat;


import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.starcloud.ops.business.core.config.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutionException;

public class AzureTest {


    @Test
    public void demoTest() {

        SpeechConfig speechConfig = SpeechConfig.fromSubscription("", "");
        // Set either the `SpeechSynthesisVoiceName` or `SpeechSynthesisLanguage`.
        speechConfig.setSpeechSynthesisLanguage("en-US");
        speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");

        AudioConfig audioConfig = AudioConfig.fromWavFileOutput("/tmp/file123.wav");


        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, audioConfig);

        //speechSynthesizer.SpeakText("I'm excited to try text to speech");

        speechSynthesizer.SpeakSsml("<speak version=\"1.0\" xmlns=\"https://www.w3.org/2001/10/synthesis\" xml:lang=\"en-US\">\n" +
                "  <voice name=\"zh-CN-liaoning-XiaobeiNeural\">\n" +
                "    曹小宝正在看小红书，气死了，气死啦，气死啦气死啦，死死死死死，自杀\n" +
                "  </voice>\n" +
                "</speak>");

    }


    @Test
    public void SpeechSynthesizerTest() throws ExecutionException, InterruptedException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription("", "");
        // Set either the `SpeechSynthesisVoiceName` or `SpeechSynthesisLanguage`.
        speechConfig.setSpeechSynthesisLanguage("en-US");
        speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");

        // Required for WordBoundary event sentences.
        speechConfig.setProperty(PropertyId.SpeechServiceResponse_RequestSentenceBoundary, "true");


        String ssml = "<speak version=\"1.0\" xmlns=\"https://www.w3.org/2001/10/synthesis\" xml:lang=\"en-US\">\n" +
                "  <voice name=\"zh-CN-liaoning-XiaobeiNeural\">\n" +
                "    曹小宝正在看小红书，气死了，气死啦，气死啦气死啦，死死死死死，自杀\n" +
                "  </voice>\n" +
                "</speak>";

        //AudioConfig audioConfig = AudioConfig.fromWavFileOutput("/tmp/file123.wav");


        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

        speechSynthesizer.BookmarkReached.addEventListener((o, e) -> {
            System.out.println("BookmarkReached event:");
            System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
            System.out.println("\tText: " + e.getText());
        });

        speechSynthesizer.SynthesisCanceled.addEventListener((o, e) -> {
            System.out.println("SynthesisCanceled event");
        });

        speechSynthesizer.SynthesisCompleted.addEventListener((o, e) -> {
            SpeechSynthesisResult result = e.getResult();
            byte[] audioData = result.getAudioData();
            System.out.println("SynthesisCompleted event:");
            System.out.println("\tAudioData: " + audioData.length + " bytes");
            System.out.println("\tAudioDuration: " + result.getAudioDuration());
            result.close();
        });

        speechSynthesizer.SynthesisStarted.addEventListener((o, e) -> {
            System.out.println("SynthesisStarted event");
        });

        speechSynthesizer.Synthesizing.addEventListener((o, e) -> {
            SpeechSynthesisResult result = e.getResult();

            e.getResult().getAudioDuration();

            byte[] audioData = result.getAudioData();
            System.out.println("Synthesizing event:");
            System.out.println("\tAudioData: " + audioData.length + " bytes");
            result.close();
        });

        speechSynthesizer.VisemeReceived.addEventListener((o, e) -> {
            System.out.println("VisemeReceived event:");
            System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
            System.out.println("\tVisemeId: " + e.getVisemeId());
        });

        speechSynthesizer.WordBoundary.addEventListener((o, e) -> {
            System.out.println("WordBoundary event:");
            System.out.println("\tBoundaryType: " + e.getBoundaryType());
            System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
            System.out.println("\tDuration: " + e.getDuration());
            System.out.println("\tText: " + e.getText());
            System.out.println("\tTextOffset: " + e.getTextOffset());
            System.out.println("\tWordLength: " + e.getWordLength());
        });

        // Synthesize the SSML
        System.out.println("SSML to synthesize:");
        System.out.println(ssml);
        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakSsmlAsync(ssml).get();

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            System.out.println("SynthesizingAudioCompleted result");
        } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you set the speech resource key and region values?");
            }
        }

        speechSynthesizer.close();

        System.exit(0);

    }

}

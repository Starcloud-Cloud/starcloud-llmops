//package com.starcloud.ops.workflow;
//
//import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
//import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
//import cn.kstry.framework.core.engine.StoryEngine;
//import cn.kstry.framework.core.engine.facade.ReqBuilder;
//import cn.kstry.framework.core.engine.facade.StoryRequest;
//import cn.kstry.framework.core.engine.facade.TaskResponse;
//import cn.kstry.framework.core.enums.TrackingTypeEnum;
//import com.starcloud.ops.business.app.domain.entity.AppEntity;
//import com.starcloud.ops.business.app.domain.factory.AppFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//
//@Slf4j
//@Import({AdapterRuoyiProConfiguration.class})
//public class DemoTest extends BaseDbUnitTest {
//
//    @Autowired
//    private StoryEngine storyEngine;
//
//
//    @Test
//    public void demoTest() {
//
//        log.info("hahahahhaha");
//    }
//
//    @Test
//    public void testRunTest() {
//
//        AppEntity app = AppFactory.factory("xxx");
//
//        StoryRequest<Void> req = ReqBuilder.returnType(Void.class).timeout(3000).startId(app.getUniqueName())
//                .trackingType(TrackingTypeEnum.ALL)
//                .request(app).build();
//
//        TaskResponse<Void> fire = storyEngine.fire(req);
//
//        log.info("{}, {}, {}, {}", fire.isSuccess(), fire.getResultCode(), fire.getResultDesc(), fire.getResult());
//
//    }
//}

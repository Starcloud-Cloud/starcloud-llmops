package com.starcloud.ops.workflow;

import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@Slf4j
@SpringBootTest
public class DemoTest {

    @Resource
    private StoryEngine storyEngine;

    @Test
    public void testRun() {

        AppEntity app = AppFactory.factory("xxx");

        StoryRequest<Void> req = ReqBuilder.returnType(Void.class).timeout(3000).startId(app.getUniqueName())
                .trackingType(TrackingTypeEnum.ALL)
                .request(app).build();

        TaskResponse<Void> fire = storyEngine.fire(req);

        log.info("{}, {}, {}, {}", fire.isSuccess(), fire.getResultCode(), fire.getResultDesc(), fire.getResult());

    }
}

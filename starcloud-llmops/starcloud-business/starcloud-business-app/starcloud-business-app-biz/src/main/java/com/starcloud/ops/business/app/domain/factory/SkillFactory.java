package com.starcloud.ops.business.app.domain.factory;

import com.starcloud.ops.business.app.domain.entity.skill.ActionSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.SkillEntity;
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import lombok.SneakyThrows;

public class SkillFactory {


    @SneakyThrows
    public static <T extends SkillEntity> T factory(Class<T> cls) {

        T skillEntity = cls.getDeclaredConstructor().newInstance();


        return skillEntity;
    }

    public static SkillEntity factory(String skillUid) {
        return null;
    }


    public static AppWorkflowSkillEntity factoryAppWorkflow(String appUid) {

        AppWorkflowSkillEntity appWorkflowSkillEntity = new AppWorkflowSkillEntity();

        appWorkflowSkillEntity.setAppUid(appUid);
        appWorkflowSkillEntity.setName("workflow-run");
        appWorkflowSkillEntity.setDesc("workflow run command");


        return appWorkflowSkillEntity;
    }

    public static ActionSkillEntity factoryAction(Class<? extends FlowStepHandler> actionCls) {

        ActionSkillEntity actionSkillEntity = new ActionSkillEntity();

        actionSkillEntity.setActionCls(actionCls);

        return actionSkillEntity;
    }

    public static SkillEntity factoryCode(String skillCode) {


        return null;
    }

}

package com.starcloud.ops.business.app.util;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public class UserRightSceneUtils {

    /**
     * 根据场景获取用户权益的业务类型
     *
     * @param scene 场景
     * @return 业务类型
     */
    public static AdminUserRightsBizTypeEnum getUserRightsBizType(String scene) {
        if (StringUtils.isBlank(scene)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_SCENE_REQUIRED);
        }
        AppSceneEnum appSceneEnum = AppSceneEnum.valueOf(scene);
        switch (appSceneEnum) {
            case WEB_ADMIN: // 1
                return AdminUserRightsBizTypeEnum.WEB_ADMIN_SCENE;
            case CHAT_TEST: // 2
                return AdminUserRightsBizTypeEnum.CHAT_TEST_SCENE;
            case WEB_MARKET: // 3
                return AdminUserRightsBizTypeEnum.WEB_MARKET_SCENE;
            case CHAT_MARKET: // 4
                return AdminUserRightsBizTypeEnum.CHAT_MARKET_SCENE;
            case WEB_IMAGE: // 5
                return AdminUserRightsBizTypeEnum.WEB_IMAGE_SCENE;
            case IMAGE_UPSCALING: // 6
                return AdminUserRightsBizTypeEnum.IMAGE_UPSCALING_SCENE;
            case IMAGE_REMOVE_BACKGROUND: // 7
                return AdminUserRightsBizTypeEnum.IMAGE_REMOVE_BACKGROUND_SCENE;
            case IMAGE_REPLACE_BACKGROUND: // 8
                return AdminUserRightsBizTypeEnum.IMAGE_REPLACE_BACKGROUND_SCENE;
            case IMAGE_REMOVE_TEXT: // 9
                return AdminUserRightsBizTypeEnum.IMAGE_REMOVE_TEXT_SCENE;
            case IMAGE_SKETCH: // 10
                return AdminUserRightsBizTypeEnum.IMAGE_SKETCH_SCENE;
            case IMAGE_VARIANTS: // 11
                return AdminUserRightsBizTypeEnum.IMAGE_VARIANTS_SCENE;
            case OPTIMIZE_PROMPT: // 12
                return AdminUserRightsBizTypeEnum.OPTIMIZE_PROMPT_SCENE;
            case LISTING_GENERATE: // 13
                return AdminUserRightsBizTypeEnum.LISTING_GENERATE_SCENE;
            case XHS_WRITING: // 14
                return AdminUserRightsBizTypeEnum.XHS_WRITING_SCENE;
            case SHARE_WEB: // 15
                return AdminUserRightsBizTypeEnum.SHARE_WEB_SCENE;
            case SHARE_IFRAME: // 16
                return AdminUserRightsBizTypeEnum.SHARE_IFRAME_SCENE;
            case SHARE_JS: // 17
                return AdminUserRightsBizTypeEnum.SHARE_JS_SCENE;
            case SHARE_API: // 18
                return AdminUserRightsBizTypeEnum.SHARE_API_SCENE;
            case WECOM_GROUP: // 19
                return AdminUserRightsBizTypeEnum.WECOM_GROUP_SCENE;
            case MP: // 18
                return AdminUserRightsBizTypeEnum.MP_SCENE;
            case APP_TEST: // 19
                return AdminUserRightsBizTypeEnum.APP_TEST_SCENE;
            default:
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_SCENE_REQUIRED);
        }
    }
}

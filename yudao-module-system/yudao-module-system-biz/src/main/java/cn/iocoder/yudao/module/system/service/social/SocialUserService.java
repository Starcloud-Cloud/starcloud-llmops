package cn.iocoder.yudao.module.system.service.social;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserRespVO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserUpdateSimpleReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 社交用户 Service 接口，例如说社交平台的授权登录
 *
 * @author 芋道源码
 */
public interface SocialUserService {

    /**
     * 获得指定用户的社交用户列表
     *
     * @param userId   用户编号
     * @param userType 用户类型
     * @return 社交用户列表
     */
    List<SocialUserDO> getSocialUserList(Long userId, Integer userType);

    /**
     * 绑定社交用户
     *
     * @param reqDTO 绑定信息
     * @return 社交用户 openid
     */
    String bindSocialUser(@Valid SocialUserBindReqDTO reqDTO);

    /**
     * 取消绑定社交用户
     *
     * @param userId     用户编号
     * @param userType   全局用户类型
     * @param socialType 社交平台的类型 {@link SocialTypeEnum}
     * @param openid     社交平台的 openid
     */
    void unbindSocialUser(Long userId, Integer userType, Integer socialType, String openid);

    /**
     * 获得绑定用户
     *
     * @param openId
     * @return
     */
    AdminUserDO getSocialUser(String openId, Integer socialUserType, Integer userType);

    /**
     * 获得社交用户
     * <p>
     * 在认证信息不正确的情况下，也会抛出 {@link ServiceException} 业务异常
     *
     * @param userType   用户类型
     * @param socialType 社交平台的类型
     * @param code       授权码
     * @param state      state
     * @return 社交用户
     */
    SocialUserRespDTO getSocialUser(Integer userType, Integer socialType, String code, String state);

    // ==================== 社交用户 CRUD ====================

    /**
     * 获得社交用户
     *
     * @param id 编号
     * @return 社交用户
     */
    SocialUserDO getSocialUser(Long id);


    /**
     * 获得社交用户
     *
     * @param id 编号
     * @return 社交用户
     */
    SocialUserDO getNewSocialUser(Long id);

    /**
     * 获得社交用户分页
     *
     * @param pageReqVO 分页查询
     * @return 社交用户分页
     */
    PageResult<SocialUserDO> getSocialUserPage(SocialUserPageReqVO pageReqVO);

    /**
     * 绑定公共号用户
     *
     * @param socialUserDO
     * @param socialUserBindDO
     */
    void bindWechatUser(SocialUserDO socialUserDO, SocialUserBindDO socialUserBindDO);

    /**
     * 获得指定用户的社交用户
     *
     * @param userId
     * @param userType
     * @return
     */
    SocialUserDO getSocialUser(Long userId, Integer userType);

    /**
     * 获得指定用户的社交用户
     *
     * @param userId     用户编号
     * @param userType   用户类型
     * @param socialType 社交平台的类型
     * @return 社交用户
     */
    SocialUserDO getSocialUser(Long userId, Integer userType, Integer socialType);

    /**
     * id查询绑定用户
     */
    Map<String, SocialUserDO> getSocialUser(List<String> socialId);


    /**
     *更新社交昵称
     */
    void updateNickname(SocialUserUpdateSimpleReqVO simpleReqVO);


    PageResult<SocialUserRespVO> getSocialUserPage2(SocialUserPageReqVO pageReqVO);


}

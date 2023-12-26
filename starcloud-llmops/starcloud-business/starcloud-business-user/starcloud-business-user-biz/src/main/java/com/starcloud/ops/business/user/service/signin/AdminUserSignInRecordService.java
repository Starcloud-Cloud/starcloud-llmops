package com.starcloud.ops.business.user.service.signin;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AdminUserSignInRecordPageReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AppAdminUserSignInRecordSummaryRespVO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInRecordDO;

/**
 * 签到记录 Service 接口
 *
 * @author 芋道源码
 */
public interface AdminUserSignInRecordService {

    /**
     * 【管理员】获得签到记录分页
     *
     * @param pageReqVO 分页查询
     * @return 签到记录分页
     */
    PageResult<AdminUserSignInRecordDO> getSignInRecordPage(AdminUserSignInRecordPageReqVO pageReqVO);

    /**
     * 【会员】获得签到记录分页
     *
     * @param userId    用户编号
     * @param pageParam 分页查询
     * @return 签到记录分页
     */
    PageResult<AdminUserSignInRecordDO> getSignRecordPage(Long userId, PageParam pageParam);

    /**
     * 创建签到记录
     *
     * @param userId 用户编号
     * @return 签到记录
     */
    AdminUserSignInRecordDO createSignRecord(Long userId);

    /**
     * 根据用户编号，获得个人签到统计信息
     *
     * @param userId 用户编号
     * @return 个人签到统计信息
     */
    AppAdminUserSignInRecordSummaryRespVO getSignInRecordSummary(Long userId);


}

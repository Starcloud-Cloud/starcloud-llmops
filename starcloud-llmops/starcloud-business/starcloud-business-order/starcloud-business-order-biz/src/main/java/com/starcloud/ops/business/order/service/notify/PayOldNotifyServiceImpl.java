package com.starcloud.ops.business.order.service.notify;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.starcloud.ops.business.order.api.notify.dto.PayOrderNotifyReqDTO;
import com.starcloud.ops.business.order.api.notify.dto.PayRefundNotifyReqDTO;
import com.starcloud.ops.business.order.dal.dataobject.notify.PayNotifyLogDO;
import com.starcloud.ops.business.order.dal.dataobject.notify.PayNotifyTaskDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.refund.PayRefundDO;
import com.starcloud.ops.business.order.dal.mysql.notify.PayOldNotifyLogMapper;
import com.starcloud.ops.business.order.dal.mysql.notify.PayOldNotifyTaskMapper;
import com.starcloud.ops.business.order.dal.redis.notify.PayOldNotifyLockRedisDAO;
import com.starcloud.ops.business.order.enums.notify.PayNotifyStatusEnum;
import com.starcloud.ops.business.order.enums.notify.PayNotifyTypeEnum;
import com.starcloud.ops.business.order.service.notify.dto.PayNotifyTaskCreateReqDTO;
import com.starcloud.ops.business.order.service.order.PayOrderService;
import com.starcloud.ops.business.order.service.refund.PayRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.addTime;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.afterNow;

/**
 * 支付通知 Core Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Valid
@Slf4j
public class PayOldNotifyServiceImpl implements PayNotifyService {

    /**
     * 通知超时时间，单位：秒
     */
    public static final int NOTIFY_TIMEOUT = 120;
    /**
     * {@link #NOTIFY_TIMEOUT} 的毫秒
     */
    public static final long NOTIFY_TIMEOUT_MILLIS = 120 * DateUtils.SECOND_MILLIS;
    private static final String NOTIFY_THREAD_POOL_TASK_EXECUTOR = "`";

    @Resource
    @Lazy // 循环依赖，避免报错
    private PayOrderService orderService;
    @Resource
    @Lazy // 循环依赖，避免报错
    private PayRefundService refundService;

    @Resource
    private PayOldNotifyTaskMapper payOldNotifyTaskMapper;
    @Resource
    private PayOldNotifyLogMapper payOldNotifyLogMapper;

//    @Resource(name = NOTIFY_THREAD_POOL_TASK_EXECUTOR)
//    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private PayOldNotifyLockRedisDAO payNotifyLockCoreRedisDAO;

    @Resource
    @Lazy // 循环依赖（自己依赖自己），避免报错
    private PayOldNotifyServiceImpl self;

    @Override
    public void createPayNotifyTask(PayNotifyTaskCreateReqDTO reqDTO) {
        PayNotifyTaskDO task = new PayNotifyTaskDO();
        task.setType(reqDTO.getType()).setDataId(reqDTO.getDataId());
        task.setStatus(PayNotifyStatusEnum.WAITING.getStatus()).setNextNotifyTime(LocalDateTime.now())
                .setNotifyTimes(0).setMaxNotifyTimes(PayNotifyTaskDO.NOTIFY_FREQUENCY.length + 1);
        // 补充 merchantId + appId + notifyUrl 字段
        if (Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())) {
            PayOrderDO order = orderService.getOrder(task.getDataId()); // 不进行非空判断，有问题直接异常
            task.setMerchantId(order.getMerchantId()).setAppId(order.getAppId()).
                    setMerchantOrderId(order.getMerchantOrderId()).setNotifyUrl(order.getNotifyUrl());
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())) {
            PayRefundDO refundDO = refundService.getRefund(task.getDataId());
            task.setMerchantId(refundDO.getMerchantId()).setAppId(refundDO.getAppId())
                    .setMerchantOrderId(refundDO.getMerchantOrderId()).setNotifyUrl(refundDO.getNotifyUrl());
        }

        // 执行插入
        payOldNotifyTaskMapper.insert(task);

        // 异步直接发起任务。虽然会有定时任务扫描，但是会导致延迟
        self.executeNotifyAsync(task);
    }

    @Override
    public int executeNotify() throws InterruptedException {
        // 获得需要通知的任务
        List<PayNotifyTaskDO> tasks = payOldNotifyTaskMapper.selectListByNotify();
        if (CollUtil.isEmpty(tasks)) {
            return 0;
        }

        // 遍历，逐个通知
        CountDownLatch latch = new CountDownLatch(tasks.size());
//        tasks.forEach(task -> threadPoolTaskExecutor.execute(() -> {
//            try {
//                executeNotifySync(task);
//            } finally {
//                latch.countDown();
//            }
//        }));
        // 等待完成
        awaitExecuteNotify(latch);
        // 返回执行完成的任务数（成功 + 失败)
        return tasks.size();
    }

    /**
     * 等待全部支付通知的完成
     * 每 1 秒会打印一次剩余任务数量
     *
     * @param latch Latch
     * @throws InterruptedException 如果被打断
     */
    private void awaitExecuteNotify(CountDownLatch latch) throws InterruptedException {
        long size = latch.getCount();
        for (int i = 0; i < NOTIFY_TIMEOUT; i++) {
            if (latch.await(1L, TimeUnit.SECONDS)) {
                return;
            }
            // log.info("[awaitExecuteNotify][任务处理中， 总任务数({}) 剩余任务数({})]", size, latch.getCount());
        }
        // log.error("[awaitExecuteNotify][任务未处理完，总任务数({}) 剩余任务数({})]", size, latch.getCount());
    }

    /**
     * 异步执行单个支付通知
     *
     * @param task 通知任务
     */
    @Async
    public void executeNotifyAsync(PayNotifyTaskDO task) {
        self.executeNotifySync(task); // 使用 self，避免事务不发起
    }

    /**
     * 同步执行单个支付通知
     *
     * @param task 通知任务
     */
    public void executeNotifySync(PayNotifyTaskDO task) {
        // 分布式锁，避免并发问题
        payNotifyLockCoreRedisDAO.lock(task.getId(), NOTIFY_TIMEOUT_MILLIS, () -> {
            // 校验，当前任务是否已经被通知过
            // 虽然已经通过分布式加锁，但是可能同时满足通知的条件，然后都去获得锁。此时，第一个执行完后，第二个还是能拿到锁，然后会再执行一次。
            PayNotifyTaskDO dbTask = payOldNotifyTaskMapper.selectById(task.getId());
            if (afterNow(dbTask.getNextNotifyTime())) {
                log.info("[executeNotifySync][dbTask({}) 任务被忽略，原因是未到达下次通知时间，可能是因为并发执行了]",
                        JsonUtils.toJsonString(dbTask));
                return;
            }

            // 执行通知
            self.executeNotify(dbTask);
        });
    }

    @Transactional
    public void executeNotify(PayNotifyTaskDO task) {
        // 发起回调
        CommonResult<?> invokeResult = null;
        Throwable invokeException = null;
        try {
            invokeResult = executeNotifyInvoke(task);
        } catch (Throwable e) {
            invokeException = e;
        }

        // 处理结果
        Integer newStatus = processNotifyResult(task, invokeResult, invokeException);

        // 记录 PayNotifyLog 日志
        String response = invokeException != null ? ExceptionUtil.getRootCauseMessage(invokeException) :
                JsonUtils.toJsonString(invokeResult);
        payOldNotifyLogMapper.insert(PayNotifyLogDO.builder().taskId(task.getId())
                .notifyTimes(task.getNotifyTimes() + 1).status(newStatus).response(response).build());
    }

    /**
     * 执行单个支付任务的 HTTP 调用
     *
     * @param task 通知任务
     * @return HTTP 响应
     */
    private CommonResult<?> executeNotifyInvoke(PayNotifyTaskDO task) {
        // 拼接 body 参数
        Object request;
        if (Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())) {
            request = PayOrderNotifyReqDTO.builder().merchantOrderId(task.getMerchantOrderId())
                    .payOrderId(task.getDataId()).build();
        } else if (Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())) {
            request = PayRefundNotifyReqDTO.builder().merchantOrderId(task.getMerchantOrderId())
                    .payRefundId(task.getDataId()).build();
        } else {
            throw new RuntimeException("未知的通知任务类型：" + JsonUtils.toJsonString(task));
        }
        // 拼接 header 参数
        Map<String, String> headers = new HashMap<>();
        TenantUtils.addTenantHeader(headers, TenantContextHolder.getTenantId());

        // 发起请求
        try (HttpResponse response = HttpUtil.createPost(task.getNotifyUrl())
                .body(JsonUtils.toJsonString(request)).addHeaders(headers)
                .timeout((int) NOTIFY_TIMEOUT_MILLIS).execute()) {
            // 解析结果
            return JsonUtils.parseObject(response.body(), CommonResult.class);
        }
    }

    /**
     * 处理并更新通知结果
     *
     * @param task 通知任务
     * @param invokeResult 通知结果
     * @param invokeException 通知异常
     * @return 最终任务的状态
     */
    private Integer processNotifyResult(PayNotifyTaskDO task, CommonResult<?> invokeResult, Throwable invokeException) {
        // 设置通用的更新 PayNotifyTaskDO 的字段
        PayNotifyTaskDO updateTask = new PayNotifyTaskDO()
                .setId(task.getId())
                .setLastExecuteTime(LocalDateTime.now())
                .setNotifyTimes(task.getNotifyTimes() + 1);

        // 情况一：调用成功
        if (invokeResult != null && invokeResult.isSuccess()) {
            updateTask.setStatus(PayNotifyStatusEnum.SUCCESS.getStatus());
            payOldNotifyTaskMapper.updateById(updateTask);
            return updateTask.getStatus();
        }
        // 情况二：调用失败、调用异常
        // 2.1 超过最大回调次数
        if (updateTask.getNotifyTimes() >= PayNotifyTaskDO.NOTIFY_FREQUENCY.length) {
            updateTask.setStatus(PayNotifyStatusEnum.FAILURE.getStatus());
            payOldNotifyTaskMapper.updateById(updateTask);
            return updateTask.getStatus();
        }
        // 2.2 未超过最大回调次数
        updateTask.setNextNotifyTime(addTime(Duration.ofSeconds(PayNotifyTaskDO.NOTIFY_FREQUENCY[updateTask.getNotifyTimes()])));
        updateTask.setStatus(invokeException != null ? PayNotifyStatusEnum.REQUEST_FAILURE.getStatus()
                : PayNotifyStatusEnum.REQUEST_SUCCESS.getStatus());
        payOldNotifyTaskMapper.updateById(updateTask);
        return updateTask.getStatus();
    }

}

// package com.starcloud.ops.business.user.framework.rights.core.rightslimit;
//
// import lombok.RequiredArgsConstructor;
// import lombok.SneakyThrows;
// import lombok.extern.slf4j.Slf4j;
// import org.aspectj.lang.JoinPoint;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.stereotype.Component;
//
// import javax.annotation.Resource;
// import javax.servlet.http.HttpServletRequest;
//
// import java.util.concurrent.TimeUnit;
//
// import static com.starcloud.ops.business.app.domain.entity.BaseAppEntity.exception;
//
//
// /**
//  * @author Yang
//  * @date 2023/1/29
//  */
// @Aspect
// @Component
// @Slf4j
// @RequiredArgsConstructor
// public class RightsLimitAspect {
//
//
// 	@Resource
// 	private StringRedisTemplate stringRedisTemplate;
//
// 	/**
// 	 * 接口请求频率限制切面逻辑
// 	 *
// 	 * @param joinPoint  连接点
// 	 * @param visitLimit 访问限制
// 	 * @return {@link Object}
// 	 */
// 	@SneakyThrows
// 	@Around("@annotation(RightsLimit)")
// 	public Object around(ProceedingJoinPoint joinPoint, RightsLimit visitLimit) {
// 		// 获取用户等级信息
// 		// 获取用户当前权益最大的值
// 		HttpServletRequest request = WebExtendUtil.getRequest();
// 		String ipAddress = WebExtendUtil.getIp(request);
// 		String value = visitLimit.value();
// 		// 过期时间和最大限制次数
// 		long timeOut = visitLimit.timeOut();
// 		int limitNumber = visitLimit.number();
// 		String notice = visitLimit.info();
// 		log.info("当前用户{}，正在验证{}", ipAddress, value);
// 		String key = this.getKeyName(joinPoint, visitLimit, ipAddress);
// 		long count = this.redisTemplate.opsForValue().increment(key, 1);
// 		if (count == 1) {
// 			redisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
// 		}
// 		if (count > limitNumber) {
// 			log.info("用户IP:{},请求资源:{},超过了限定的次数：{}", ipAddress, value, limitNumber);
// 			// throw exception(notice);
// 		}
// 		// 继续执行方法，并获取方法返回值
// 		return joinPoint.proceed();
// 	}
//
// 	/**
// 	 * 获取redis键名称
// 	 *
// 	 * @param joinPoint  连接点
// 	 * @param visitLimit 访问限制
// 	 * @param ipAddress  ip地址
// 	 * @return {@link String}
// 	 */
// 	private String getKeyName(JoinPoint joinPoint, RightsLimit visitLimit, String ipAddress) {
// 		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
// 		Method method = signature.getMethod();
// 		String className = method.getDeclaringClass().getSimpleName();
// 		String methodName = method.getName();
// 		return "req_limit_".concat(className).concat("_").concat(methodName).concat("_").concat(visitLimit.value()).concat(ipAddress);
// 	}
// }

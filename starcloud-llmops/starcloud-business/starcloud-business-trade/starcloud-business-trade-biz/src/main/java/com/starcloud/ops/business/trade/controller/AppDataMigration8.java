package com.starcloud.ops.business.trade.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.enums.order.TradeOrderStatusEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 旧数据迁移
 *
 * @author AlanCusack
 */
public class AppDataMigration8 {

    private static final String SOURCE_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/cn-pro-llmops?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String SOURCE_USERNAME = "llmops";
    private static final String SOURCE_PASSWORD = "Hellostarcloud2022";

    private static final String TARGET_URL = "jdbc:mysql://rm-uf665hm6p41e4tuk89o.mysql.rds.aliyuncs.com:3306/cn-pro-llmops?useSSL=false&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true";
    private static final String TARGET_USERNAME = "llmops";
    private static final String TARGET_PASSWORD = "Hellostarcloud2022";
    private static final List<String> levelList = Arrays.asList("5", "6", "7", "8", "48", "49", "55", "53", "54");


    public static void main(String[] args) throws SQLException {


        // 1.链接源数据库
        Connection sourceConnection = DriverManager.getConnection(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        sourceConnection.setAutoCommit(false);
        sourceConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // 1.1 查询源数据库的数据
        ResultSet resultSet = sourceConnection
                .prepareStatement("SELECT * FROM pay_order;")
                .executeQuery();
        // 解析数据
        List<TradeOrderDO> tradeOrderDOS = new LinkedList<>();
        System.out.println("================开始解析数据=====================");
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String userId = resultSet.getString("creator");
            String userIp = resultSet.getString("user_ip");
            Integer orderStatus = resultSet.getInt("status");
            Timestamp successTime = resultSet.getTimestamp("success_time");
            Integer amount = resultSet.getInt("amount");
            Timestamp createTime = resultSet.getTimestamp("create_time");
            Timestamp updateTime = resultSet.getTimestamp("update_time");

            //  1.0 设置用户权益数据
            TradeOrderDO tradeOrderDO = new TradeOrderDO();
            tradeOrderDO.setNo("TO"+IdUtil.getSnowflakeNextId());

            tradeOrderDO.setType(0);
            tradeOrderDO.setTerminal(20);
            tradeOrderDO.setUserId(Long.valueOf(userId));
            tradeOrderDO.setUserIp(userIp!=null?userIp:"10.244.209.120");

            tradeOrderDO.setStatus(orderStatus==10? TradeOrderStatusEnum.COMPLETED.getStatus():TradeOrderStatusEnum.CANCELED.getStatus());
            tradeOrderDO.setProductCount(1);


            tradeOrderDO.setCommentStatus(false);
            tradeOrderDO.setPayOrderId(id);
            tradeOrderDO.setPayStatus(orderStatus == 10);
            tradeOrderDO.setPayTime(LocalDateTimeUtil.of(successTime));
            tradeOrderDO.setDeliveryType(3);

            tradeOrderDO.setCouponPrice(0);
            tradeOrderDO.setUsePoint(0);
            tradeOrderDO.setPayPrice(0);
            tradeOrderDO.setPointPrice(0);
            tradeOrderDO.setGivePoint(0);
            tradeOrderDO.setTotalPrice(amount);
            tradeOrderDO.setDiscountPrice(0);
            tradeOrderDO.setDeliveryPrice(0);
            tradeOrderDO.setAdjustPrice(0);
            tradeOrderDO.setPayPrice(0);

            tradeOrderDO.setBrokerageUserId(null);



            tradeOrderDO.setPayChannelCode(null);


            tradeOrderDO.setRefundStatus(0);
            tradeOrderDO.setRefundPrice(0);


            tradeOrderDO.setRefundPoint(0);
            tradeOrderDO.setVipPrice(0);

            tradeOrderDO.setDeleted(false);
            tradeOrderDO.setCreator(userId);
            tradeOrderDO.setUpdater(userId);
            tradeOrderDO.setCreateTime(LocalDateTimeUtil.of(createTime));
            tradeOrderDO.setUpdateTime(LocalDateTimeUtil.of(updateTime));
            tradeOrderDOS.add(tradeOrderDO);


        }

        System.out.println("================数据解析完成=====================");
        // 1.2 关闭链接
        resultSet.close();
        sourceConnection.close();

        System.out.println("================开始插入数据=====================");
        // 2.链接目标数据库
        Connection targetConnection = DriverManager.getConnection(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
        targetConnection.setAutoCommit(false);
        targetConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // 2.1 批量插入数据
        PreparedStatement preparedStatement = targetConnection.prepareStatement("INSERT INTO `trade_order` " +
                "( `no`, `type`, `terminal`, `user_id`, `user_ip`, `status`, `product_count`,  `comment_status`,  `pay_order_id`, `pay_status`, " +
                "`pay_time`, `pay_channel_code`, `total_price`, `discount_price`, `delivery_price`, `adjust_price`," +
                " `pay_price`, `delivery_type`,  `refund_status`, " +
                "`refund_price`,  `coupon_price`, `use_point`, `point_price`, `give_point`,  `refund_point`, `vip_price`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`) VALUES" +
                " ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?);");
        for (TradeOrderDO orderDO : tradeOrderDOS) {
            preparedStatement.setString(1, orderDO.getNo());
            preparedStatement.setInt(2, orderDO.getType());
            preparedStatement.setInt(3, orderDO.getTerminal());
            preparedStatement.setLong(4, orderDO.getUserId());
            preparedStatement.setString(5, orderDO.getUserIp());
            preparedStatement.setInt(6, orderDO.getStatus());
            preparedStatement.setInt(7, 1);
            preparedStatement.setBoolean(8, false);
            preparedStatement.setLong(9, orderDO.getPayOrderId());
            preparedStatement.setBoolean(10, orderDO.getPayStatus());

            preparedStatement.setTimestamp(11, Timestamp.valueOf(orderDO.getPayTime()));
            preparedStatement.setString(12, "alipay_pc");
            preparedStatement.setInt(13, orderDO.getPayPrice());
            preparedStatement.setInt(14, 0);
            preparedStatement.setInt(15, 0);
            preparedStatement.setInt(16, 0);
            preparedStatement.setInt(17, orderDO.getPayPrice());
            preparedStatement.setInt(18, 3);
            preparedStatement.setInt(19, 0);

            preparedStatement.setInt(20, 0);
            preparedStatement.setInt(21, 0);
            preparedStatement.setInt(22, 0);
            preparedStatement.setInt(23, 0);
            preparedStatement.setInt(24, 0);
            preparedStatement.setInt(25, 0);
            preparedStatement.setInt(26, 0);
            preparedStatement.setString(27, String.valueOf(orderDO.getUserId()));
            preparedStatement.setTimestamp(28, Timestamp.valueOf(orderDO.getCreateTime()));
            preparedStatement.setString(29, String.valueOf(orderDO.getUserId()));
            preparedStatement.setTimestamp(30, Timestamp.valueOf(orderDO.getUpdateTime()));
            preparedStatement.setBoolean(31, false);
            preparedStatement.setString(32, "2");

            preparedStatement.addBatch();
        }

        preparedStatement.executeBatch();
        targetConnection.commit();
        preparedStatement.close();
        targetConnection.close();
        System.out.println("================数据插入完成=====================");



    }


}

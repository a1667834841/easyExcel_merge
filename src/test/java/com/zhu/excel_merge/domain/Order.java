package com.zhu.excel_merge.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ggBall
 * @version 1.0.0
 * @ClassName Order.java
 * @Description 订单
 * @createTime 2023年03月01日 14:10:00
 */
@Data
public class Order implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单名称
     */
    private String orderName;

    /**
     * 创建时间
     */
    private Date createTime;
}

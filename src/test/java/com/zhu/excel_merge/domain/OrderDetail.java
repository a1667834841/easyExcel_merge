package com.zhu.excel_merge.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ggBall
 * @version 1.0.0
 * @ClassName OrderDetail.java
 * @Description 订单明细的
 * @createTime 2023年03月01日 14:10:00
 */
@Data
public class OrderDetail implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单明细号
     */
    private String orderDetailNo;

    /**
     * 订单明细名称
     */
    private String orderDetailName;

    /**
     * 创建时间
     */
    private Date createTime;

}

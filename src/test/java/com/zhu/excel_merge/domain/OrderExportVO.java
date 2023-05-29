package com.zhu.excel_merge.domain;

import com.zhu.excel_merge.ExcelKey;
import com.zhu.excel_merge.ExcelMerge;
import lombok.Data;

import java.util.Date;

/**
 * @author ggBall
 * @version 1.0.0
 * @ClassName OrderExportVO.java
 * @Description 导出订单明细
 * @createTime 2023年03月01日 14:28:00
 */
@Data
public class OrderExportVO {

    /**
     * 订单号
     */
    @ExcelKey
    @ExcelMerge
    private String orderNo;

    /**
     * 订单名称
     */

    private String orderName;

    /**
     * 创建时间
     */
    @ExcelMerge
    private Date orderCreateTime;


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
    private Date orderDetailCreateTime;

}

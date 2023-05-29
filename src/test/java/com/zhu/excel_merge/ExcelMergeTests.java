package com.zhu.excel_merge;


import com.alibaba.excel.EasyExcel;
import com.zhu.excel_merge.domain.OrderExportVO;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ExcelMergeTests {

    private List<OrderExportVO> orderExportVOS;

    @Before
    public void setData() {

        orderExportVOS = new ArrayList<>();

        // 模拟查询出订单导出数据
        for (int i = 0; i < 20; i++) {

            Date orderCreateTime = new Date();
            String orderNo = "orderNo"+i;
            String orderName = "orderName"+i;


            Random random = new Random();
            int r = random.nextInt(5);
            for (int j = 0; j < r; j++) {

                Date orderDetailCreateTime = new Date();
                String orderDetailNo = "orderDetailNo"+i;
                String orderDetailName = "orderDetailName"+i;

                OrderExportVO orderExportVO = new OrderExportVO();
                orderExportVO.setOrderNo(orderNo);
                orderExportVO.setOrderName(orderName);
                orderExportVO.setOrderCreateTime(orderCreateTime);
                orderExportVO.setOrderDetailNo(orderDetailNo);
                orderExportVO.setOrderDetailName(orderDetailName);
                orderExportVO.setOrderDetailCreateTime(orderDetailCreateTime);

                orderExportVOS.add(orderExportVO);

            }

        }
    }


    @Test
    public void testMergeWrite() throws IOException, IllegalAccessException {

        String excelName = "//order.xls";
        //  自定义合并单元格策略
        String fileName = this.getClass().getClassLoader().getResource("").getPath()+excelName;



        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, OrderExportVO.class)
                .registerWriteHandler(new MyMergeStrategy<>(orderExportVOS))
                .sheet("模板").doWrite(orderExportVOS);
    }

}

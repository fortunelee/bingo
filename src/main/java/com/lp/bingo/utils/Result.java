package com.lp.bingo.utils;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Result {


    // 响应码
    private int code;

    // 日期状态
    /**
     * 正常工作日对应结果为 0, 法定节假日对应结果为 1, 节假日调休补班对应的结果为 2，休息日对应结果为 3
     */
    private int status;

}

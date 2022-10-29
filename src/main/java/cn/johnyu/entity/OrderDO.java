package cn.johnyu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 订单实体
 */
@NoArgsConstructor
@Getter@Setter
@Accessors(chain = true) //链式风格的setter
public class OrderDO {

    /** 订单编号 **/
    private Integer id;

    /** 用户编号 **/
    private Long userId;

    /** 产品编号 **/
    private Long productId;

    /** 支付金额 **/
    private Integer payAmount;



}
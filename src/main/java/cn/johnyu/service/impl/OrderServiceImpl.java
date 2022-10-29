package cn.johnyu.service.impl;

import cn.johnyu.entity.OrderDO;
import cn.johnyu.dao.OrderDao;
import cn.johnyu.service.AccountService;
import cn.johnyu.service.OrderService;
import cn.johnyu.service.ProductService;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    @Override
    @DS(value = "order-ds") // 苞米豆管理的动态 datasource(productService和accountService，都声明了自己的 datasource)
    @GlobalTransactional // 全局事务的开始，order-ds 的数据源将包装 "TM"
    public Integer createOrder(Long userId, Long productId, Integer price) throws Exception {
        Integer amount = 1; // 购买数量，暂时设置为 1。

        log.info("[createOrder] 当前 XID: {}", RootContext.getXID());

        /*===============开始： 由TM 管理的三个 RM ============================================================*/
        //  扣减库存： 此事务会经由两阶段提交：
        // 第一阶段： 直接由productService 的数据源包装的RM进行"分支事务"的提交，同时在"product库中的undo_log中记录提交动作"
        // 第二阶段： （1）如收到全局事务的"提交"，直接删除undo_log中的记录，（2）如收到全局事务的"回滚"，根据undo_log执行"反向"的sql
        productService.reduceStock(productId, amount);
        // 扣减余额：与其余事务相同，两阶段提交
        accountService.reduceBalance(userId, price);
        OrderDO order = new OrderDO().setUserId(userId).setProductId(productId).setPayAmount(amount * price);
        // 保存订单：与其余事务相同，两阶段提交
        orderDao.saveOrder(order);
        /*===============结束： 由TM 管理的三个 RM ============================================================*/

        log.info("[createOrder] 保存订单: {}", order.getId());

        // 返回订单编号
        return order.getId();
    }

}
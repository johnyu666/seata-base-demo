package cn.johnyu.service.impl;

import cn.johnyu.dao.ProductDao;
import cn.johnyu.service.ProductService;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;

    @Override
    //使用指定的数据源，因为"苞米豆的动态数据源特性"， account-ds 将成为 "RM" 的代理，将参与"两阶段事务"的提交
    @DS(value = "product-ds")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reduceStock(Long productId, Integer amount) throws Exception {
        log.info("[reduceStock] 当前 XID: {}", RootContext.getXID());

        // <3> 检查库存
        checkStock(productId, amount);

        log.info("[reduceStock] 开始扣减 {} 库存", productId);
        // <4> 扣减库存
        int updateCount = productDao.reduceStock(productId, amount);
        // 扣除成功
        if (updateCount == 0) {
            log.warn("[reduceStock] 扣除 {} 库存失败", productId);
            throw new Exception("库存不足");
        }
        // 扣除失败
        log.info("[reduceStock] 扣除 {} 库存成功", productId);
    }

    private void checkStock(Long productId, Integer requiredAmount) throws Exception {
        log.info("[checkStock] 检查 {} 库存", productId);
        Integer stock = productDao.getStock(productId);
        if (stock < requiredAmount) {
            log.warn("[checkStock] {} 库存不足，当前库存: {}", productId, stock);
            throw new Exception("库存不足");
        }
    }

}
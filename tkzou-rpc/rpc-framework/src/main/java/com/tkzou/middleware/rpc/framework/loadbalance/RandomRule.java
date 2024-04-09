package com.tkzou.middleware.rpc.framework.loadbalance;

import cn.hutool.core.collection.CollectionUtil;
import com.tkzou.middleware.rpc.framework.protocol.MethodInvoker;

import java.util.List;
import java.util.Random;

/**
 * 随机选取一个实例
 *
 * @author zoutongkun
 */
public class RandomRule implements IRule {

    @Override
    public MethodInvoker choose(List<MethodInvoker> methodInvokers) {
        if (CollectionUtil.isEmpty(methodInvokers)) {
            throw new RuntimeException("当前无服务实例可供使用，请检查服务提供者是否启动。。。。。");
        }
        Random random = new Random();
        int curIndex = random.nextInt(methodInvokers.size());
        return methodInvokers.get(curIndex);
    }
}

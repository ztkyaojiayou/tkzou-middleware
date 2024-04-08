package com.tkzou.middleware.rpc.framework;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡器
 *
 * @author zoutongkun
 */
public class LoadBalance {

    public static MethodInvoker random(List<MethodInvoker> list) {
        Random random = new Random();
        int n = random.nextInt(list.size());
        return list.get(n);
    }
}

package com.tkzou.middleware.transaction.mqretry.v2.processor;

import com.tkzou.middleware.transaction.mqretry.v2.listener.CapitalAccountTaskEventListener;
import com.tkzou.middleware.transaction.mqretry.v2.listener.IKafkaMsgListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 一个具体的消息监听者
 *
 * @author zoutongkun
 */
@Service
public class MatchingMessageObserver extends AbstractMessageObserver {
    @Autowired
    private CapitalAccountTaskEventListener capitalAccountTaskEventObserver;

    @Override
    protected IKafkaMsgListener getObserver() {
        return capitalAccountTaskEventObserver;
    }
}

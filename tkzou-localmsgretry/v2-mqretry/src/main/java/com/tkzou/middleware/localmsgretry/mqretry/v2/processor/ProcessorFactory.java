//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tkzou.middleware.localmsgretry.mqretry.v2.processor;

/**
 * 消息处理器工厂
 *
 * @author zoutongkun
 */
public interface ProcessorFactory {
    /**
     * 创建消息处理器
     *
     * @param clazz
     * @return
     */
    IKafkaMessageProcessor create(Class<? extends IKafkaMessageProcessor> clazz);

    public static class Default implements ProcessorFactory {
        public Default() {
        }

        @Override
        public IKafkaMessageProcessor create(Class<? extends IKafkaMessageProcessor> clazz) {
            try {
                return (IKafkaMessageProcessor) clazz.newInstance();
            } catch (Exception var3) {
                throw new RuntimeException("Error creating processor instance", var3);
            }
        }
    }
}

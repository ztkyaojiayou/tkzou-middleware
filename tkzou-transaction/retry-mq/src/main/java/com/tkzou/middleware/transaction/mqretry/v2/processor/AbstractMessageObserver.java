package com.tkzou.middleware.transaction.mqretry.v2.processor;

import com.tkzou.middleware.transaction.mqretry.v2.common.SponsorAccountMessageType;
import com.tkzou.middleware.transaction.mqretry.v2.dto.ISponsorAccountMessage;
import com.tkzou.middleware.transaction.mqretry.v2.dto.SponsorTransactionCommitDTO;
import com.tkzou.middleware.transaction.mqretry.v2.listener.IKafkaMsgListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * 消息监听者抽象类
 *
 * @author zoutongkun
 */
@Slf4j
public abstract class AbstractMessageObserver extends AbstractMessageProcessor<SponsorAccountMessageType> {

    @Override
    public Class<SponsorAccountMessageType> messageType() {
        return SponsorAccountMessageType.class;
    }

    /**
     * 按消息类型处理消息
     * 某一类消息都通过一个专门的observer处理，
     * 相当于是订阅发布模式，其实也是监听模式，当每个ob'er's
     *
     * @param key
     * @param type
     * @param messageObject
     */
    @Override
    public void processMessage(String key, SponsorAccountMessageType type,
                               Map<Object, Object> messageObject) {
        //不同type的msg对应的dto
        ISponsorAccountMessage sponsorTransactionCommitDTO = new SponsorTransactionCommitDTO();
        switch (type) {
            case COMMIT_SPONSOR_TRANSACTION:
                //通知需要监听该类消息的监听者即可
                getObserver().onCommitMsg(sponsorTransactionCommitDTO);
                break;
            case SPONSOR_GO_MATCHING:
                getObserver().onSponsorGoMatchingMessage(sponsorTransactionCommitDTO);
                break;
            case TOP_UP:
                getObserver().onSponsorTopUp(sponsorTransactionCommitDTO);
                break;
            case GO_TRANSFER_FOR_REDEEM:
                getObserver().onSponsorGoTransfer(sponsorTransactionCommitDTO);
                break;
            case TRANSFER_PAYOUT_SUCCESS:
                getObserver().onSponsorTransferPayoutSuccess(sponsorTransactionCommitDTO);
                break;
            case MARKETING:
                getObserver().onSponsorMarketing(sponsorTransactionCommitDTO);
                break;
            case WITHDRAW:
                getObserver().onSponsorWithdraw(sponsorTransactionCommitDTO);
                break;
            default:
                throw new RuntimeException("Undetermined SponsorAccountMessageType type : " + type);
        }
    }

    /**
     * 获取一个观察者/监听者
     *
     * @return
     */
    abstract protected IKafkaMsgListener getObserver();
}

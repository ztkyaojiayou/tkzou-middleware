package com.tkzou.middleware.localmsgretry.mqretry.v2.listener;

import com.tkzou.middleware.localmsgretry.mqretry.v2.dto.ISponsorAccountMessage;

/**
 * kafka消息监听者
 * 其实就
 *
 * @author zoutongkun
 */
public interface IKafkaMsgListener {
    /**
     * 消息提交时
     * 还可以定义一些业务方法，用于在消息提交前后执行
     *
     * @param transactionCommitDTO
     */
    void onCommitMsg(ISponsorAccountMessage transactionCommitDTO);

    /**
     * 业务方法
     *
     * @param sponsorGoMatchingMessage
     */
    void onSponsorGoMatchingMessage(ISponsorAccountMessage sponsorGoMatchingMessage);

    /**
     * 业务方法
     *
     * @param topUpSuccessMessage
     */
    void onSponsorTopUp(ISponsorAccountMessage topUpSuccessMessage);

    /**
     * 业务方法
     *
     * @param withdrawSuccessMessage
     */
    void onSponsorWithdraw(ISponsorAccountMessage withdrawSuccessMessage);

    /**
     * 业务方法
     *
     * @param sponsorGoTransferForRedeemMessage
     */
    void onSponsorGoTransfer(ISponsorAccountMessage sponsorGoTransferForRedeemMessage);

    /**
     * 业务方法
     *
     * @param transferSuccessMessage
     */
    void onSponsorTransferPayoutSuccess(ISponsorAccountMessage transferSuccessMessage);

    /**
     * 业务方法
     *
     * @param sponsorMarketingMessage
     */
    void onSponsorMarketing(ISponsorAccountMessage sponsorMarketingMessage);
}

package com.tkzou.middleware.transaction.mqretry.v2.listener;

import com.tkzou.middleware.transaction.mqretry.v2.dto.ISponsorAccountMessage;
import org.springframework.stereotype.Service;

/**
 * 一个具体的监听者
 *
 * @author zoutongkun
 */
@Service
public class CapitalAccountTaskEventListener implements IKafkaMsgListener {

    @Override
    public void onCommitMsg(ISponsorAccountMessage transactionCommitDTO) {
    }

    @Override
    public void onSponsorGoMatchingMessage(ISponsorAccountMessage sponsorGoMatchingMessage) {

    }

    @Override
    public void onSponsorTopUp(ISponsorAccountMessage topUpSuccessMessage) {

    }

    @Override
    public void onSponsorWithdraw(ISponsorAccountMessage withdrawSuccessMessage) {

    }

    @Override
    public void onSponsorGoTransfer(ISponsorAccountMessage sponsorGoTransferForRedeemMessage) {

    }

    @Override
    public void onSponsorTransferPayoutSuccess(ISponsorAccountMessage transferSuccessMessage) {

    }

    @Override
    public void onSponsorMarketing(ISponsorAccountMessage sponsorMarketingMessage) {

    }
}

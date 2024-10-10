package com.tkzou.middleware.transaction.mqretry.v2.common;

import lombok.AllArgsConstructor;

/**
 * 枚举
 *
 * @author zoutongkun
 */
@AllArgsConstructor
public enum SponsorAccountMessageType {
    COMMIT_SPONSOR_TRANSACTION("出借人下单"),
    SPONSOR_GO_MATCHING("出借人新增匹配"),
    REDEEM("赎回"),
    TOP_UP("充值"),
    GO_TRANSFER_FOR_REDEEM("转让申请"),
    TRANSFER_PAYOUT_SUCCESS("转让打款成功"),
    WITHDRAW("提现"),
    MARKETING("营销活动"),
    ;

    private String description;
}

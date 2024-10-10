package com.tkzou.middleware.transaction.mqretry.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * msg对应的DTO
 * msg中的每一个type都对应一个实体，其是不同的，
 * 因此在解析原始消息时没有转成一个统一的DTO，
 * 因为对象并不相同，因此转成的是一个map，
 * 再在具体的处理器中转成对应的DTO
 *
 * @author zoutongkun
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Accessors(chain = true)
public class SponsorTransactionCommitDTO implements ISponsorAccountMessage {
    @NotNull
    private Long userId;
    @NotNull
    private BigDecimal worth;
    /**
     * 外键
     * 此处释义: externalId = 理财订单ID
     */
    @NotNull
    private Long externalId;
    @NotNull
    private BigDecimal totalInterest;
    @NotNull
    private Long timeActual;
    @NotNull
    private Long timeExpectedComplete;

    @Override
    public Long getBusinessTime() {
        return timeActual;
    }

    /**
     * @return 提交订单时, 外部订单号=提交这个事件的ID
     */
    @Override
    public Long getBusinessId() {
        return externalId;
    }
}

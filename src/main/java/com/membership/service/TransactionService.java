package com.membership.service;

import com.membership.dto.TransactionDTO;
import com.membership.entity.Transaction;
import com.membership.entity.Member;
import com.membership.exception.BusinessException;
import com.membership.repository.TransactionRepository;
import com.membership.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    /**
     * 更新交易记录
     * 根据交易类型更新不同的字段：
     * - 充值记录：只能修改金额和备注
     * - 消费记录：可以修改所有字段
     */
    @Transactional
    public Transaction updateTransaction(Long id, TransactionDTO dto) {
        // 查找交易记录
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("交易记录不存在"));

        // 查找会员
        Member member = memberRepository.findById(transaction.getMemberId())
                .orElseThrow(() -> new BusinessException("会员不存在"));

        // 根据交易类型进行不同的处理
        if (transaction.getType().name().equals("RECHARGE")) {
            // 处理充值记录的更新
            BigDecimal oldAmount = transaction.getAmount();
            BigDecimal newAmount = dto.getAmount();
            
            // 计算余额变化
            BigDecimal balanceChange = newAmount.subtract(oldAmount);
            
            // 更新会员余额
            member.setBalance(member.getBalance().add(balanceChange));
            memberRepository.save(member);
            
            // 只更新金额和备注
            transaction.setAmount(newAmount);
            transaction.setRemark(dto.getRemark());
        } else {
            // 处理消费记录的更新
            BigDecimal oldAmount = transaction.getAmount();
            BigDecimal oldDeduction = transaction.getDeductionAmount();
            BigDecimal newAmount = dto.getAmount();
            BigDecimal newDeduction = dto.getDeductionAmount();

            // 验证新的抵扣金额
            if (newDeduction.compareTo(newAmount) > 0) {
                throw new BusinessException("抵扣金额不能大于消费金额");
            }

            // 计算余额变化
            BigDecimal balanceChange = oldDeduction.subtract(newDeduction);
            if (member.getBalance().add(balanceChange).compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("余额不足以支付新的抵扣金额");
            }

            // 更新会员信息
            member.setBalance(member.getBalance().add(balanceChange));
            member.setTotalAmountBeforeDeduction(member.getTotalAmountBeforeDeduction().subtract(oldAmount).add(newAmount));
            member.setTotalAmount(member.getTotalAmount().subtract(oldDeduction).add(newDeduction));
            memberRepository.save(member);

            // 更新交易记录的所有相关字段
            transaction.setAmount(newAmount);
            transaction.setDeductionAmount(newDeduction);
            transaction.setConsumptionType(dto.getConsumptionType());
            transaction.setDestination(dto.getDestination());
            transaction.setRemark(dto.getRemark());
        }

        // 保存并返回更新后的交易记录
        return transactionRepository.save(transaction);
    }
} 
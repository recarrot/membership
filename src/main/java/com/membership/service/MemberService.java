package com.membership.service;

import com.membership.dto.MemberCreateDTO;
import com.membership.dto.MemberDTO;
import com.membership.dto.MemberStatisticsDTO;
import com.membership.dto.TransactionDTO;
import com.membership.entity.Member;
import com.membership.entity.Transaction;
import com.membership.enums.ConsumptionType;
import com.membership.enums.TransactionType;
import com.membership.exception.BusinessException;
import com.membership.repository.MemberRepository;
import com.membership.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 创建新会员
     */
    public Member createMember(MemberCreateDTO dto) {
        // 验证会员卡号是否已存在
        if (memberRepository.existsByCardNumber(dto.getCardNumber())) {
            throw new BusinessException("会员卡号已存在");
        }

        // 验证身份证号是否已被使用
        if (memberRepository.existsByIdNumber(dto.getIdNumber())) {
            throw new BusinessException("身份证号已被使用");
        }

        Member member = new Member();
        member.setCardNumber(dto.getCardNumber());
        member.setName(dto.getName());
        member.setIdNumber(dto.getIdNumber());
        member.setAge(dto.getAge());
        member.setJoinDate(LocalDateTime.now());
        member.setConsumptionCount(0);
        member.setTotalAmount(BigDecimal.ZERO);
        member.setBalance(BigDecimal.ZERO);
        member.setIsActive(true);

        member.setPhoneNumber(dto.getPhoneNumber());

        log.info("Creating new member: {}", dto.getCardNumber());
        return memberRepository.save(member);
    }

    /**
     * 会员充值
     */
    @Transactional
    public Transaction recharge(Long memberId, TransactionDTO dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("会员不存在"));

        if (!member.getIsActive()) {
            throw new BusinessException("会员已停用");
        }

        // 更新会员余额
        member.setBalance(member.getBalance().add(dto.getAmount()));
        memberRepository.save(member);

        // 记录充值交易
        Transaction transaction = new Transaction();
        transaction.setMemberId(memberId);
        transaction.setType(TransactionType.RECHARGE);
        transaction.setAmount(dto.getAmount());
        transaction.setRemark(dto.getRemark());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setConsumptionType(ConsumptionType.EMPTY);

        log.info("Member {} recharge: {}", member.getCardNumber(), dto.getAmount());
        return transactionRepository.save(transaction);
    }

    /**
     * 会员消费
     */
    @Transactional
    public Transaction consume(Long memberId, TransactionDTO dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("会员不存在"));

        if (!member.getIsActive()) {
            throw new BusinessException("会员已停用");
        }

        // 检查余额是否足够
        if (member.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BusinessException("余额不足");
        }

        // 更新会员信息
        member.setBalance(member.getBalance().subtract(dto.getAmount()));
        member.setConsumptionCount(member.getConsumptionCount() + 1);
        member.setTotalAmount(member.getTotalAmount().add(dto.getAmount()));
        memberRepository.save(member);

        // 记录消费交易
        Transaction transaction = new Transaction();
        transaction.setMemberId(memberId);
        transaction.setType(TransactionType.CONSUMPTION);
        transaction.setAmount(dto.getAmount());
        transaction.setRemark(dto.getRemark());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDestination(dto.getDestination());
        transaction.setConsumptionType(dto.getConsumptionType());

        log.info("Member {} consume: {}", member.getCardNumber(), dto.getAmount());
        return transactionRepository.save(transaction);
    }

    /**
     * 查询会员交易记录
     */
    public Page<Transaction> getMemberTransactions(Long memberId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException("会员不存在");
        }
        try {
            return transactionRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        } catch (Exception e) {
            log.error("查询会员交易记录异常：memberId={}, error={}", memberId, e.getMessage(), e);
            throw new BusinessException("查询交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 查询会员列表
     */
    public Page<MemberDTO> getMembers(String keyword, Pageable pageable) {
        Page<Member> members;
        if (StringUtils.hasText(keyword)) {
            members = memberRepository.findByCardNumberContainingOrNameContainingOrPhoneNumberContaining(
                    keyword, keyword, keyword, pageable);
        } else {
            members = memberRepository.findAll(pageable);
        }

        return members.map(this::convertToDTO);
    }

    /**
     * 更新会员状态
     */
    @Transactional
    public Member updateMemberStatus(Long memberId, boolean isActive) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("会员不存在"));

        member.setIsActive(isActive);
        log.info("Update member {} status to {}", member.getCardNumber(), isActive);
        return memberRepository.save(member);
    }

    /**
     * 修改会员手机号
     */
    @Transactional
    public Member updateMemberPhoneNumber(Long memberId, String newPhoneNumber){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException("会员不存在")
        );
        member.setPhoneNumber(newPhoneNumber);
        log.info("Update member {} phoneNumber to {}", member.getCardNumber(), newPhoneNumber);
        return memberRepository.save(member);
    }

    /**
     * 获取会员统计信息
     */
    public MemberStatisticsDTO getMemberStatistics() {
        MemberStatisticsDTO stats = new MemberStatisticsDTO();
        stats.setTotalMembers(memberRepository.count());
        stats.setActiveMembers(memberRepository.countByIsActiveTrue());
        stats.setTotalConsumption(memberRepository.sumTotalAmount());
        stats.setAverageConsumption(memberRepository.averageConsumptionAmount());
        return stats;
    }

    private MemberDTO convertToDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        BeanUtils.copyProperties(member, dto);
        return dto;
    }
}
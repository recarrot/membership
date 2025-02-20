package com.membership.repository;

import com.membership.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 原有方法
    Page<Transaction> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 新增按时间范围查询的方法
    Page<Transaction> findByMemberIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long memberId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable);
}
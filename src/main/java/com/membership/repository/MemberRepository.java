package com.membership.repository;

import com.membership.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByCardNumber(String cardNumber);
    boolean existsByIdNumber(String idNumber);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.isActive = true")
    long countByIsActiveTrue();

    @Query("SELECT SUM(m.totalAmount) FROM Member m")
    BigDecimal sumTotalAmount();

    @Query("SELECT AVG(m.totalAmount) FROM Member m WHERE m.consumptionCount > 0")
    BigDecimal averageConsumptionAmount();

    Page<Member> findByCardNumberContainingOrNameContaining(
            String cardNumber, String name, Pageable pageable);
}
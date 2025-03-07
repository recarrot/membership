package com.membership.controller;

import com.membership.dto.TransactionDTO;
import com.membership.entity.Transaction;
import com.membership.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 更新交易记录（暂时移除权限控制，使所有用户都能编辑账单）
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto) {
        log.info("Updating transaction with id: {}", id);
        Transaction updatedTransaction = transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok(updatedTransaction);
    }
} 
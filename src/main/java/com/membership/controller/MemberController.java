package com.membership.controller;

import com.membership.dto.*;
import com.membership.entity.Member;
import com.membership.entity.Transaction;
import com.membership.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody MemberCreateDTO dto) {
        return ResponseEntity.ok(memberService.createMember(dto));
    }

    @GetMapping
    public ResponseEntity<Page<MemberDTO>> getMembers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(memberService.getMembers(keyword, pageable));
    }

    @PostMapping("/{id}/recharge")
    public ResponseEntity<Transaction> recharge(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto) {
        return ResponseEntity.ok(memberService.recharge(id, dto));
    }

    @PostMapping("/{id}/consume")
    public ResponseEntity<Transaction> consume(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto) {
        return ResponseEntity.ok(memberService.consume(id, dto));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<Transaction>> getTransactions(
            @PathVariable Long id,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        return ResponseEntity.ok(memberService.getMemberTransactions(id, startDate, endDate, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Member> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        return ResponseEntity.ok(memberService.updateMemberStatus(id, isActive));
    }

    @PutMapping("/{id}/updatePhoneNumber")
    public ResponseEntity<Member> updatePhoneNumber(
            @PathVariable Long id,
            @RequestParam String phoneNumber){
        return ResponseEntity.ok(memberService.updateMemberPhoneNumber(id, phoneNumber));
    }

    @PutMapping("/{id}/updateIdNumber")
    public ResponseEntity<Member> updateIdNumber(
            @PathVariable Long id,
            @RequestParam String idNumber){
        return ResponseEntity.ok(memberService.updateMemberIdNumber(id, idNumber));
    }

    @GetMapping("/statistics")
    public ResponseEntity<MemberStatisticsDTO> getStatistics() {
        return ResponseEntity.ok(memberService.getMemberStatistics());
    }
}
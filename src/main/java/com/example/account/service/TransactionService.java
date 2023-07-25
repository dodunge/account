package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.account.type.TransactionResultType.*;
import static com.example.account.type.TransactionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 1. 사용자가 없는 경우
     * 2. 계좌가 없는 경우
     * 3. 사용자 아이디와 계좌 소유주가 다른 경우
     * 4. 계좌가 이미 해지 상태인 경우
     * 5. 거래금액이 잔액보다 큰 경우
     * // 6은 RequestBody에 이미 validation을 해 둠.
     * 6. 거래금액이 너무 작거나 큰 경우
     */
    @Transactional
    public TransactionDto userBalance(Long userId,
                                      String accountNumber, Long amount) {
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUseBalance(user, account, amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(S, amount, account));
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if(!user.getId().equals(account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() != AccountStatus.IN_USE){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(F, amount, account);
    }

    private Transaction saveAndGetTransaction(
            TransactionResultType transactionResultType,
            Long amount, Account account) {
        return transactionRepository.save(
                Transaction.builder()
                    .transactionType(USE)
                    .transactionResult(transactionResultType)
                    .account(account)
                    .amount(amount)
                    .balanceSnapshot(account.getBalance())
                    .transactionId(UUID.randomUUID().toString().replace("-", ""))
                    .transactionAt(LocalDateTime.now())
                    .build()
        );
    }
}

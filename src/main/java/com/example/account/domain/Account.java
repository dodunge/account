package com.example.account.domain;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

// Entity는 일종의 설정파일(설정 클래스)이라고 생각하면 됨.
// Builder로 객체를 생성하는 방법이 있음(객체 생성하는 또 다른 방법)

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity {
    // @Id : pk
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    public void useBalance(Long amount) {
        if(amount > balance) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void cancelBalance(Long amount) {
        if(amount < 0) {
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance += amount;
    }
}

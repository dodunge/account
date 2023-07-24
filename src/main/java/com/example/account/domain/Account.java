package com.example.account.domain;

import lombok.*;

import javax.persistence.*;

// Entity는 일종의 설정파일(설정 클래스)이라고 생각하면 됨.
// Builder로 객체를 생성하는 방법이 있음(객체 생성하는 또 다른 방법)

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account {
    // @Id : pk
    @Id
    @GeneratedValue
    private Long id;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
}

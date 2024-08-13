package org.hidog.member.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hidog.global.entities.BaseEntity;
import org.hidog.mypage.entities.SellRecord;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Member extends BaseEntity implements Serializable {
    @Id @GeneratedValue
    private Long seq;

    @Column(length = 65, nullable = false, unique = true)
    private String email;

    @Column(length = 65, nullable = false)
    private String password;

    @Column(length = 40, nullable = false, unique = true)
    private String userName;

    @Column(length = 60, nullable = false)
    private String address;

    @Column(length = 60)
    private String detailAddress;

    @ToString.Exclude
    @OneToMany(mappedBy = "member")
    private List<Authorities> authorities;

    @OneToMany(mappedBy = "member")
    private Set<SellRecord> sellRecords;
}
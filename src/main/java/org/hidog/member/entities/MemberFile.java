package org.hidog.member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hidog.file.entities.FileInfo;
import org.hidog.global.entities.BaseEntity;

import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFile extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    private Long seq;

    @Column(length=45, nullable = false)
    private String gid;

    @Column(length = 65, unique = true, nullable = false)
    private String email;

    @Column(length = 65,nullable = false)
    private String password;

    @Column(length = 40,nullable = false)
    private String userName;

    @Column(length=15,nullable = false)
    private String mobile;

    /*
    @ToString.Exclude
    @OneToMany(mappedBy = "memberFile")
    private List<Authorities> authorities;

     */

    @Transient
    private FileInfo profileImage;
}

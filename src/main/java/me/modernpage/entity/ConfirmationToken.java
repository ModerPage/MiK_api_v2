package me.modernpage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "confirmation_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "confirmation_token", length = 64)
    private String confirmationToken;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account account;
}

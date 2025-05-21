package com.traveljournal.domain.follow.entity;

import com.traveljournal.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    // fromUser 나를 / 팔로우를 요청하는 USER
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    // toUser 내가 / 팔로우를 요청받은 USER
    private Member toMember;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    public void accept() {
        this.requestStatus = RequestStatus.ACCEPTED;
    }

    public void reject() {
        this.requestStatus = RequestStatus.REJECTED;
    }
}

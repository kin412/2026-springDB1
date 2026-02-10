package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/*
    예외 누수 문제 해결
    SQLException 제거

    MemberRepository 인터페이스 의존
 */
@Slf4j
//@Transactional // 이렇게 할 경우 클래스내 모든 메서드에 트랜잭션 적용
public class MemberServiceV4 {

    //private final TransactionTemplate txTemplate;
    private final MemberRepository MemberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {
        this.MemberRepository = memberRepository;
    }

    @Transactional // 트랜잭션 끝
    public void accountTransfer(String fromId, String toId, int money) {

        bizLogic(fromId, toId, money);

    }

    private void bizLogic(String fromId, String toId, int
            money){
        Member fromMember = MemberRepository.findById(fromId);
        Member toMember = MemberRepository.findById(toId);
        MemberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        MemberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}

package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

/*
    트랜잭션 - @Transactional AOP
    스프링 컨테이너위에서 동작함.
 */
@Slf4j
//@Transactional // 이렇게 할 경우 클래스내 모든 메서드에 트랜잭션 적용
public class MemberServiceV3_3 {

    //private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 MemberRepository;

    public MemberServiceV3_3(/*PlatformTransactionManager transactionManager,*/ MemberRepositoryV3 memberRepository) {
        //this.txTemplate = new TransactionTemplate(transactionManager);
        MemberRepository = memberRepository;
    }

    @Transactional // 트랜잭션 끝
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        /*txTemplate.executeWithoutResult((status) -> {
            //비즈니스 로직
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });*/

        bizLogic(fromId, toId, money);

    }

    private void bizLogic(String fromId, String toId, int
            money) throws SQLException {
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

package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 MemberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = MemberRepository.findById(fromId);
        Member toMember = MemberRepository.findById(toId);

        MemberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        MemberRepository.update(toId, toMember.getMoney() + money);

    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}

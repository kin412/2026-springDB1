package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/*
예외 누수 문제 해결
체크 예외를 런타임 예외로 변경
MemberRepository 인터페이스 사용
throws SQLException 제거
 */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member (member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 영향받은 row 수많큼 int 타입 반환
            return member;
        } catch (SQLException e) {
            throw new MyDbException(e); //언체크로 감싸기
        } finally {
            close(con, pstmt, null); //try에서 에러가 나도 반드시 종료해줘야하기때문에 finally
        }

    }

    @Override
    public Member findById(String memberId){
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else{
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        }catch (SQLException e) {
            throw new MyDbException(e); //언체크로 감싸기
        }finally {
            close(con, pstmt, rs);
        }

    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); // 영향받은 row 수많큼 int 타입 반환
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            throw new MyDbException(e); //언체크로 감싸기
        } finally {
            close(con, pstmt, null); //try에서 에러가 나도 반드시 종료해줘야하기때문에 finally
        }

    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new MyDbException(e); //언체크로 감싸기
        } finally {
            close(con, pstmt, null); //try에서 에러가 나도 반드시 종료해줘야하기때문에 finally
        }

    }

    private void close(Connection con , Statement stmt, ResultSet rs) {

        //JdbcUtils로 아래의 코드를 대체
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        //JdbcUtils.closeConnection(con);
        DataSourceUtils.releaseConnection(con, dataSource);

    }

    private Connection getConnection() throws SQLException {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        //Connection con = dataSource.getConnection();
        Connection con = DataSourceUtils.getConnection(dataSource);

        log.info("getConnection={}, class={}", con, con.getClass());
        return con;
    }

}

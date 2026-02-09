package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/*
JDBC - DataSource 사용, JdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); //try에서 에러가 나도 반드시 종료해줘야하기때문에 finally
        }

    }

    public Member findById(String memberId) throws SQLException {
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
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }

    }

    public void update(String memberId, int money) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); //try에서 에러가 나도 반드시 종료해줘야하기때문에 finally
        }

    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); //try에서 에러가 나도 반드시 종료해줘야하기때문에 finally
        }

    }

    private void close(Connection con , Statement stmt, ResultSet rs) {

        //JdbcUtils로 아래의 코드를 대체
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);

        /*if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("rs close error", e);
            }
        }

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("stmt close error", e);
            }
        }

        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("con close error", e);
            }
        }*/

    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("getConnection={}, class={}", con, con.getClass());
        return con;
    }

}

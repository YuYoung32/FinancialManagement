import javax.swing.*;
import java.sql.*;

public class SqlFunction {
    //region 变量声明
    //驱动路径
    static final String DBDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //数据库地址
    static final String DBURL = "jdbc:sqlserver://localhost:1433;DataBaseName=moneyManager;integratedSecurity=true";
    /*Windows身份验证不需要
///数据库登录用户名
private static final String DBUSER = "YOUNGDESKTOP";
//数据库用户密码
private static final String DBPASSWORD = "12334";
*/
    //数据库操作
    static Statement stmt = null;
    //数据库查询结果集
    static ResultSet rs = null;
    //数据库连接
    static Connection conn = null;

    //静态初始化，初始化一遍，以后就可以使用连接的对象coon了
    static {

        try {
            //加载驱动程序
            Class.forName(DBDRIVER);
            System.out.println("Succeed to load driver.");
            //连接数据库
            conn = DriverManager.getConnection(DBURL);//获得连接，public权限
            System.out.println("Succeed to connect database.");
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//endregion

    /**
     * 判断登录信息是否正确
     *
     * @param username
     * @param password
     * @return 正确返回 true 错误返回 false
     * @throws SQLException
     */
    public boolean login(String username, String password) throws SQLException {
        //创建sql语句，使用?作为占位符
        String sql = "select * from loginInfo where uid = ? and upw = ? ";
        //创建预编译查询语句对象
        PreparedStatement pstmt = conn.prepareStatement(sql);
        //给预编译查询语句对象传入参数
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        //执行预编译查询语句
        rs = pstmt.executeQuery();
        return rs.next();
    }
    /*获得结果集用这种
    //创建sql语句，使用?作为占位符
    String sql = "{call proc_sumUpMoney()}";
    //创建预编译查询语句对象
    CallableStatement proc = conn.prepareCall(sql);
    rs = proc.executeQuery();
    return proc.getInt(1);

     */
    public int returnMoney() throws SQLException {
        //创建sql语句，使用?作为占位符
        String sql = "{? = call proc_sumUpMoney()}";
        //创建预编译查询语句对象
        CallableStatement proc = conn.prepareCall(sql);
        proc.registerOutParameter(1,Types.INTEGER);
        proc.execute();
        return proc.getInt(1);
    }

    /**
     * 修改密码
     * @param username
     * @param newPwd
     */
    public void changePwd(String username, String newPwd) throws SQLException{
        String sql = "update loginInfo set upw = ? where uid = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,newPwd);
        pstmt.setString(2,username);
        pstmt.execute();
    }
}
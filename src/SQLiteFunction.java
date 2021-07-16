import javax.swing.table.DefaultTableModel;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import static javax.swing.UIManager.put;

public class SQLiteFunction {
    //region 变量声明与连接数据库
    //数据库连接
    static Connection conn = null;
    //数据库操作
    static Statement stmt = null;
    //数据库查询结果集
    static ResultSet rs = null;

    static{
        try {
            //加载驱动
            Class.forName("org.sqlite.JDBC");
            System.out.println("Succeed to load driver.");
            conn = DriverManager.getConnection("jdbc:sqlite:MyDB.db");
            System.out.println("Succeed to connect database.");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
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
        String sql = "select * from loginInfo where uid = ? and upw = ?";
        //创建预编译查询语句对象
        PreparedStatement pstmt = conn.prepareStatement(sql);
        //给预编译查询语句对象传入参数
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        //执行预编译查询语句
        rs = pstmt.executeQuery();
        return rs.next();
    }

    /**
     * 把所有收支加起来
     *
     * @return
     * @throws SQLException
     */
    public int returnMoney() throws SQLException {
        String sql = "select rtype, bal from IncomeAndSpending";
        int money = 0;
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        while (rs.next()){
            //String str = rs.getString();
            //fixme 无法匹配，好像sqlite中文编码
            String GBKstr = rs.getString("rtype");
            byte[] UTF8Byte = GBKstr.getBytes();
            String str = new String(UTF8Byte, StandardCharsets.UTF_8);
            System.out.println(str);
            if(str.equals("收入"))
                money += rs.getInt("bal");
            else if(str.equals("支出"))
                money -= rs.getInt("bal");
        }
        return money;
    }

    /**
     * 修改密码
     *
     * @param username
     * @param newPwd
     */
    public void changePwd(String username, String newPwd) throws SQLException {
        String sql = "update loginInfo set upw = ? where uid = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newPwd);
        pstmt.setString(2, username);
        pstmt.execute();
    }

    /**
     * 把收支表内数据呈现出来
     *
     * @param model
     * @throws SQLException
     */
    public void showData(DefaultTableModel model) throws SQLException {
        String sql = "select * from IncomeAndSpending order by rdate desc, id asc";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String id = rs.getString("id");
            String rdate = rs.getString("rdate");
            String rtype = rs.getString("rtype");
            String ritem = rs.getString("ritem");
            int bal = rs.getInt("bal");
            model.addRow(new Object[]{id, rdate, rtype, ritem, bal});
        }
    }

    /**
     * 更新表中数据
     *
     * @param id
     * @param rdate
     * @param rtype
     * @param ritem
     * @param bal
     * @param selid
     * @throws SQLException
     */
    public void updateData(String id, String rdate, String rtype, String ritem, int bal, String selid)
            throws SQLException {
        String sql = "update IncomeAndSpending set id=?, rdate=?, rtype=?, ritem=?, bal=? where id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        pstmt.setString(2, rdate);
        pstmt.setString(3, rtype);
        pstmt.setString(4, ritem);
        pstmt.setInt(5, bal);
        pstmt.setString(6, selid);
        pstmt.executeUpdate();
    }

    /**
     * 删除表中数据
     *
     * @param id
     * @throws SQLException
     */
    public void deleteData(String id) throws SQLException {
        String sql = "delete from IncomeAndSpending where id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        pstmt.executeUpdate();
    }

    /**
     * 插入数据
     *
     * @param id
     * @param rdate
     * @param rtype
     * @param ritem
     * @param bal
     * @throws SQLException
     */
    public void insertData(String id, String rdate, String rtype, String ritem, int bal)
            throws SQLException {
        String sql = "insert into IncomeAndSpending values(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        pstmt.setString(2, rdate);
        pstmt.setString(3, rtype);
        pstmt.setString(4, ritem);
        pstmt.setInt(5, bal);
        pstmt.executeUpdate();
    }

    /**
     * 判断数据库中是否有和传入id重复的id
     * @param id
     * @return
     * @throws SQLException
     */
    public boolean judgeRepeat(String id) throws SQLException {
        String sql = "select * from IncomeAndSpending where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        rs = pstmt.executeQuery();
        return rs.next();
    }

    /**
     * 按照开始日期和结束日期和收支类型筛选表
     * @param model
     * @param startDate
     * @param endDate
     * @param selType
     * @throws SQLException
     */
    public void selShowData(DefaultTableModel model, String startDate, String endDate, String selType) throws SQLException {
        String sql = "select * from IncomeAndSpending order by rdate desc, id asc";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String id = rs.getString("id");
            String rdate = rs.getString("rdate");
            String rtype = rs.getString("rtype");
            String ritem = rs.getString("ritem");
            int bal = rs.getInt("bal");
            if (rtype.equals(selType)) {                                            //确认收支类型
                if (startDate.length() != 0 && endDate.length() == 0) {             //指定日期以后
                    if (rdate.compareTo(startDate) >= 0) {
                        model.addRow(new Object[]{id, rdate, rtype, ritem, bal});
                    }
                } else if (startDate.length() == 0 && endDate.length() != 0) {          //指定日期之前
                    if (rdate.compareTo(endDate) <= 0) {
                        model.addRow(new Object[]{id, rdate, rtype, ritem, bal});
                    }
                } else if (startDate.length() != 0 && endDate.length() != 0) {          //两日期之间
                    if (rdate.compareTo(startDate) >= 0 && rdate.compareTo(endDate) <= 0) {
                        model.addRow(new Object[]{id, rdate, rtype, ritem, bal});
                    }
                } else {                                                                 //没有指定日期
                    model.addRow(new Object[]{id, rdate, rtype, ritem, bal});
                }
            }
        }
    }
}
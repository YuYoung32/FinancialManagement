import java.sql.*;

public class SQLiteFunction {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            //加载驱动
            Class.forName("org.sqlite.JDBC");
            System.out.println("Succeed to load driver.");
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}
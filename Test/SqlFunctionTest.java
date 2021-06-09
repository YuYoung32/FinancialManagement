import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlFunctionTest {

    @Test
    void main() {
    }

    @Test
    void login() throws SQLException {
        assertEquals(true, new SqlFunction().login("hdu", "321"));
    }
}
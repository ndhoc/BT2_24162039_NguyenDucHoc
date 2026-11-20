package lt_web.repository;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {
    public DataAccessException(Throwable cause) { super("Không thể truy cập cơ sở dữ liệu.", cause); }

    public boolean isDuplicateKey() {
        for (Throwable cause = this; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql && (sql.getErrorCode() == 2601 || sql.getErrorCode() == 2627))
                return true;
        }
        return false;
    }
}

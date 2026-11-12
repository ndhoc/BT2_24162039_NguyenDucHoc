package lt_web.repository;

public class DataAccessException extends RuntimeException {
    public DataAccessException(Throwable cause) { super("Không thể truy cập cơ sở dữ liệu.", cause); }
}

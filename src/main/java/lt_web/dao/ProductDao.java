package lt_web.dao;

import java.math.BigDecimal;
import java.util.List;

import lt_web.model.Product;

public interface ProductDao {
    List<Product> latest(int limit);
    List<Product> search(Integer categoryId, String keyword);
    long count();
    BigDecimal totalValue();
    List<Object[]> countByCategory();
}

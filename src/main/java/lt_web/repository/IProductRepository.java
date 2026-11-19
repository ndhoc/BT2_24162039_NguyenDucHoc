package lt_web.repository;

import java.math.BigDecimal;
import java.util.List;

import lt_web.entity.Product;

public interface IProductRepository {
    Product get(int id);
    void insert(Product product);
    String update(Product product);
    String delete(int id);
    List<Product> latest(int limit);
    List<Product> search(Integer categoryId, String keyword);
    long count();
    BigDecimal totalValue();
    List<Object[]> countByCategory();
}

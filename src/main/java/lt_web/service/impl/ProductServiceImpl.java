package lt_web.service.impl;

import java.math.BigDecimal;
import java.util.List;

import lt_web.dao.ProductDao;
import lt_web.dao.impl.ProductDaoImpl;
import lt_web.model.Product;
import lt_web.service.ProductService;

public class ProductServiceImpl implements ProductService {
    private final ProductDao dao = new ProductDaoImpl();

    @Override public List<Product> latest(int limit) { return dao.latest(limit); }
    @Override public List<Product> search(Integer categoryId, String keyword) { return dao.search(categoryId, keyword); }
    @Override public long count() { return dao.count(); }
    @Override public BigDecimal totalValue() { return dao.totalValue(); }
    @Override public List<Object[]> countByCategory() { return dao.countByCategory(); }
}

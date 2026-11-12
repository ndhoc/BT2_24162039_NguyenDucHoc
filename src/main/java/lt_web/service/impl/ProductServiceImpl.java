package lt_web.service.impl;

import java.math.BigDecimal;
import java.util.List;

import lt_web.entity.Product;
import lt_web.repository.IProductRepository;
import lt_web.repository.impl.ProductRepository;
import lt_web.service.ProductService;

public class ProductServiceImpl implements ProductService {
    private final IProductRepository repository = new ProductRepository();

    @Override public List<Product> latest(int limit) { return repository.latest(limit); }
    @Override public List<Product> search(Integer categoryId, String keyword) { return repository.search(categoryId, keyword); }
    @Override public long count() { return repository.count(); }
    @Override public BigDecimal totalValue() { return repository.totalValue(); }
    @Override public List<Object[]> countByCategory() { return repository.countByCategory(); }
}

package lt_web.repository.impl;

import java.math.BigDecimal;
import java.util.List;

import lt_web.entity.Product;
import lt_web.repository.IProductRepository;
import lt_web.repository.JpaConfig;

public class ProductRepository implements IProductRepository {

    @Override
    public List<Product> latest(int limit) {
        return JpaConfig.read(em -> em.createQuery(
                        "select p from Product p order by p.id desc", Product.class)
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public List<Product> search(Integer categoryId, String keyword) {
        StringBuilder jpql = new StringBuilder("select p from Product p where 1 = 1");
        if (categoryId != null) jpql.append(" and p.category.id = :categoryId");
        if (keyword != null && !keyword.isBlank()) jpql.append(" and lower(p.name) like :keyword escape '!'");
        jpql.append(" order by p.id desc");

        String escaped = keyword == null ? null : keyword.toLowerCase()
                .replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
        return JpaConfig.read(em -> {
            var query = em.createQuery(jpql.toString(), Product.class);
            if (categoryId != null) query.setParameter("categoryId", categoryId);
            if (escaped != null && !escaped.isBlank()) query.setParameter("keyword", "%" + escaped + "%");
            return query.getResultList();
        });
    }

    @Override
    public long count() {
        return JpaConfig.read(em -> em.createQuery("select count(p) from Product p", Long.class).getSingleResult());
    }

    @Override
    public BigDecimal totalValue() {
        return JpaConfig.read(em -> em.createQuery(
                        "select coalesce(sum(p.price), 0) from Product p", BigDecimal.class)
                .getSingleResult());
    }

    @Override
    public List<Object[]> countByCategory() {
        return JpaConfig.read(em -> em.createQuery(
                        "select p.category.name, count(p) from Product p group by p.category.id, p.category.name order by p.category.name",
                        Object[].class)
                .getResultList());
    }
}

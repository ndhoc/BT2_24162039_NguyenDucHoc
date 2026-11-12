package lt_web.repository.impl;

import java.util.List;

import lt_web.entity.Category;
import lt_web.repository.ICategoryRepository;
import lt_web.repository.JpaConfig;

public class CategoryRepository implements ICategoryRepository {

    @Override
    public List<Category> getAll() {
        return JpaConfig.read(em -> em.createQuery(
                "select c from Category c order by c.id", Category.class).getResultList());
    }

    @Override
    public Category get(int id) {
        return JpaConfig.read(em -> em.find(Category.class, id));
    }

    @Override
    public Category get(String name) {
        return JpaConfig.read(em -> em.createQuery(
                        "select c from Category c where c.name = :name", Category.class)
                .setParameter("name", name)
                .getResultStream().findFirst().orElse(null));
    }

    @Override
    public List<Category> search(String keyword) {
        // Treat SQL LIKE wildcards typed by the user as literal characters.
        String escaped = keyword.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
        return JpaConfig.read(em -> em.createQuery(
                        "select c from Category c where c.name like :keyword escape '!' order by c.id", Category.class)
                .setParameter("keyword", "%" + escaped + "%")
                .getResultList());
    }

    @Override
    public void insert(Category category) {
        JpaConfig.write(em -> em.persist(category));
    }

    @Override
    public void edit(Category category) {
        JpaConfig.write(em -> {
            Category managed = em.find(Category.class, category.getId());
            if (managed == null) throw new IllegalArgumentException("Danh mục không còn tồn tại.");
            managed.setName(category.getName());
            managed.setIcon(category.getIcon());
        });
    }

    @Override
    public void delete(int id) {
        JpaConfig.write(em -> {
            Category managed = em.find(Category.class, id);
            if (managed == null) throw new IllegalArgumentException("Danh mục không còn tồn tại.");
            em.remove(managed);
        });
    }

    @Override
    public long count() {
        return JpaConfig.read(em -> em.createQuery("select count(c) from Category c", Long.class).getSingleResult());
    }
}

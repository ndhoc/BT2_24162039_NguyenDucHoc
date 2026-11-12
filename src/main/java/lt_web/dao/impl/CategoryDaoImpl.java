package lt_web.dao.impl;

import java.util.List;

import lt_web.dao.CategoryDao;
import lt_web.model.Category;
import lt_web.utils.JpaUtil;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public List<Category> getAll() {
        return JpaUtil.read(em -> em.createQuery(
                "select c from Category c order by c.id", Category.class).getResultList());
    }

    @Override
    public Category get(int id) {
        return JpaUtil.read(em -> em.find(Category.class, id));
    }

    @Override
    public Category get(String name) {
        return JpaUtil.read(em -> em.createQuery(
                        "select c from Category c where c.name = :name", Category.class)
                .setParameter("name", name)
                .getResultStream().findFirst().orElse(null));
    }

    @Override
    public List<Category> search(String keyword) {
        // Treat SQL LIKE wildcards typed by the user as literal characters.
        String escaped = keyword.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
        return JpaUtil.read(em -> em.createQuery(
                        "select c from Category c where c.name like :keyword escape '!' order by c.id", Category.class)
                .setParameter("keyword", "%" + escaped + "%")
                .getResultList());
    }

    @Override
    public void insert(Category category) {
        JpaUtil.write(em -> em.persist(category));
    }

    @Override
    public void edit(Category category) {
        JpaUtil.write(em -> {
            Category managed = em.find(Category.class, category.getId());
            if (managed == null) throw new IllegalArgumentException("Danh mục không còn tồn tại.");
            managed.setName(category.getName());
            managed.setIcon(category.getIcon());
        });
    }

    @Override
    public void delete(int id) {
        JpaUtil.write(em -> {
            Category managed = em.find(Category.class, id);
            if (managed == null) throw new IllegalArgumentException("Danh mục không còn tồn tại.");
            em.remove(managed);
        });
    }

    @Override
    public long count() {
        return JpaUtil.read(em -> em.createQuery("select count(c) from Category c", Long.class).getSingleResult());
    }
}

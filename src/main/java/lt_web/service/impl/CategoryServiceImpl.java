package lt_web.service.impl;

import java.util.List;

import lt_web.entity.Category;
import lt_web.repository.ICategoryRepository;
import lt_web.repository.impl.CategoryRepository;
import lt_web.service.CategoryService;

public class CategoryServiceImpl implements CategoryService {
    private final ICategoryRepository repository = new CategoryRepository();

    private void validate(Category category) {
        String name = category.getName() == null ? "" : category.getName().trim();
        if (name.isEmpty() || name.length() > 255) throw new IllegalArgumentException("Tên danh mục phải có 1–255 ký tự.");
        category.setName(name);
    }

    @Override public void insert(Category category) { validate(category); repository.insert(category); }
    @Override public void edit(Category category) { validate(category); repository.edit(category); }
    @Override public void delete(int id) { repository.delete(id); }
    @Override public Category get(int id) { return repository.get(id); }
    @Override public Category get(String name) { return repository.get(name); }
    @Override public List<Category> getAll() { return repository.getAll(); }
    @Override public List<Category> search(String keyword) { return keyword == null || keyword.isBlank() ? getAll() : repository.search(keyword.trim()); }
    @Override public long count() { return repository.count(); }
}

package fr.fms.spring_things.services;

import fr.fms.spring_things.dao.ArticleRepository;
import fr.fms.spring_things.dao.CategoryRepository;
import fr.fms.spring_things.entities.Article;
import fr.fms.spring_things.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DataServiceTests {
  private DataService dataService;

  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @BeforeEach
  void setUp() {
    dataService = new DataService();

    ReflectionTestUtils.setField(dataService, "articleRepository", articleRepository);
    ReflectionTestUtils.setField(dataService, "categoryRepository", categoryRepository);
  }

  @Test
  void getAllArticlesReturnsAllArticles() {
    // Créer uen categorie et deux articles
    Category category = new Category(1L, "Phones", null);
    Article article1 = new Article(1L, "Android phone", "Samsung", 599.99, category);
    Article article2 = new Article(2L, "iPhone", "Apple", 999.99, category);

    // Renvoie les deux articles si findall est utlisé
    when(articleRepository.findAll()).thenReturn(List.of(article1, article2));


    List<Article> result = dataService.getAllArticles();

    assertThat(result).containsExactly(article1, article2);
    verify(articleRepository).findAll();
  }

  @Test
  void getArticleByIdReturnsArticleWhenFound() {
    Category category = new Category(1L, "Phones", null);
    Article article = new Article(1L, "Android phone", "Samsung", 599.99, category);

    when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

    Article result = dataService.getArticleById(1L);

    assertThat(result).isEqualTo(article);
    verify(articleRepository).findById(1L);
  }

  @Test
  void getArticleByIdReturnsNullWhenNotFound() {
    when(articleRepository.findById(99L)).thenReturn(Optional.empty());

    Article result = dataService.getArticleById(99L);

    assertThat(result).isNull();
    verify(articleRepository).findById(99L);
  }


  @Test
  void saveArticleCallsRepositorySave() {
    Category category = new Category(1L, "Phones", null);
    Article article = new Article(null, "Android phone", "Samsung", 599.99, category);

    dataService.saveArticle(article);

    verify(articleRepository).save(article);
  }

  @Test
  void deleteArticleDeletesWhenArticleExists() {
    when(articleRepository.existsById(1L)).thenReturn(true);

    dataService.deleteArticle(1L);

    verify(articleRepository).existsById(1L);
    verify(articleRepository).deleteById(1L);
  }

  @Test
  void deleteArticleDoesNotDeleteWhenArticleDoesNotExist() {
    when(articleRepository.existsById(99L)).thenReturn(false);

    dataService.deleteArticle(99L);

    verify(articleRepository).existsById(99L);
    verify(articleRepository, never()).deleteById(anyLong());
  }

  @Test
  void searchArticlesReturnsMatchingArticles() {
    Category category = new Category(1L, "Phones", null);
    Article article = new Article(1L, "Android phone", "Samsung", 599.99, category);

    when(articleRepository.findByDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase("android", "android"))
        .thenReturn(List.of(article));

    List<Article> result = dataService.searchArticles("android");

    assertThat(result).containsExactly(article);

    verify(articleRepository)
        .findByDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase("android", "android");
  }

  @Test
  void searchArticlesReturnsEmptyListWhenNoResult() {
    when(articleRepository.findByDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase("unknown", "unknown"))
        .thenReturn(List.of());

    List<Article> result = dataService.searchArticles("unknown");

    assertThat(result).isEmpty();

    verify(articleRepository)
        .findByDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase("unknown", "unknown");
  }

  @Test
  void getAllCategoriesReturnsAllCategories() {
    Category category1 = new Category(1L, "Phones", null);
    Category category2 = new Category(2L, "Computers", null);

    when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

    List<Category> result = dataService.getAllCategories();

    assertThat(result).containsExactly(category1, category2);
    verify(categoryRepository).findAll();
  }

  @Test
  void saveCategoryCallsRepositorySave() {
    Category category = new Category(null, "Phones", null);

    dataService.saveCategory(category);

    verify(categoryRepository).save(category);
  }

  @Test
  void deleteCategoryDeletesWhenCategoryExistsAndHasNoArticles() {
    Category category = new Category(1L, "Phones", List.of());

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    dataService.deleteCategory(1L);

    verify(categoryRepository).findById(1L);
    verify(categoryRepository).deleteById(1L);
  }

  @Test
  void deleteCategoryDoesNotDeleteWhenCategoryContainsArticles() {
    Category category = new Category(1L, "Phones", null);
    Article article = new Article(1L, "Android phone", "Samsung", 599.99, category);
    category.setArticles(List.of(article));

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    dataService.deleteCategory(1L);

    verify(categoryRepository).findById(1L);
    verify(categoryRepository, never()).deleteById(anyLong());
  }

  @Test
  void deleteCategoryDoesNothingWhenCategoryDoesNotExist() {
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    dataService.deleteCategory(99L);

    verify(categoryRepository).findById(99L);
    verify(categoryRepository, never()).deleteById(anyLong());
  }

  @Test
  void getArticlesByCategoryIdReturnsArticlesFromRepository() {
    Category category = new Category(1L, "Phones", null);
    Article article = new Article(1L, "Android phone", "Samsung", 599.99, category);

    when(articleRepository.findByCategoryId(1L)).thenReturn(List.of(article));

    List<Article> result = dataService.getArticlesByCategoryId(1L);

    assertThat(result).containsExactly(article);
    verify(articleRepository).findByCategoryId(1L);
  }

  @Test
  void clearDataDeletesArticlesThenCategories() {
    dataService.clearData();

    verify(articleRepository).deleteAll();
    verify(categoryRepository).deleteAll();
  }

}



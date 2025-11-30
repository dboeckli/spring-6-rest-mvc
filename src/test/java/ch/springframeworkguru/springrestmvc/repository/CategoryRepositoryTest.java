package ch.springframeworkguru.springrestmvc.repository;

import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.springframeworkguru.springrestmvc.bootstrap.BootstrapData;
import ch.springframeworkguru.springrestmvc.config.CacheConfiguration;
import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.entity.Category;
import ch.springframeworkguru.springrestmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class, CacheConfiguration.class})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Transactional
    @Test
    void testAddCategoryUsingExistingBeer() {
        Beer testBeer = beerRepository.findAll().getFirst();

        Category newCategory = Category.builder()
            .description("Ales")
            .build();

        testBeer.addCategory(newCategory);

        categoryRepository.save(newCategory);
        beerRepository.save(testBeer);

        assertEquals(2413, beerRepository.count());
        assertEquals(1, categoryRepository.count());
        assertEquals(1, categoryRepository.findAll().getFirst().getBeers().size());
        assertEquals(1, beerRepository.findAll().getFirst().getCategories().size());
    }

    @Transactional
    @Test
    void testAddCategoryWithNewBeer() {
        Beer newBeer = Beer.builder()
            .beerName("guguseli")
            .upc("testupc")
            .beerStyle(BeerStyle.PALE_ALE)
            .price(BigDecimal.valueOf(5))
            .build();
        Category newCategory = Category.builder()
            .description("Ales")
            .build();

        newBeer.getCategories().add(newCategory);
        newCategory.getBeers().add(newBeer);

        Category savedCategory = categoryRepository.save(newCategory);
        Beer savedBeer = beerRepository.save(newBeer);

        assertEquals(2414, beerRepository.count());
        assertEquals(1, categoryRepository.count());
        assertEquals(1, savedCategory.getBeers().size());
        assertEquals(1, savedBeer.getCategories().size());
    }

}

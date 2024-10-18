package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.bootstrap.BootstrapData;
import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.entity.Category;
import ch.springframeworkguru.springrestmvc.service.BeerCsvServiceImpl;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class CategoryRepositoryTest {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Transactional
    @Test
    void testAddCategory() {
        Beer testBeer = beerRepository.findAll().getFirst();

        Category newCategory = Category.builder()
            .description("Ales")
            .build();

        testBeer.getCategories().add(newCategory);
        //newCategory.getBeers().add(testBeer);  // TODO: we get a constraint violation, because the join table is saved twice

        Category savedCategory = categoryRepository.save(newCategory);
        Beer savedBeer = beerRepository.save(testBeer);

        assertEquals(2413, beerRepository.count());
        assertEquals(1, categoryRepository.count());
        assertEquals(0, categoryRepository.findAll().getFirst().getBeers().size()); // TODO: when above works then we should get one
        assertEquals(1, beerRepository.findAll().getFirst().getCategories().size());
    }

    @Transactional
    @Test
    void testAddCategory2() {
        //Beer testBeer = beerRepository.findAll().getFirst();
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
        //newCategory.getBeers().add(newBeer); // TODO: we get a constraint violation, because the join table is saved twice

        Category savedCategory = categoryRepository.save(newCategory);
        Beer savedBeer = beerRepository.save(newBeer);

        assertEquals(2414, beerRepository.count());
        assertEquals(1, categoryRepository.count());
        assertEquals(0, savedCategory.getBeers().size()); // TODO: when above works then we should get one
        assertEquals(1, savedBeer.getCategories().size());
    }

}

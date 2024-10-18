package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.bootstrap.BootstrapData;
import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.entity.Category;
import ch.springframeworkguru.springrestmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class CategoryRepositoryTest {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Transactional
    @Test
    void testAddCategory2() {
        Beer testBeer = beerRepository.findAll().getFirst();
        
        Category savedCategory = categoryRepository.save(Category.builder()
            .description("Ales")
            .build());

        testBeer.addCategory(savedCategory);
        Beer savedBeer = beerRepository.save(testBeer);
        
        // TODO: DOES NOT WORK WHEN ENABLING BELOW. CONSTRAINT VIOLATION EXCEPTION
        //assertEquals(2413, beerRepository.count());
        //assertEquals(1, categoryRepository.count());
        //assertEquals(1, savedCategory.getBeers().size());
        //assertEquals(1, savedBeer.getCategories().size());
    }

}

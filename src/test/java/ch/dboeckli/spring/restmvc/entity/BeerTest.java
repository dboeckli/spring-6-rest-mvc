package ch.dboeckli.spring.restmvc.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeerTest {

    @Test
    void builderInitializesCategories() {
        Beer beer = Beer.builder().build();

        assertThat(beer.getCategories()).isNotNull().isEmpty();
    }

    @Test
    void addCategoryLinksBothSides() {
        Beer beer = Beer.builder().beerName("Beer").build();
        Category category = Category.builder().description("Lager").build();

        beer.addCategory(category);

        assertThat(beer.getCategories()).containsExactly(category);
        assertThat(category.getBeers()).containsExactly(beer);
    }

    @Test
    void removeCategoryUnlinksBothSides() {
        Beer beer = Beer.builder().beerName("Beer").build();
        Category category = Category.builder().description("Lager").build();
        beer.addCategory(category);

        beer.removeCategory(category);

        assertThat(beer.getCategories()).isEmpty();
        assertThat(category.getBeers()).isEmpty();
    }

}

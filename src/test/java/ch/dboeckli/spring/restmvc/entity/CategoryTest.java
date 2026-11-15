package ch.dboeckli.spring.restmvc.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void equalsReturnsTrueForSameInstance() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category).isEqualTo(category);
    }

    @Test
    void equalsReturnsFalseForNull() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category).isNotEqualTo(null);
    }

    @Test
    void equalsReturnsFalseForDifferentCategory() {
        Category category = Category.builder().description("Lager").build();
        Category other = Category.builder().description("Lager").build();

        assertThat(category).isNotEqualTo(other);
    }

    @Test
    void equalsReturnsFalseForOtherType() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category).isNotEqualTo("Lager");
    }

    @Test
    void hashCodeHandlesDescription() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category.hashCode()).isEqualTo(31 * System.identityHashCode(category) + "Lager".hashCode());
    }

    @Test
    void hashCodeHandlesNullDescription() {
        Category category = Category.builder().description(null).build();

        assertThat(category.hashCode()).isEqualTo(31 * System.identityHashCode(category));
    }

}

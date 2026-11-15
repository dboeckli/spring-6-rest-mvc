package ch.dboeckli.spring.restmvc.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void equalsReturnsTrueForSameInstance() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category.equals(category)).isTrue();
    }

    @Test
    void equalsReturnsFalseForNull() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category.equals(null)).isFalse();
    }

    @Test
    void equalsReturnsFalseForDifferentCategory() {
        Category category = Category.builder().description("Lager").build();
        Category other = Category.builder().description("Lager").build();

        assertThat(category.equals(other)).isFalse();
    }

    @Test
    void equalsReturnsFalseForOtherType() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category.equals("Lager")).isFalse();
    }

    @Test
    void hashCodeHandlesDescription() {
        Category category = Category.builder().description("Lager").build();

        assertThat(category.hashCode()).isEqualTo(category.hashCode());
    }

    @Test
    void hashCodeHandlesNullDescription() {
        Category category = Category.builder().description(null).build();

        assertThat(category.hashCode()).isEqualTo(category.hashCode());
    }

}

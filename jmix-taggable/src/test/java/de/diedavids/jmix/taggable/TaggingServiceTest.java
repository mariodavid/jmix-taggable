package de.diedavids.jmix.taggable;

import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.entity.Tagging;
import de.diedavids.jmix.taggable.test_support.entity.Product;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaggingServiceTest {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private TaggingService sut;

    private Tag small;
    private Tag tall;
    private Tag cool;

    private Product iPhone;
    private Product iPad;

    @BeforeEach
    void setUp() {
        small = tag("small");
        tall = tag("tall");
        cool = tag("cool");

        iPhone = createProduct();
        iPad = createProduct();
    }

    @Test
    void tagsCanBeAppliedToTaggableInstances() {
        Product taggableProduct = createProduct();

        tagging(taggableProduct, small);


        Product loadedProduct = dataManager.load(Id.of(taggableProduct)).one();
        assertEquals(taggableProduct, loadedProduct);
    }

    private Product createProduct() {
        Product taggableProduct = dataManager.create(Product.class);
        taggableProduct.setName("Product with Tags");

        dataManager.save(taggableProduct);
        return taggableProduct;
    }

    @Nested
    class SetTagsForEntity {

        @Test
        void given_oneTag_expect_oneTag() {

            // when:
            sut.setTagsForEntity(iPhone, asList(cool));

            // then:
            assertThat(sut.getTags(iPhone))
                    .hasSize(1)
                    .containsOnly(cool);
        }
        @Test
        void given_noTags_expect_noTags() {

            // when:
            sut.setTagsForEntity(iPhone, emptyList());

            // then:
            assertThat(sut.getTags(iPhone))
                    .isEmpty();
        }

    }


    @Nested
    class SetTagsForEntityWithContext {

        @Test
        void given_oneTagWithContext_expect_oneTagForContext() {

            // when:
            sut.setTagsForEntityWithContext(iPhone, asList(cool), "context-1");

            // then:
            assertThat(sut.getTagsWithContext(iPhone, "context-1"))
                    .hasSize(1)
                    .containsOnly(cool);
        }

        @Test
        void given_noTags_expect_noTags() {
            // given:
            final String aContext = "context-1";
            final String aDifferentContext = "other-context";


            // when:
            sut.setTagsForEntityWithContext(iPhone, asList(cool), aContext);

            // then:
            assertThat(sut.getTagsWithContext(iPhone, aDifferentContext))
                    .isEmpty();
        }

    }


    @Nested
    class GetEntitiesWithTag {

        @Test
        void given_iPhoneWithTagCool_and_iPadWithOtherTag_expect_resultIsIPhone() {

            // given:
            sut.setTagsForEntity(iPhone, asList(cool));
            sut.setTagsForEntity(iPad, asList(tall));

            // expect:
            assertThat(sut.getEntitiesWithTag(cool))
                    .hasSize(1)
                    .containsOnly(iPhone);
        }

        @Test
        void given_multipleProductsWithTag_expect_allProductsAreReturned() {

            // given:
            sut.setTagsForEntity(iPhone, asList(cool));
            sut.setTagsForEntity(iPad, asList(cool));

            // expect:
            assertThat(sut.getEntitiesWithTag(cool))
                    .hasSize(2)
                    .containsOnly(iPad, iPhone);
        }


        @Test
        void given_noProductWithTag_expect_emptyResult() {

            // given:
            sut.setTagsForEntity(iPhone, asList(cool));
            sut.setTagsForEntity(iPad, asList(tall));

            // expect:
            assertThat(sut.getEntitiesWithTag(small))
                    .isEmpty();
        }

    }
    @Nested
    class GetTaggingsForEntity {

        @Test
        void given_iPhoneWithTwoTags_expect_twoTaggingsAreReturned() {

            // given:
            sut.setTagsForEntity(iPhone, asList(cool, small));
            sut.setTagsForEntity(iPad, asList(tall));

            // expect:
            assertThat(sut.getTaggingsForEntity(iPhone))
                    .hasSize(2)
                    .anyMatch(it -> hasTag(it, cool))
                    .anyMatch(it -> hasTag(it, small))
                    .noneMatch(it -> hasTag(it, tall));
        }

        @Test
        void given_iPhoneWithTagAndContext_expect_taggingWithContextToBeReturned() {

            // given:
            final String expectedContext = "someContext";
            sut.setTagsForEntityWithContext(iPhone, asList(cool, small), expectedContext);
            sut.setTagsForEntity(iPad, asList(tall));

            // expect:
            assertThat(sut.getTaggingsForEntity(iPhone))
                    .hasSize(2)
                    .allMatch(it -> it.getContext().equals(expectedContext));
        }

        private boolean hasTag(Tagging tagging, Tag cool) {
            return tagging.getTag().equals(cool);
        }

    }


    private Tag tag(String value) {
        final Tag tag = dataManager.create(Tag.class);
        tag.setValue(value);
        return dataManager.save(tag);
    }

    private Tagging tagging(Taggable taggable, Tag tag) {
        final Tagging tagging = dataManager.create(Tagging.class);
        tagging.setTag(tag);
        tagging.setTaggable(taggable);
        return dataManager.save(tagging);
    }
}

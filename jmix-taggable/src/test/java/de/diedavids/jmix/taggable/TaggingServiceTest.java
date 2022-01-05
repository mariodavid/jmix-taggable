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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaggingServiceTest {

    @Autowired
    DataManager dataManager;

    @Autowired
    TaggingService sut;


    Tag small;
    Tag tall;
    Tag cool;
    private Product taggable;

    @BeforeEach
    void setUp() {
        small = tag("small");
        tall = tag("tall");
        cool = tag("cool");

        taggable = createTaggable();
    }

    @Test
    void tagsCanBeAppliedToTaggableInstances() {
        Product taggableProduct = createTaggable();

        tagging(taggableProduct, small);


        Product loadedProduct = dataManager.load(Id.of(taggableProduct)).one();
        assertEquals(taggableProduct, loadedProduct);
    }

    private Product createTaggable() {
        Product taggableProduct = dataManager.create(Product.class);
        taggableProduct.setName("Product with Tags");

        dataManager.save(taggableProduct);
        return taggableProduct;
    }

    @Nested
    class SetTagsForEntity {

        @Test
        void tagsAnEntity_withOnlyOneTag_passedAsAParameter() {

            //when:
            sut.setTagsForEntity(taggable, List.of(cool));

            //then:
            assertThat(sut.getTags(taggable))
                    .hasSize(1)
                    .containsOnly(cool);
        }

    }


    private Tag tag(String value) {
        final Tag tag = dataManager.create(Tag.class);
        tag.setValue(value);
        return dataManager.save(tag);
    }

    private Tagging tagging(Taggable taggable, Tag tag, String context) {
        final Tagging tagging = dataManager.create(Tagging.class);
        tagging.setTag(tag);
        tagging.setTaggable(taggable);
        tagging.setContext(context);
        return dataManager.save(tagging);
    }

    private Tagging tagging(Taggable taggable, Tag tag) {
        final Tagging tagging = dataManager.create(Tagging.class);
        tagging.setTag(tag);
        tagging.setTaggable(taggable);
        return dataManager.save(tagging);
    }
}

package de.diedavids.jmix.taggable.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.PropertyDatatype;

import java.util.List;
import java.util.UUID;

@JmixEntity(name = "jt_TaggableToTagsDTO")
public class TaggableToTagsDTO {
    @JmixGeneratedValue
    @JmixId
    private UUID id;

    @PropertyDatatype("SoftReference")
    private Object taggable;

    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Object getTaggable() {
        return taggable;
    }

    public void setTaggable(Object taggable) {
        this.taggable = taggable;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
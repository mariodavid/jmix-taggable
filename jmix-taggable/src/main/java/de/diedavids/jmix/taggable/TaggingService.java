package de.diedavids.jmix.taggable;

import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.entity.Tagging;

import java.util.Collection;

public interface TaggingService {

    String NAME = "taggable_TaggingService";

    /**
     * tags an entity with a set of tags
     *
     * Warning: It removes all the existing tag associations for the entity and keeps only those,
     * that are listed in the tags parameter
     *
     * @param entity the entity to tag
     * @param tags the exclusive list of tags that should be associated to that entity. Every tag, that the entity was
     *             previously associated with the entity and is not listed in the parameter, will get removed
     */
    void setTagsForEntity(Taggable entity, Collection<Tag> tags);



    /**
     * tags an entity with a set of tags under a particular "persistent taggable attribute"
     *
     * Warning: It removes all the existing tag associations for the entity and keeps only those,
     * that are listed in the tags parameter
     *
     * @param entity the entity to tag
     * @param tags the exclusive list of tags that should be associated to that entity. Every tag, that the entity was
     *             previously associated with the entity and is not listed in the parameter, will get removed
     * @param persistentTaggableAttribute the persistent attribute of the tagging entity that should be additionally used
     */
    void setTagsForEntity(Taggable entity, Collection<Tag> tags, String persistentTaggableAttribute);




    /**
     * tags an entity with a set of tags for a particular tag context
     *
     * Warning: It removes all the existing tag associations for the entity and keeps only those,
     * that are listed in the tags parameter
     *
     * @param entity the entity to tag
     * @param tags the exclusive list of tags that should be associated to that entity. Every tag, that the entity was
     *             previously associated with the entity and is not listed in the parameter, will get removed
     * @param tagContext the tag context to store the tag <-> entity association
     */
    void setTagsForEntityWithContext(Taggable entity, Collection<Tag> tags, String tagContext);



    /**
     * tags an entity with a set of tags for a particular tag context
     *
     * Warning: It removes all the existing tag associations for the entity and keeps only those,
     * that are listed in the tags parameter
     *
     * @param entity the entity to tag
     * @param tags the exclusive list of tags that should be associated to that entity. Every tag, that the entity was
     *             previously associated with the entity and is not listed in the parameter, will get removed
     * @param persistentTaggableAttribute the persistent attribute of the tagging entity that should be additionally used
     * @param tagContext the tag context to store the tag <-> entity association
     */
    void setTagsForEntityWithContext(Taggable entity, Collection<Tag> tags, String persistentTaggableAttribute, String tagContext);

    /**
     * returns all tags of a particular entity
     *
     * @param entity the entity to search for
     * @return the associated tags
     */
    Collection<Tag> getTags(Taggable entity);


    /**
     *
     * returns all tags of a particular entity for a given tag context
     *
     * @param entity the entity to search for
     * @param tagContext the tag context to search for
     * @return the associated tags
     */
    Collection<Tag> getTagsWithContext(Taggable entity, String tagContext);


    /**
     * returns all entities that are associated to a particular tag
     * @param tag the tag to search for
     * @return the associated entities
     */
    Collection<Taggable> getEntitiesWithTag(Tag tag);


    /**
     * returns all taggings (mapping table between tag and entity) for a given entity
     * @param entity the entity to search for
     * @return the associated taggings
     */
    Collection<Tagging> getTaggingsForEntity(Taggable entity);
}
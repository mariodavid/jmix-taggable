package de.diedavids.jmix.taggable.impl;

import de.diedavids.jmix.taggable.TaggingService;
import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.entity.Tagging;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlanBuilder;
import io.jmix.core.Metadata;
import io.jmix.core.SaveContext;
import io.jmix.core.querycondition.PropertyCondition;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(TaggingService.NAME)
class TaggingServiceBean implements TaggingService {

    private final DataManager dataManager;
    private final Metadata metadata;


    TaggingServiceBean(DataManager dataManager, Metadata metadata) {
        this.dataManager = dataManager;
        this.metadata = metadata;
    }

    @Override
    public void setTagsForEntity(Taggable taggable, Collection<Tag> tags) {
        setTagsForEntity(taggable, tags, null);
    }

    @Override
    public void setTagsForEntity(Taggable taggable, Collection<Tag> newTags, String persistentTaggableAttribute) {
        SaveContext saveContext = new SaveContext();

        Collection<Tag> existingTags = getTags(taggable);

        addTagsToAddToCommitContext(newTags, existingTags, taggable, persistentTaggableAttribute, null, saveContext);
        addTagsToRemoveToCommitContext(existingTags, newTags, taggable, saveContext);

        dataManager.save(saveContext);
    }

    @Override
    public void setTagsForEntityWithContext(Taggable taggable, Collection<Tag> newTags, String tagContext) {
        setTagsForEntityWithContext(taggable, newTags, null, tagContext);
    }

    @Override
    public void setTagsForEntityWithContext(Taggable taggable, Collection<Tag> newTags, String persistentTaggableAttribute, String tagContext) {
        SaveContext saveContext = new SaveContext();

        Collection<Tag> existingTags = getTagsWithContext(taggable, tagContext);
        addTagsToAddToCommitContext(newTags, existingTags, taggable, persistentTaggableAttribute, tagContext, saveContext);
        addTagsToRemoveToCommitContext(existingTags, newTags, taggable, saveContext);

        dataManager.save(saveContext);
    }

    private void addTagsToRemoveToCommitContext(Collection<Tag> existingTags, Collection<Tag> newTags, Taggable taggable, SaveContext saveContext) {
//        def tagsToRemove = existingTags - newTags
//        def existingTaggings = getTaggingsForEntity(taggable)
//        tagsToRemove.each { tagToRemove ->
//                commitContext.addInstanceToRemove(findTaggingToRemove(existingTaggings, tagToRemove))
//        }
        Collection<Tagging> existingTaggings = getTaggingsForEntity(taggable);
        
        existingTags.stream()
                .filter(it -> !newTags.contains(it))
                .map(tagToRemove -> findTaggingToRemove(existingTaggings, tagToRemove))
                .filter(Optional::isPresent)
                .forEach(saveContext::removing);
    }

    private void addTagsToAddToCommitContext(Collection<Tag> newTags, Collection<Tag> existingTags, Taggable taggable, String persistentTaggableAttribute, String tagContext, SaveContext saveContext) {
//        def tagsToAdd = newTags - existingTags
//        tagsToAdd.each { tagToAdd ->
//            Tagging tagging = createTagging(taggable, tagToAdd, persistentTaggableAttribute, tagContext)
//            saveContext.addInstanceToCommit(tagging)
//        }
        newTags.stream()
                .filter(it -> !existingTags.contains(it))
                .map(tagToAdd -> createTagging(taggable, tagToAdd, persistentTaggableAttribute, tagContext))
                .forEach(saveContext::saving);
    }

    private Optional<Tagging> findTaggingToRemove(Collection<Tagging> taggings, Tag tagToRemove) {
        return taggings.stream().filter(it -> it.getTag().equals(tagToRemove)).findFirst();
    }

    private Tagging createTagging(Taggable taggable, Tag tag, String persistentTaggableAttribute, String tagContext) {
        Tagging tagging = metadata.create(Tagging.class);
        tagging.setTag(tag);
        tagging.setContext(tagContext);
        tagging.setTaggable(taggable);

//        if (persistentTaggableAttribute) {
//            tagging[persistentTaggableAttribute] = taggable
//        }
        return tagging;
    }

    @Override
    public Collection<Tag> getTags(Taggable taggable) {
//        getTaggingsForEntityWithContext(taggable, null)*.tag
        return tagsOf(getTaggingsForEntityWithContext(taggable, null));
    }

    @Override
    public Collection<Tag> getTagsWithContext(Taggable taggable, String tagContext) {
//        if (tagContext) {
//            getTaggingsForEntityWithContext(entity, tagContext)*.tag
//        }
//        else {
//            getTaggingsForEntityWithContext(entity, null)*.tag
//        }
        if (StringUtils.hasText(tagContext)) {
            return tagsOf(getTaggingsForEntityWithContext(taggable, tagContext));
        }
        else {
            return tagsOf(getTaggingsForEntityWithContext(taggable, tagContext));
        }

    }

    private List<Tag> tagsOf(Stream<Tagging> taggings) {
        return taggings
                .map(Tagging::getTag)
                .collect(Collectors.toList());
    }

    private Stream<Tagging> getTaggingsForEntityWithContext(Taggable taggable, String tagContext) {
//        getTaggingsForEntity(taggable).findAll { it.context == tagContext }
        return getTaggingsForEntity(taggable).stream().filter(it -> Objects.equals(it.getContext(),tagContext));
    }

    @Override
    public Collection<Taggable> getEntitiesWithTag(Tag tag) {
//        getTaggingsForTag(tag)*.taggable.findAll { it != null }
        return getTaggingsForTag(tag).stream()
                .map(Tagging::getTaggable)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Tagging> getTaggingsForEntity(Taggable taggable) {
//        LoadContext.Query query = LoadContext.createQuery('select e from ddct$Tagging e where e.taggable = :taggable');
//        query.setParameter('taggable', taggable);
//        LoadContext<Tagging> loadContext = LoadContext.create(Tagging);
//                .setQuery(query).setView(TAGGING_VIEW_NAME);
//        dataManager.loadList(loadContext);
        return dataManager.load(Tagging.class)
                .condition(PropertyCondition.equal("taggable", taggable))
                .fetchPlan(taggingWithTags())
                .list();
    }


    private List<Tagging> getTaggingsForTag(Tag tag) {
//        LoadContext.Query query = LoadContext.createQuery('select e from ddct$Tagging e where e.tag = :tag');
//        query.setParameter('tag', tag);
//        LoadContext<Tagging> loadContext = LoadContext.create(Tagging);
//                .setQuery(query).setView(TAGGING_VIEW_NAME);
//        dataManager.loadList(loadContext);

        return dataManager.load(Tagging.class)
                .condition(PropertyCondition.equal("tag", tag))
                .fetchPlan(taggingWithTags())
                .list();
    }

    private Consumer<FetchPlanBuilder> taggingWithTags() {
        return fetchPlanBuilder -> fetchPlanBuilder.addFetchPlan(FetchPlan.BASE)
                .add("tag", FetchPlan.BASE);
    }

}
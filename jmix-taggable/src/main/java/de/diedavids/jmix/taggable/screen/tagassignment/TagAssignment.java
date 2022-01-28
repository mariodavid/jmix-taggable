package de.diedavids.jmix.taggable.screen.tagassignment;

import de.diedavids.jmix.taggable.TaggingService;
import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.TagAssociationHolder;
import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.screen.tag.TagCreate;
import io.jmix.core.LoadContext;
import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.TagPicker;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.screen.Install;
import io.jmix.ui.screen.MessageBundle;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.StandardOutcome;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.Target;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@UiController("jt_TagAssignment")
@UiDescriptor("tag-assignment.xml")
public class TagAssignment extends Screen {
    @Autowired
    private Metadata metadata;

    @Autowired
    protected TaggingService taggingService;


    private String persistentAttribute;
    private String tagContext;
    private Taggable taggable;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private CollectionLoader<Tag> allTagsDl;
    @Autowired
    private TagPicker<Tag> tagPicker;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private DataContext dataContext;
    @Autowired
    private InstanceContainer<TagAssociationHolder> holderDc;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        getScreenData().loadAll();
        getWindow().setCaption(messageBundle.formatMessage("tagAssignment.caption", metadataTools.getInstanceName(taggable)));
    }



    public void setPersistentAttribute(String persistentAttribute) {
        this.persistentAttribute = persistentAttribute;
    }

    public void setTagContext(String tagContext) {
        this.tagContext = tagContext;
    }

    public void setTaggable(Taggable taggable) {
        this.taggable = taggable;
    }

    @Subscribe("createTag")
    public void onCreateTag(Action.ActionPerformedEvent event) {
        final Tag tag = dataContext.create(Tag.class);
        tag.setContext(tagContext);

        screenBuilders.editor(Tag.class, this)
                .withScreenClass(TagCreate.class)
                .newEntity(tag)
                .withAfterCloseListener(tagCreateAfterScreenCloseEvent -> {
                    if (tagCreateAfterScreenCloseEvent.closedWith(StandardOutcome.COMMIT)) {

                        allTagsDl.load();

                        // setting the item value does not add the tag to the selection
                        holderDc.getItem().getTags().add(tag);

                        // setting the value explicitly also does not change the selection
                        //tagPicker.setValue(holderDc.getItem().getTags());
                    }
                })
                .show();
    }

    @Subscribe("windowCommitAndClose")
    public void onWindowCommitAndClose(Action.ActionPerformedEvent event) {

        if (!hasText(tagContext)) {
            taggingService.setTagsForEntity(taggable, holderDc.getItem().getTags(), persistentAttribute);
        }
        else {
            taggingService.setTagsForEntityWithContext(taggable, holderDc.getItem().getTags(), persistentAttribute, tagContext);
        }

        close(StandardOutcome.COMMIT);
    }

    @Subscribe("windowClose")
    public void onWindowClose(Action.ActionPerformedEvent event) {
        close(StandardOutcome.DISCARD);
    }

    @Install(to = "holderDl", target = Target.DATA_LOADER)
    private TagAssociationHolder holderDlLoadDelegate(LoadContext<TagAssociationHolder> loadContext) {

        final List<Tag> tags = taggingService.getTagsWithContext(taggable, tagContext)
                .stream()
                .collect(Collectors.toList());
        final TagAssociationHolder tagAssociationHolder = metadata.create(TagAssociationHolder.class);
        tagAssociationHolder.setTaggable(taggable);
        tagAssociationHolder.setTags(tags);
        return tagAssociationHolder;
    }
}
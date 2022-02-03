package de.diedavids.jmix.taggable.screen;

import de.diedavids.jmix.taggable.TaggingService;
import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.screen.tag.TagAssociations;
import de.diedavids.jmix.taggable.screen.tagassignment.TagAssignment;
import io.jmix.core.Messages;
import io.jmix.ui.Actions;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.ItemTrackingAction;
import io.jmix.ui.action.ListAction;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ButtonsPanel;
import io.jmix.ui.component.ComponentContainer;
import io.jmix.ui.component.HBoxLayout;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.LinkButton;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.ScreenData;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.StandardOutcome;
import io.jmix.ui.screen.UiControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;

import static de.diedavids.jmix.taggable.screen.WithTagsSupport.*;

@Component(WithTagsSupportExecution.NAME)
public class WithTagsSupportExecutionBean implements WithTagsSupportExecution {


    @Autowired
    private Messages messages;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private Actions actions;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private TaggingService taggingService;


    @Override
    public void openTagAssignment(String persistentAttribute, String tagContext, ScreenBuilders screenBuilders, Screen screen, ScreenData screenData, Table table) {

        final TagAssignment tagAssignment = screenBuilders.screen(screen)
                .withScreenClass(TagAssignment.class)
                .withOpenMode(OpenMode.DIALOG)
                .withAfterCloseListener(tagAssignmentAfterScreenCloseEvent -> {
                    if (tagAssignmentAfterScreenCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                        screenData.loadAll();
                    }
                })
                .build();

        tagAssignment.setTaggable((Taggable) table.getSingleSelected());
        tagAssignment.setPersistentAttribute(persistentAttribute);
        tagAssignment.setTagContext(tagContext);

        tagAssignment.show();

    }

    @Override
    public void initTagsTableButton(Screen screen, String persistentAttribute, String tagContext, Table listComponent, String buttonId, ButtonsPanel buttonsPanel) {

        Button button = createButton(buttonId, buttonsPanel);
        ListAction tagsAction = actions.create(ItemTrackingAction.class, "tagsAction")
                .withPrimary(true)
                .withIcon(ICON_KEY)
                .withCaption(messages.getMessage(BUTTON_MSG_KEY))
                .withHandler(e -> openTagAssignment(
                        persistentAttribute,
                        tagContext,
                        screenBuilders,
                        screen,
                        UiControllerUtils.getScreenData(screen),
                        listComponent
                ));
        listComponent.addAction(tagsAction);
        button.setAction(tagsAction);
    }

    @Override
    public void initTagsTableColumn(Screen screen, Table listComponent, String tagContext, OpenMode tagLinkOpenMode, boolean showTagsAsLink) {

        listComponent.addGeneratedColumn(
                messages.getMessage(COLUMN_MSG_KEY),
                (Table.ColumnGenerator<Object>) entity ->
                        tagsTableColumn(screen, tagContext, tagLinkOpenMode, showTagsAsLink, (Taggable) entity)
        );
    }

    private ComponentContainer tagsTableColumn(Screen screen, String tagContext, OpenMode tagLinkOpenMode, boolean showTagsAsLink, Taggable entity) {
        Collection<Tag> tags = taggingService.getTagsWithContext(entity, tagContext);
        ComponentContainer container = createContainerComponentForTags(uiComponents);

        tags.stream()
                .sorted(Comparator.comparing(Tag::getValue))
                .map(tag -> createComponentForTag(uiComponents, tag, screen, tagLinkOpenMode, showTagsAsLink))
                .forEach(container::add);

        return container;
    }


    ComponentContainer createContainerComponentForTags(UiComponents uiComponents) {
        HBoxLayout layout = uiComponents.create(HBoxLayout.NAME);
        layout.setSpacing(true);
        return layout;
    }

    io.jmix.ui.component.Component createComponentForTag(UiComponents uiComponents, Tag tag, Screen screen, OpenMode tagLinkOpenMode, boolean showTagsAsLink) {
        if (showTagsAsLink) {

            LinkButton tagComponent = uiComponents.create(LinkButton.NAME);
            tagComponent.setCaption(tag.getValue());
            tagComponent.setIcon(ICON_KEY);
            tagComponent.setAction(
                    new BaseAction("openTag")
                            .withHandler(e -> {

                                final TagAssociations tagAssociations = screenBuilders.screen(screen)
                                        .withScreenClass(TagAssociations.class)
                                        .withOpenMode(tagLinkOpenMode)
                                        .build();

                                tagAssociations.setTagLinkOpenMode(tagLinkOpenMode);
                                tagAssociations.setTag(tag);

                                tagAssociations.show();
                            })
            );
            return tagComponent;

        } else {
            Label<String> tagComponent = uiComponents.create(Label.NAME);
            tagComponent.setValue(tag.getValue());
            tagComponent.setIcon(ICON_KEY);
            return tagComponent;

        }
    }

    public Button createButton(String id, ButtonsPanel buttonsPanel) {

        Button button = uiComponents.create(Button.class);
        button.setId(id);

        buttonsPanel.add(button);

        return button;
    }
}
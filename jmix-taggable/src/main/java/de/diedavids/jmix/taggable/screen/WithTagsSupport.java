package de.diedavids.jmix.taggable.screen;

import de.diedavids.jmix.taggable.TaggingService;
import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.screen.tag.TagAssociations;
import io.jmix.core.Messages;
import io.jmix.ui.Actions;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.ItemTrackingAction;
import io.jmix.ui.action.ListAction;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ButtonsPanel;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.ComponentContainer;
import io.jmix.ui.component.HBoxLayout;
import io.jmix.ui.component.Label;
import io.jmix.ui.component.LinkButton;
import io.jmix.ui.component.Table;
import io.jmix.ui.screen.Extensions;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiControllerUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

public interface WithTagsSupport {

    String BUTTON_MSG_KEY = "actions.tags";
    String COLUMN_MSG_KEY = "column.tags";
    String ICON_KEY = "font-icon:TAGS";

    /**
     * defines the table component that will be used as a basis for the tag functionality
     *
     * @return the table
     */
    Table getListComponent();

    /**
     * the button id of the destination button
     * <p>
     * It will either picked up from existing XML definitions or created with this identifier
     *
     * @return the button identifier
     */
    default String getButtonId() {
        return "tagsBtn";
    }


    /**
     * defines the button panel that will be used for inserting the tags button
     *
     * @return the destination buttonPanel
     */
    ButtonsPanel getButtonsPanel();


    /**
     * defines the optional persistent attribute of the extended Tagging entity
     * that should be used for additionally storing the references between the Tagging entity and the usage entity
     *
     * @return the attribute name of the persistent attribute of the extended Tagging entity
     */
    default String getPersistentAttribute() {
        return null;
    }


    /**
     * defines the tag context that the tagging functionality should be scoped towards
     * It can be any string, that is treated as a identifier to differentiate between different contexts
     *
     * @return the identifier that defines the tag context
     */
    default String getTagContext() {
        return null;
    }


    /**
     * option to determine if the table (see #getListComponent()) should be enhanced by a column that shows the
     * tags as a CSV list
     *
     * @return whether the tags should be displayed in the list (default false)
     */
    default boolean showTagsInList() {
        return false;
    }

    /**
     * option to determine if the tags are rendered as links in case they are displayed in the list
     *
     * @return whether the tags should be displayed as links
     */
    default boolean showTagsAsLink() {
        return false;
    }


    /**
     * option to configure the option type of the tag link
     */
    default OpenMode tagLinkOpenMode() {
        return OpenMode.DIALOG;
    }

    @Subscribe
    default void initTagsButton(Screen.InitEvent event) {

        Screen screen = event.getSource();
        Button button = createOrGetButton(screen);

        initButtonWithTagsFunctionality(screen, button);
    }

    default void initButtonWithTagsFunctionality(Screen screen, Button button) {

        final ApplicationContext applicationContext = getApplicationContext(screen);
        WithTagsSupportExecution withTagsBean = applicationContext.getBean(WithTagsSupportExecution.class);
        Messages messages = applicationContext.getBean(Messages.class);
        ScreenBuilders screenBuilders = applicationContext.getBean(ScreenBuilders.class);

        final Actions actions = applicationContext.getBean(Actions.class);
        final Taggable taggable = (Taggable) getListComponent().getSingleSelected();

        ListAction tagsAction = actions.create(ItemTrackingAction.class, "tagsAction")
                .withPrimary(true)
                .withIcon(ICON_KEY)
                .withCaption(messages.getMessage(BUTTON_MSG_KEY))
                .withHandler(e -> withTagsBean.openTagAssignment(
                        getPersistentAttribute(),
                        getTagContext(),
                        screenBuilders,
                        screen,
                        UiControllerUtils.getScreenData(screen),
                        getListComponent()
                ));
        getListComponent().addAction(tagsAction);
        button.setAction(tagsAction);

    }


    default Button createOrGetButton(Screen screen) {
        Assert.notNull(getButtonsPanel(), "Provided Buttons Panel was null. Cannot create Tag Button.");

        ApplicationContext applicationContext = getApplicationContext(screen);
        ButtonsPanelFactory buttonsPanelFactory = applicationContext.getBean(ButtonsPanelFactory.class);


        return buttonsPanelFactory.createButton(getButtonId(), getButtonsPanel(), Collections.emptyList());
    }


    @Subscribe
    default void initTagsTableColumn(Screen.InitEvent event) {

        if (showTagsInList()) {
            Screen screen = event.getSource();

            ApplicationContext applicationContext = getApplicationContext(screen);

            TaggingService taggingService = applicationContext.getBean(TaggingService.class);
            UiComponents uiComponents = applicationContext.getBean(UiComponents.class);
            Messages messages = applicationContext.getBean(Messages.class);

            getListComponent().addGeneratedColumn(messages.getMessage(COLUMN_MSG_KEY), (Table.ColumnGenerator<Object>) entity -> {
                Collection<Tag> tags = taggingService.getTagsWithContext((Taggable) entity, getTagContext());
                ComponentContainer layout = createContainerComponentForTags(uiComponents);

                tags.stream()
                        .map(tag -> createComponentForTag(uiComponents, tag, screen))
                        .forEach(layout::add);

                return layout;
            });
        }
    }


    default ApplicationContext getApplicationContext(Screen screen) {
        return Extensions.getApplicationContext(screen);
    }


    default ComponentContainer createContainerComponentForTags(UiComponents uiComponents) {
        HBoxLayout layout = uiComponents.create(HBoxLayout.NAME);
        layout.setSpacing(true);
        return layout;
    }

    default Component createComponentForTag(UiComponents uiComponents, Tag tag, Screen screen) {
        if (showTagsAsLink()) {

            final ApplicationContext applicationContext = getApplicationContext(screen);
            final ScreenBuilders screenBuilders = applicationContext.getBean(ScreenBuilders.class);
            LinkButton tagComponent = uiComponents.create(LinkButton.NAME);
            tagComponent.setCaption(tag.getValue());
            tagComponent.setIcon(ICON_KEY);
            tagComponent.setAction(
                    new BaseAction("openTag")
                            .withHandler(e -> {

                                final TagAssociations tagEditor = screenBuilders.editor(Tag.class, screen)
                                        .withScreenClass(TagAssociations.class)
                                        .editEntity(tag)
                                        .withOpenMode(tagLinkOpenMode())
                                        .build();

                                tagEditor.setTagLinkOpenMode(tagLinkOpenMode());
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


}
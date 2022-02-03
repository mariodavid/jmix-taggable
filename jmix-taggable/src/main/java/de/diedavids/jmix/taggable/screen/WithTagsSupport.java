package de.diedavids.jmix.taggable.screen;

import io.jmix.ui.component.ButtonsPanel;
import io.jmix.ui.component.Table;
import io.jmix.ui.screen.Extensions;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;

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

        final Screen screen = event.getSource();

        withTagsSupportExecution(screen)
                .initTagsTableButton(
                        screen,
                        getPersistentAttribute(),
                        getTagContext(),
                        getListComponent(),
                        getButtonId(),
                        getButtonsPanel()
                );
    }

    @Subscribe
    default void initTagsTableColumn(Screen.InitEvent event) {

        if (showTagsInList()) {
            Screen screen = event.getSource();

            withTagsSupportExecution(screen).initTagsTableColumn(
                    screen,
                    getListComponent(),
                    getTagContext(),
                    tagLinkOpenMode(),
                    showTagsAsLink()
            );
        }
    }

    default WithTagsSupportExecution withTagsSupportExecution(Screen screen) {
        return Extensions.getApplicationContext(screen).getBean(WithTagsSupportExecution.class);
    }
}
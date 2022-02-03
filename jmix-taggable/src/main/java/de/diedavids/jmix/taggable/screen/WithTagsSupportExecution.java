package de.diedavids.jmix.taggable.screen;

import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.component.ButtonsPanel;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.ScreenData;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;

public interface WithTagsSupportExecution {

    String NAME = "jt_WithTagsSupportExecution";

    void openTagAssignment(String persistentAttribute, String tagContext, ScreenBuilders screenBuilders, Screen screen, ScreenData screenData, Table taggable);

    void initTagsTableButton(Screen screen, String persistentAttribute, String tagContext, Table listComponent, String buttonId, ButtonsPanel buttonsPanel);

    void initTagsTableColumn(Screen screen, Table listComponent, String tagContext, OpenMode tagLinkOpenMode, boolean showTagsAsLink);
}

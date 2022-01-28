package de.diedavids.jmix.taggable.screen;

import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.ScreenData;
import io.jmix.ui.screen.Screen;

public interface WithTagsSupportExecution {

    String NAME = "jt_WithTagsSupportExecution";

    void openTagAssignment(String persistentAttribute, String tagContext, ScreenBuilders screenBuilders, Screen screen, ScreenData screenData, Table taggable);
}

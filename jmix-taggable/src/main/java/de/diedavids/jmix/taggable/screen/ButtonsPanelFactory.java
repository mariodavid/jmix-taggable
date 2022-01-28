package de.diedavids.jmix.taggable.screen;

import io.jmix.ui.component.Button;
import io.jmix.ui.component.ButtonsPanel;
import io.jmix.ui.component.ComponentContainer;

import java.util.List;

public interface ButtonsPanelFactory {


    String NAME = "jt_ButtonsPanelFactory";

    /**
     * Creates a button
     *
     * @param id the buttonId to search for
     * @param buttonsPanel the buttonsPanel where the button gets added
     * @param beforeButtonIds Buttons, that should be placed before the new button
     * @return the Button which either got created
     */
    Button createButton(String id, ButtonsPanel buttonsPanel, List<String> beforeButtonIds);

}

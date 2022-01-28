package de.diedavids.jmix.taggable.screen;

import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.ButtonsPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ButtonsPanelFactory.NAME)
class ButtonsPanelFactoryBean implements ButtonsPanelFactory {

    @Autowired
    UiComponents uiComponents;

    @Override
    public Button createButton(String id, ButtonsPanel buttonsPanel, List<String> existingButtonIds) {

        Button button = uiComponents.create(Button.class);
        button.setId(id);

            buttonsPanel.add(button);

        return button;
    }
}

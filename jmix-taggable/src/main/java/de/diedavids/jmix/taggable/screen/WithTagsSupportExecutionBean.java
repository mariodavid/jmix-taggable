package de.diedavids.jmix.taggable.screen;

import de.diedavids.jmix.taggable.entity.Taggable;
import de.diedavids.jmix.taggable.screen.tagassignment.TagAssignment;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.component.Table;
import io.jmix.ui.model.ScreenData;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.StandardOutcome;
import org.springframework.stereotype.Component;

@Component(WithTagsSupportExecution.NAME)
public class WithTagsSupportExecutionBean implements WithTagsSupportExecution {


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
}
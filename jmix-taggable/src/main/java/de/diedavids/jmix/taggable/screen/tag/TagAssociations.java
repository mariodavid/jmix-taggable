package de.diedavids.jmix.taggable.screen.tag;

import de.diedavids.jmix.taggable.TaggingService;
import de.diedavids.jmix.taggable.entity.Tag;
import de.diedavids.jmix.taggable.entity.Taggable;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.IdSerialization;
import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.HBoxLayout;
import io.jmix.ui.component.LinkButton;
import io.jmix.ui.component.Table;
import io.jmix.ui.component.data.table.ContainerTableItems;
import io.jmix.ui.model.DataComponents;
import io.jmix.ui.model.KeyValueCollectionContainer;
import io.jmix.ui.screen.MessageBundle;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.StandardOutcome;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@UiController("jt_Tag-associations")
@UiDescriptor("tag-associations.xml")
public class TagAssociations extends Screen {

    private static final String VALUE_PROPERTY = "value";

    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    protected TaggingService taggingService;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private IdSerialization idSerialization;
    @Autowired
    protected DataManager dataManager;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private DataComponents dataComponents;
    @Autowired
    private HBoxLayout tableBox;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private Metadata metadata;

    private Tag tag;
    private OpenMode tagLinkOpenMode;

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setTagLinkOpenMode(OpenMode tagLinkOpenMode) {
        this.tagLinkOpenMode = tagLinkOpenMode;
    }


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        getWindow().setCaption(messageBundle.formatMessage("tagAssociations.caption", metadataTools.getInstanceName(tag)));

        tableBox.add(
                createTable(
                        taggingService.getEntitiesWithTag(tag)
                )
        );

    }

    private KeyValueCollectionContainer taggablesCollectionContainer(Collection<Taggable> taggables) {
        KeyValueCollectionContainer container = dataComponents.createKeyValueCollectionContainer();
        container.addProperty(VALUE_PROPERTY, String.class);
        container.setItems(taggables.stream()
                .map(this::toKeyValueEntity)
                .collect(Collectors.toList()));
        return container;
    }

    private Table<KeyValueEntity> createTable(Collection<Taggable> taggables) {
        Table<KeyValueEntity> table = uiComponents.create(Table.of(KeyValueEntity.class));
        table.setHeight("100%");
        table.setWidth("100%");
        table.setColumnHeaderVisible(false);

        table.setEnterPressAction(new BaseAction("enterPressAction")
                .withHandler(actionPerformedEvent -> getSelectedAndOpenEditor(table, taggables)));
        table.setItemClickAction(new BaseAction("itemClickAction")
                .withHandler(actionPerformedEvent -> getSelectedAndOpenEditor(table, taggables)));

        table.addGeneratedColumn(VALUE_PROPERTY, entity -> linkButtonForEntity(taggables, entity), LinkButton.class);
        table.setItems(new ContainerTableItems<>(taggablesCollectionContainer(taggables)));
        return table;
    }

    private KeyValueEntity toKeyValueEntity(Taggable taggable) {
        final KeyValueEntity keyValueEntity = dataManager.create(KeyValueEntity.class);
        keyValueEntity.setValue(VALUE_PROPERTY, metadataTools.getInstanceName(taggable));
        keyValueEntity.setIdName("id");
        keyValueEntity.setId(idOf(taggable));
        return keyValueEntity;
    }

    private LinkButton linkButtonForEntity(Collection<Taggable> taggables, KeyValueEntity entity) {
        BaseAction openAction = new BaseAction("openAction").withHandler(e -> openEditor(taggables, entity));
        final LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(entity.getValue(VALUE_PROPERTY));
        linkButton.setAction(openAction);
        return linkButton;
    }

    private void getSelectedAndOpenEditor(Table<KeyValueEntity> table, Collection<Taggable> taggables) {
        Optional.ofNullable(table.getSingleSelected())
                .ifPresent(entity -> openEditor(taggables, entity));
    }

    private void openEditor(Collection<Taggable> taggables, KeyValueEntity entity) {
        taggables.stream()
                .filter(it -> findTaggableForEntity(it, entity))
                .findFirst()
                .ifPresent(taggable -> {
                    final Class<Object> taggableClass = metadata.getClass(taggable).getJavaClass();
                    screenBuilders.editor(taggableClass, this)
                            .editEntity(taggable)
                            .withOpenMode(tagLinkOpenMode)
                            .show();
                });
    }

    private boolean findTaggableForEntity(Taggable taggable, KeyValueEntity entity) {
        return idOf(taggable).equals(entity.getId());
    }

    private String idOf(Taggable it) {
        return idSerialization.idToString(Id.of(it));
    }

    @Subscribe("windowCommitAndClose")
    public void onWindowCommitAndClose(Action.ActionPerformedEvent event) {
        close(StandardOutcome.CLOSE);
    }
}

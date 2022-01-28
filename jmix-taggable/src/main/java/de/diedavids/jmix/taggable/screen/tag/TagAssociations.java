package de.diedavids.jmix.taggable.screen.tag;

import io.jmix.ui.screen.*;
import de.diedavids.jmix.taggable.entity.Tag;

@UiController("jt_Tag-associations")
@UiDescriptor("tag-associations.xml")
@EditedEntityContainer("tagDc")
public class TagAssociations extends StandardEditor<Tag> {

    private OpenMode tagLinkOpenMode;

    public void setTagLinkOpenMode(OpenMode tagLinkOpenMode) {
        this.tagLinkOpenMode = tagLinkOpenMode;
    }
}
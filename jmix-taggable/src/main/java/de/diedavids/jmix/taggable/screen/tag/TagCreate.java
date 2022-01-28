package de.diedavids.jmix.taggable.screen.tag;

import io.jmix.ui.screen.*;
import de.diedavids.jmix.taggable.entity.Tag;

@UiController("jt_Tag.create")
@UiDescriptor("tag-create.xml")
@EditedEntityContainer("tagDc")
public class TagCreate extends StandardEditor<Tag> {
}
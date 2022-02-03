# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [1.0.0] - 2021-28-01

### Added

- Initial Jmix version of Taggable. Migrated from `cuba-component-taggable:0.6.0`

### Changed

- [Breaking] All entities that support Tagging, now need to implement the `de.diedavids.jmix.taggable.entity.Taggable` marker interface
- [Breaking] The user who created a tag is not available via `Tagging::getTagger` anymore, but now the regular Jmix `createdBy` field is used
- `WithTagsSupport::tagLinkOpenType` has been replaced by `WithTagsSupport::tagLinkOpenMode`. Now uses `io.jmix.ui.screen.OpenMode` instead of `com.haulmont.cuba.gui.WindowManager.OpenType`


### Removed

- Support for CUBA 6 UI APIs and `@WithTags` UI controller annotation

### Dependencies
- Jmix 1.1.x


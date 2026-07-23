Sample OpenRefine extension
===========================

This repository contains a scaffold of an OpenRefine extension, which you can use as a basis to write your own.
See [our guide to writing extensions](https://openrefine.org/docs/technical-reference/writing-extensions) for more information about the process.

### Getting started

To start your own extension, click the "Use this template" button in the top right corner of this page.
This will create a copy of this repository, where you can then change:
* The extension name and description in `module/MOD-INF/module.properties`
* The `groupId`, `artifactId`, `name` and `description` fields in `pom.xml`
* Edit this `README.md` file to describe your extension to potential users and contributors instead of the sample extension's own instructions

### Building your extension
The sample extension uses the [maven-assembly-plugin](https://maven.apache.org/plugins/maven-assembly-plugin/) to support packaging the code as a zip file. Running `mvn package` will build a zip file which by default is placed in `target/<extension.id>-<version>.zip`. `extension.id` and `version` are defined in the extension's POM.

## Model Gen Kit
Auto Generate Dart Model Class with JsonSerializable, copyWith and toString() method using IntelliJ Plugin.

(not depend on build_runner.)

Jetbrain Plugin :
[![jetbrain plugin](https://img.shields.io/jetbrains/plugin/d/22714-dartmodelgeneratekit.svg)](https://plugins.jetbrains.com/plugin/22714-dartmodelgeneratekit)

Pub.dev :
[![pub package](https://img.shields.io/pub/v/model_gen_kit.svg?color=4285F4)](https://pub.dev/packages/model_gen_kit)

### Install
1. add dependency on pubspec.yaml

```yaml
model_gen_kit: ^0.1.2
```

2.  On IntelliJ(or Android Studio), Install Plugin "Dart Model Generate Kit" ([Install Link](https://plugins.jetbrains.com/plugin/22714-dartmodelgeneratekit))

### Usage
1. Create empty dart file and write "gm" and press enter. (not need for enum creation)
2. Write your model class name and arguments.
3. Press "option(alt) + m" to generate model class.

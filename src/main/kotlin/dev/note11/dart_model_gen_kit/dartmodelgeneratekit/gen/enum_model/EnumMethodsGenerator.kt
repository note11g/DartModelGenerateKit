package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.enum_model

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.addTabIndentation

object EnumMethodsGenerator {
    fun invoke(enumInfo: ClassInfo.EnumInfo): String = with(enumInfo) {
        val fromJsonFactoryConstructorSection = generateFromJsonFactoryConstructorSection(enumInfo)
        val toJsonMethodSection = generateToJsonMethodSection(enumInfo)
        return@with fromJsonFactoryConstructorSection.addTabIndentation() +
                "\n\t$toJsonMethodSection\n"
    }

    private fun generateFromJsonFactoryConstructorSection(enumInfo: ClassInfo.EnumInfo) = with(enumInfo) {
        """
factory $className.fromJson(Object json) {
  for (final value in values) {
    if (json == value.$jsonValue) return value;
  }
  throw ParseFailedException("$className", json);
}
"""
    }

    private fun generateToJsonMethodSection(enumInfo: ClassInfo.EnumInfo) = with(enumInfo) {
        "String toJson() => $jsonValue;"
    }
}
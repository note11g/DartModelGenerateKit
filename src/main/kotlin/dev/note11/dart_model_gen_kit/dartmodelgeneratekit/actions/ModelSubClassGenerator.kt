package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.addTabIndentation

object ModelSubClassGenerator {
    fun invoke(modelInfo: ModelInfo): String = with(modelInfo) {
        val variablesSection = args.joinToString("\n") {
            "@override\nfinal ${it.type.rawDartTypeString} ${it.name};"
        }
        val constructorInitializeSection = args.joinToString("\n") {
            var res = "this.${it.name}"
            if (it.isRequired) res = "required $res"
            if (it.defaultValue != null) res = "$res = ${it.defaultValue}"
            return@joinToString "$res,"
        }
        val constructorSection =
            "const _$className({\n${constructorInitializeSection.addTabIndentation() + "\n"}}) : super._();"
        val fromJsonFactoryConstructorSection =
            "factory _$className.fromJson(json) =>\n" +
                    "\t\t_${className}Extension.createModelFromJson(json);"

        return@with """class _$className extends $className {
${variablesSection.addTabIndentation()}

${constructorSection.addTabIndentation()}

${fromJsonFactoryConstructorSection.addTabIndentation()}
}
"""
    }
}
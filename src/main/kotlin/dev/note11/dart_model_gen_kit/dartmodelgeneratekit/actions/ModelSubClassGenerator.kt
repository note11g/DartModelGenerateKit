package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.addTabIndentation

object ModelSubClassGenerator {
    fun invoke(modelInfo: ModelInfo): String = with(modelInfo) {
        val variablesSection = generateVariablesSection(modelInfo)
        val constructorSection = generateConstructorSection(modelInfo)
        val fromJsonFactoryConstructorSection = generateFromJsonFactoryConstructorSection(modelInfo)

        return@with """class _$className extends $className {
${variablesSection.addTabIndentation()}

${constructorSection.addTabIndentation()}

${fromJsonFactoryConstructorSection.addTabIndentation()}
}
"""
    }

    private fun generateVariablesSection(modelInfo: ModelInfo) = with(modelInfo) {
        args.joinToString("\n") {
            "@override\nfinal ${it.type.typeString} ${it.name};"
        }
    }

    private fun generateConstructorSection(modelInfo: ModelInfo) = with(modelInfo) {
        val constructorInitializeSection = generateConstructorInitializeSection(modelInfo)
        "const _$className({\n${constructorInitializeSection.addTabIndentation() + "\n"}}) : super._();"
    }

    private fun generateConstructorInitializeSection(modelInfo: ModelInfo) = with(modelInfo) {
        args.joinToString("\n") {
            var res = "this.${it.name}"
            if (it.isRequired) res = "required $res"
            if (it.actualDefaultValue != null) res = "$res = ${it.actualDefaultValue}"
            return@joinToString "$res,"
        }
    }

    private fun generateFromJsonFactoryConstructorSection(modelInfo: ModelInfo) = with(modelInfo) {
        "factory _$className.fromJson(Object json) =>\n" +
                "\t\t_${className}Extension.createModelFromJson(json);"
    }
}
package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.addTabIndentation

object ModelExtensionGenerator {
    fun invoke(modelInfo: ModelInfo): String = with(modelInfo) {
        val variablesSection = generateVariablesSection(modelInfo)
        val fromJsonSection = generateFromJsonSection(modelInfo)
        val toJsonSection = createToJsonSection(modelInfo)
        val copyWithSection = generateCopyWithSection(modelInfo)
        val toStringSection = generateToStringSection(modelInfo)

        return@with """mixin _${className}Extension {
${variablesSection.addTabIndentation()}

${fromJsonSection.addTabIndentation()}

${toJsonSection.addTabIndentation()}

${copyWithSection.addTabIndentation()}

${toStringSection.addTabIndentation()}
}
"""
    }

    private fun generateVariablesSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString("\n") {
            "${it.type.typeString} get ${it.name} => throw UnimplementedError();"
        }
    }

    private fun generateFromJsonSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val patternMatchingKeyVariableSection = generatePatternMatchingKeyVariableSection(modelInfo)
        val modelConstructorCallArgsSection = generateModelConstructorCallArgsSection(modelInfo)
        """static _$className createModelFromJson(Object json) {
  if (json
      case {
${patternMatchingKeyVariableSection.addTabIndentation(4)},
      }) {
    return _$className(
${modelConstructorCallArgsSection.addTabIndentation(3)},
    );
  } else {
    throw const ParseFailedException("$className");
  }
}"""
    }

    private fun generatePatternMatchingKeyVariableSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(",\n") {
            val type = it.type.dartTypeOnFromPatternMatching
            "\"${it.key}\": $type ${it.name}"
        }
    }

    private fun generateModelConstructorCallArgsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(",\n") {
            "${it.name}: ${it.type.getFromJsonConstructorParam(it.name)}"
        }
    }

    private fun createToJsonSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val jsonKeyValueSection = generateJsonKeyValueSection(modelInfo)
        """Map<String, dynamic> toJson() => {
${jsonKeyValueSection.addTabIndentation(3)},
    };"""
    }

    private fun generateJsonKeyValueSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(",\n") {
            "\"${it.key}\": ${it.type.getToJsonValue(it.name)}"
        }
    }

    private fun generateCopyWithSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val copyWithArgsSection = generateCopyWithArgsSection(modelInfo)
        val copyWithConstructorParamsSection = generateCopyWithConstructorParamsSection(modelInfo)


        """$className copyWith({
${copyWithArgsSection.addTabIndentation()},
}) =>
    $className(
${copyWithConstructorParamsSection.addTabIndentation(3)},
    );"""
    }

    private fun generateCopyWithArgsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(",\n") {
            "${it.type.dartTypeWithOutNullable}? ${it.name}"
        }
    }

    private fun generateCopyWithConstructorParamsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(",\n") {
            "${it.name}: ${it.name} ?? this.${it.name}"
        }
    }

    private fun generateToStringArgsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(", ") {
            "${it.name}: \$${it.name}"
        }
    }

    private fun generateToStringSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val toStringArgsSection = generateToStringArgsSection(modelInfo)

        """@override
String toString() =>
    '$className{${toStringArgsSection}}';"""
    }
}
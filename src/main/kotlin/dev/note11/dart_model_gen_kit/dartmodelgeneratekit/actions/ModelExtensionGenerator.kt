package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.addTabIndentation

object ModelExtensionGenerator {
    fun invoke(modelInfo: ModelInfo): String = with(modelInfo) {

        val variablesSection = args.joinToString("\n") {
            "${it.type.rawDartTypeString} get ${it.name} => throw UnimplementedError();"
        }

        val patternMatchingKeyVariableSection = args.joinToString(",\n") {
            val jsonKey = it.name // TODO : jsonKey changeable
            val type = it.type.dartTypeOnFromPatternMatching
            "\"${jsonKey}\": $type ${it.name}"
        }

        val modelConstructorCallArgsSection = args.joinToString(",\n") {
            "${it.name}: ${it.type.getFromJsonConstructorParam(it.name)}"
        }

        val fromJsonSection = """static _$className createModelFromJson(json) {
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

        val jsonKeyValueSection = args.joinToString(",\n") {
            val jsonKey = it.name // TODO : jsonKey changeable
            "\"${jsonKey}\": ${it.type.getToJsonValue(it.name)}"
        }

        val toJsonSection = """Map<String, dynamic> toJson() => {
${jsonKeyValueSection.addTabIndentation(3)},
    };"""

        val copyWithArgsSection = args.joinToString(",\n") {
            "${it.type.dartTypeWithOutNullable}? ${it.name}"
        }

        val copyWithConstructorParamsSection = args.joinToString(",\n") {
            "${it.name}: ${it.name} ?? this.${it.name}"
        }


        val copyWithSection = """$className copyWith({
${copyWithArgsSection.addTabIndentation()},
}) =>
    $className(
${copyWithConstructorParamsSection.addTabIndentation(3)},
    );"""

        val toStringArgsSection = args.joinToString(", ") {
            "${it.name}: \$${it.name}"
        }

        val toStringSection = """@override
String toString() =>
    '$className{${toStringArgsSection}}';"""

        """mixin _${className}Extension {
${variablesSection.addTabIndentation()}

${fromJsonSection.addTabIndentation()}

${toJsonSection.addTabIndentation()}

${copyWithSection.addTabIndentation()}

${toStringSection.addTabIndentation()}
}
"""
    }
}
package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.model

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelArgument
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.addTabIndentation

object ModelExtensionGenerator {
    fun invoke(modelInfo: ModelInfo): String = with(modelInfo) {
        val variablesSection = generateVariablesSection(modelInfo)
        val fromJsonSection = generateFromJsonSection(modelInfo)
        val toJsonSection = createToJsonSection(modelInfo)
        val copyWithSection = generateCopyWithSection(modelInfo)
        val toStringSection = generateToStringSection(modelInfo)
        val equalitySection = generateEqualitySection(modelInfo)

        return@with """mixin _${className}Extension {
${variablesSection.addTabIndentation()}

${fromJsonSection.addTabIndentation()}

${toJsonSection.addTabIndentation()}

${copyWithSection.addTabIndentation()}

${equalitySection.addTabIndentation()}

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
${patternMatchingKeyVariableSection.addTabIndentation(4)}
      }) {
    return _$className(
${modelConstructorCallArgsSection.addTabIndentation(3)},
    );
  } else {
    throw ParseFailedException("$className", json);
  }
}"""
    }

    private fun generatePatternMatchingKeyVariableSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val graph = makeJsonGraph(args)
        return generatePatternMatchingKeyVariableSectionRecursive(graph, 1)
    }

    private fun makeJsonGraph(args: List<ModelArgument>): Map<String, ObjectOrValue> {
        val graph = mutableMapOf<String, ObjectOrValue>()

        args.forEach { arg ->
            var nowGraph: MutableMap<String, ObjectOrValue> = graph
            for (i in 0 until arg.keys.size) {
                val nowKey = arg.keys[i]

                if (i == arg.keys.lastIndex) {
                    nowGraph[nowKey] = ObjectOrValue.Value(arg)
                } else {
                    if (!nowGraph.containsKey(nowKey)) nowGraph[nowKey] = ObjectOrValue.Object(mutableMapOf())

                    val nowGraphValue = nowGraph[nowKey] as ObjectOrValue.Object
                    nowGraph = nowGraphValue.map as MutableMap<String, ObjectOrValue>
                }
            }
        }

        return graph
    }

    private fun generatePatternMatchingKeyVariableSectionRecursive(
        graph: Map<String, ObjectOrValue>,
        depth: Int
    ): String {
        val tabs = "\t".repeat(depth)
        return graph.entries.joinToString("\n") { (key, value) ->
            when (value) {
                is ObjectOrValue.Object -> {
                    """$tabs"$key": {
${generatePatternMatchingKeyVariableSectionRecursive(value.map, depth + 1)}
$tabs},"""
                }

                is ObjectOrValue.Value -> {
                    """$tabs"$key": ${value.atFromJson},"""
                }
            }
        }
    }

    private fun generateModelConstructorCallArgsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(",\n") {
            "${it.name}: ${it.type.getFromJsonConstructorParam(it.name)}"
        }
    }

    private fun createToJsonSection(modelInfo: ModelInfo): String {
        val jsonKeyValueSection = generateJsonKeyValueSection(modelInfo)
        return """Map<String, dynamic> toJson() => {
${jsonKeyValueSection.addTabIndentation(3)}
    };"""
    }

    private fun generateJsonKeyValueSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val graph = makeJsonGraph(args)
        return generateJsonKeyValueSectionRecursive(graph, 1)
    }

    private fun generateJsonKeyValueSectionRecursive(
        graph: Map<String, ObjectOrValue>,
        depth: Int
    ): String {
        val tabs = "\t".repeat(depth)
        return graph.entries.joinToString("\n") { (key, value) ->
            when (value) {
                is ObjectOrValue.Object -> {
                    """$tabs"$key": {
${generateJsonKeyValueSectionRecursive(value.map, depth + 1)}
$tabs},"""
                }

                is ObjectOrValue.Value -> {
                    """$tabs"$key": ${value.value.type.getToJsonValue(value.value.name)},"""
                }
            }
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

    private fun generateEqualitySection(modelInfo: ModelInfo): String {
        return generateEqualsSection(modelInfo) + "\n\n" + generateHashCodeSection(modelInfo)
    }

    private fun generateEqualsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val codes = args.joinToString(" && \n") {
            "other.${it.name} == ${it.name}"
        }
        """@override
bool operator ==(Object other) =>
    identical(this, other) ||
${("other is $className &&\nruntimeType == other.runtimeType &&").addTabIndentation(4)}
${codes.addTabIndentation(4)};
"""
    }

    private fun generateHashCodeSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val codes = args.joinToString(" ^\n") {
            "${it.name}.hashCode"
        }

        """@override
int get hashCode =>
${codes.addTabIndentation(2)};"""
    }

    private fun generateToStringSection(modelInfo: ModelInfo): String = with(modelInfo) {
        val toStringArgsSection = generateToStringArgsSection(modelInfo)

        """@override
String toString() =>
    '$className{${toStringArgsSection}}';"""
    }

    private fun generateToStringArgsSection(modelInfo: ModelInfo): String = with(modelInfo) {
        args.joinToString(", ") {
            "${it.name}: \$${it.name}"
        }
    }
}

sealed class ObjectOrValue {
    data class Object(val map: Map<String, ObjectOrValue>) : ObjectOrValue()
    data class Value(val value: ModelArgument) : ObjectOrValue() {
        val atFromJson get() = "${value.type.dartTypeOnFromPatternMatching} ${value.name}"
    }
}
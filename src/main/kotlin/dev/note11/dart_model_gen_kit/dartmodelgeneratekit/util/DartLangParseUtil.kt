package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelArgument

object DartLangParseUtil {
    private const val DART_CLASS_OR_VARIABLE_REGEX = "^[_a-zA-Z$][_a-zA-Z$0-9]*$"
    private const val SINGLE_COMMENT_REGEX = "//.*"
    private const val SINGLE_LINE_COMMENT_REGEX = "//.*\\n"
    private const val MULTI_LINE_COMMENT_REGEX = "/\\\\*.*?\\\\*/\\n?"
    private const val ENUM_INSTANCE_REGEX = "\\(.*?\\)"
    private const val ENUM_VAL_ANNOTATION_REGEX = "@((enumVal)|(EnumVal\\(\\)))\\s+(.*?)[;{]"
    private const val SPACING_REGEX = "\\s+"

    private fun getDartClassNameFindRegex(isEnum: Boolean) =
        "${if (!isEnum) "class" else "enum"}\\s+([A-Z][a-zA-Z0-9_]*)".toRegex()

    private fun getModelFindRegex(className: String) =
        "const factory $className\\s*\\(\\s*\\{(.+?)\\}\\)".toRegex(RegexOption.DOT_MATCHES_ALL)

    private fun getEnumFindRegex(className: String) =
        "enum $className\\s*\\{(.+?)((;)|(}))".toRegex(RegexOption.DOT_MATCHES_ALL)

    fun isValidClassOrVariable(str: String): Boolean = DART_CLASS_OR_VARIABLE_REGEX.toRegex().matches(str)
    fun extractClassName(dartCode: String, isEnum: Boolean): String? =
        getDartClassNameFindRegex(isEnum).findPatternOnce(dartCode)

    fun findModelArgs(dartCode: String, className: String): List<ModelArgument> {
        val constructorPart = getModelFindRegex(className).findPatternOnce(dartCode) ?: return emptyList()
        val rawArguments = cuttingArguments(constructorPart)
        return rawArguments.mapNotNull(ModelArgument::parseRawArgument)
    }

    fun findEnumValues(dartCode: String, className: String): List<String> {
        val valuePart = getEnumFindRegex(className).findPatternOnce(dartCode) ?: return emptyList()
        return cuttingEnumValues(valuePart)
    }

    fun findEnumValProperty(dartCode: String): String? {
        val rawEnumValProperty = ENUM_VAL_ANNOTATION_REGEX.toRegex().find(dartCode)?.groupValues?.getOrNull(4)
        return rawEnumValProperty?.run {
            if (!contains("String")) {
                throw Exception("EnumVal property must be String type")
            }

            val operatorRemovedCode = split("=>").first().trim()
            return@run operatorRemovedCode.split(" ").last()
        }
    }

    private fun cuttingArguments(dartCode: String): List<String> {
        var insideGenericBrackets = false
        var start = 0
        val parts = mutableListOf<String>()

        for (i in dartCode.indices) {
            if (dartCode[i] == '<') {
                insideGenericBrackets = true
            } else if (dartCode[i] == '>') {
                insideGenericBrackets = false
            } else if (!insideGenericBrackets && dartCode[i] == ',') {
                parts.add(dartCode.substring(start, i).trim())
                start = i + 1
            }
        }

        parts.add(dartCode.substring(start).trim())
        return parts
    }

    private fun cuttingEnumValues(dartCode: String): List<String> {
        val trimmedCode = dartCode.replace(SPACING_REGEX.toRegex(), "")
        val constructorRemovedCode = trimmedCode.replace(ENUM_INSTANCE_REGEX.toRegex(), "")
        return constructorRemovedCode.split(",")
    }

    fun removeCodeComments(dartCode: String): String {
        val singleCommentPattern = SINGLE_COMMENT_REGEX.toRegex()
        val singleLineCommentPattern = SINGLE_LINE_COMMENT_REGEX.toRegex()
        val multiLineCommentPattern = MULTI_LINE_COMMENT_REGEX.toRegex(RegexOption.DOT_MATCHES_ALL)

        return singleCommentPattern.replace(dartCode, "")
            .let { singleLineCommentPattern.replace(it, "") }
            .let { multiLineCommentPattern.replace(it, "") }
    }

    // List<String> -> [String], Map<String, int> -> [String, int]
    fun parseGenericType(dartTypeWithGeneric: String): List<String> {
        val matchResult = "<(.*)>".toRegex().find(dartTypeWithGeneric)
            ?: throw Exception("No generic type: $dartTypeWithGeneric")
        return matchResult.groupValues[1].split(",").map { it.trim() }
    }

    fun isEnum(dartCode: String): Boolean {
        return dartCode.contains("enum ")
    }


    fun Regex.findPatternOnce(str: String): String? {
        return this.find(str)?.groups?.get(1)?.value
    }
}
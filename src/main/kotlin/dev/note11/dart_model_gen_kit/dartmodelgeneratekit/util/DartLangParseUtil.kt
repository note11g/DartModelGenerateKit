package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelArgument

object DartLangParseUtil {
    private const val DART_CLASS_OR_VARIABLE_REGEX = "^[_a-zA-Z$][_a-zA-Z$0-9]*$"
    private const val DART_CLASS_FIND_REGEX = "class\\s+([A-Z][a-zA-Z0-9_]*)"
    private const val SINGLE_LINE_COMMENT_REGEX = "//.*\\n"
    private const val MULTI_LINE_COMMENT_REGEX = "/\\\\*.*?\\\\*/\\n?"
    private fun getModelFindRegex(className: String) =
        "const factory $className\\s*\\(\\s*\\{(.+?)\\}\\)".toRegex(RegexOption.DOT_MATCHES_ALL)

    fun isValidClassOrVariable(str: String): Boolean = DART_CLASS_OR_VARIABLE_REGEX.toRegex().matches(str)
    fun extractClassName(dartCode: String): String? = DART_CLASS_FIND_REGEX.toRegex().findPatternOnce(dartCode)

    fun findModelArgs(dartCode: String, className: String): List<ModelArgument> {
        val constructorPart = getModelFindRegex(className).findPatternOnce(dartCode) ?: return emptyList()
        return constructorPart.split(",\n").mapNotNull {
            ModelArgument.parseRawArgument(it)
        }
    }

    fun removeCodeComments(dartCode: String): String {
        val singleLineCommentPattern = SINGLE_LINE_COMMENT_REGEX.toRegex()
        val multiLineCommentPattern = MULTI_LINE_COMMENT_REGEX.toRegex(RegexOption.DOT_MATCHES_ALL)
        val singleLineCleared = singleLineCommentPattern.replace(dartCode, "")
        return multiLineCommentPattern.replace(singleLineCleared, "")
    }

    // List<String> -> [String], Map<String, int> -> [String, int]
    fun parseGenericType(dartTypeWithGeneric: String): List<String> {
        val matchResult = "<(.*)>".toRegex().find(dartTypeWithGeneric)
            ?: throw Exception("No generic type: $dartTypeWithGeneric")
        return matchResult.groupValues[1].split(",").map { it.trim() }
    }


    fun Regex.findPatternOnce(str: String): String? {
        return this.find(str)?.groups?.get(1)?.value
    }
}
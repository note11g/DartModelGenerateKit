package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelArgument

object DartLangParseUtil {
    private const val dartClassOrVariableRegex = "^[_a-zA-Z$][_a-zA-Z$0-9]*$"
    private const val dartClassFindRegex = "class\\s+([A-Z][a-zA-Z0-9_]*)"
    private const val singleLineCommentRegex = "//.*\\n"
    private const val multiLineCommentRegex = "/\\\\*.*?\\\\*/\\n?"
    private fun getModelFindRegex(className: String) =
        "const factory $className\\s*\\(\\s*\\{(.+?)\\}\\)".toRegex(RegexOption.DOT_MATCHES_ALL)

    fun isValidClassOrVariable(str: String): Boolean = dartClassOrVariableRegex.toRegex().matches(str)
    fun extractClassName(dartCode: String): String? = dartClassFindRegex.toRegex().findPatternOnce(dartCode)

    fun findModelArgs(dartCode: String, className: String): List<ModelArgument> {
        val constructorPart = getModelFindRegex(className).findPatternOnce(dartCode) ?: return emptyList()
        return constructorPart.split(',').mapNotNull {
            val res = ModelArgument.parseRawArgument(it)
            println(res)
            res
        }
    }

    fun removeCodeComments(dartCode: String): String {
        val singleLineCommentPattern = singleLineCommentRegex.toRegex()
        val multiLineCommentPattern = multiLineCommentRegex.toRegex(RegexOption.DOT_MATCHES_ALL)
        val singleLineCleared = singleLineCommentPattern.replace(dartCode, "")
        return multiLineCommentPattern.replace(singleLineCleared, "")
    }

    fun Regex.findPatternOnce(str: String): String? {
        return this.find(str)?.groups?.get(1)?.value
    }
}
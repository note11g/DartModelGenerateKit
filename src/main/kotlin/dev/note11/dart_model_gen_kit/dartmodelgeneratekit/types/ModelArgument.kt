package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil.findPatternOnce

data class ModelArgument(
    val name: String,
    val type: ModelArgumentType,
    val isRequired: Boolean,
    private val defaultValue: String?,
    private val key: String,
) {
    val keys: List<String> = key.split('.').map { it.trim() }

    val actualDefaultValue: String?
        get() = defaultValue?.run {
            if (defaultValue.startsWith("[")) return "const $defaultValue"
            else return defaultValue
        }

    companion object {
        fun parseRawArgument(rawArg: String): ModelArgument? {
            var cleanedCode = rawArg.trim()
            if (cleanedCode.isEmpty()) return null

            val defaultValue = getDefaultValue(cleanedCode)
            if (defaultValue != null) cleanedCode = cleanUpDefaultValue(cleanedCode)

            val customKey = getCustomKeyValue(cleanedCode)
            if (customKey != null) cleanedCode = cleanUpCustomKeyValue(cleanedCode)

            val isSnakeCaseKey = detectSnakeCaseKey(cleanedCode)
            if (isSnakeCaseKey) cleanedCode = cleanUpSnakeCaseKey(cleanedCode)

            val isRequired = detectRequiredKeyword(cleanedCode)
            if (isRequired) {
                cleanedCode = cleanUpRequiredKeyword(cleanedCode)
                if (defaultValue != null) throw Exception("Required argument cannot have default value")
            }

            val argName = getArgName(cleanedCode)
            if (!DartLangParseUtil.isValidClassOrVariable(argName)) throw Exception("Invalid argument name: $argName")
            cleanedCode = cleanUpArgName(cleanedCode, argName)

            val type = getType(cleanedCode)

            val key = customKey
                ?: if (isSnakeCaseKey) changeLowerCamelCaseToSnakeCase(argName)
                else argName

            return ModelArgument(
                argName, type, isRequired, defaultValue, key
            )
        }

        private fun getDefaultValue(input: String): String? {
            val pattern = "@DefaultVal\\((.*?)\\)".toRegex()
            return pattern.findPatternOnce(input)
        }

        private fun cleanUpDefaultValue(input: String): String {
            return input.replace("@DefaultVal\\((.*?)\\)".toRegex(), "").trim()
        }

        private fun getCustomKeyValue(input: String): String? {
            val pattern = "@CustomKey\\(\"(.*?)\"\\)".toRegex()
            return pattern.findPatternOnce(input)
        }

        private fun cleanUpCustomKeyValue(input: String): String {
            return input.replace("@CustomKey\\(\"(.*?)\"\\)".toRegex(), "").trim()
        }

        private fun detectSnakeCaseKey(input: String): Boolean {
            val pattern = "(@SnakeCaseKey\\(\\))|(@snakeKey)".toRegex()
            return pattern.containsMatchIn(input)
        }

        private fun cleanUpSnakeCaseKey(input: String): String {
            return input.replace("(@SnakeCaseKey\\(\\))|(@snakeKey)".toRegex(), "").trim()
        }

        private fun detectRequiredKeyword(input: String): Boolean {
            return input.startsWith("required ")
        }

        private fun cleanUpRequiredKeyword(input: String): String {
            return input.replace("required ", "").trim()
        }

        private fun getArgName(input: String): String {
            return input.split(' ').lastOrNull() ?: throw Exception("Not found argument name")
        }

        private fun cleanUpArgName(input: String, argName: String): String {
            return input.replace(argName, "").trim()
        }

        private fun getType(input: String): ModelArgumentType {
            return ModelArgumentType.parse(input.trim())
        }

        private fun changeLowerCamelCaseToSnakeCase(str: String): String {
            return str.fold(StringBuilder()) { acc, char ->
                if (char.isUpperCase()) {
                    if (acc.isNotEmpty()) acc.append('_')
                    acc.append(char.lowercaseChar())
                } else {
                    acc.append(char)
                }
            }.toString()
        }
    }
}
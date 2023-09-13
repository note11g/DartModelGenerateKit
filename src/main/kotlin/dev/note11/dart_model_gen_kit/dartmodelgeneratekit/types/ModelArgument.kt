package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil.findPatternOnce

data class ModelArgument(
    val name: String,
    val type: ModelArgumentType,
    val isRequired: Boolean,
    val defaultValue: String?,
    val key: String
) {
    companion object {
        fun parseRawArgument(rawArg: String): ModelArgument? {
            var cleanedCode = rawArg.trim()
            if (cleanedCode.isEmpty()) return null

            val defaultValue = getDefaultValue(cleanedCode)
            if (defaultValue != null) cleanedCode = cleanUpDefaultValue(cleanedCode)

            val customKey = getCustomKeyValue(cleanedCode)
            if (customKey != null) {
                cleanedCode = cleanUpCustomKeyValue(cleanedCode)
                println(customKey)
            }

            val isRequired = detectRequiredKeyword(cleanedCode)
            if (isRequired) {
                cleanedCode = cleanUpRequiredKeyword(cleanedCode)
                if (defaultValue != null) throw Exception("Required argument cannot have default value")
            }

            val type = getType(cleanedCode)
            cleanedCode = cleanUpType(cleanedCode)

            val argName = cleanedCode.trim()
            if (!DartLangParseUtil.isValidClassOrVariable(argName)) throw Exception("Invalid argument name: $argName")

            return ModelArgument(argName, type, isRequired, defaultValue, customKey ?: argName)
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

        private fun detectRequiredKeyword(input: String): Boolean {
            return input.startsWith("required ")
        }

        private fun cleanUpRequiredKeyword(input: String): String {
            return input.replace("required ", "").trim()
        }

        private fun getType(input: String): ModelArgumentType {
            val typeString = input.split(' ').firstOrNull() ?: throw Exception("Not found type")
            return ModelArgumentType.parse(typeString) ?: throw Exception("Unsupported type: $typeString")
        }

        private fun cleanUpType(input: String): String {
            val typeString = input.split(' ').first()
            return input.replace(typeString, "").trim()
        }
    }
}
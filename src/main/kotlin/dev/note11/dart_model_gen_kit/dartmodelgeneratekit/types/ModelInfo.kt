package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil

sealed class ClassInfo {

    data class ModelInfo(val className: String, val args: List<ModelArgument>) : ClassInfo()

    data class EnumInfo(val className: String, val values: List<String>, val jsonValue: String) : ClassInfo()

    companion object {
        fun parseFromCodes(fullCodes: String): ClassInfo? {
            val isEnum = DartLangParseUtil.isEnum(fullCodes)
            val className = DartLangParseUtil.extractClassName(fullCodes, isEnum) ?: return null
            if (isEnum) {
                val values = DartLangParseUtil.findEnumValues(fullCodes, className)
                if (values.isEmpty()) return null
                val enumValProperty = DartLangParseUtil.findEnumValProperty(fullCodes) ?: "name"
                return EnumInfo(className, values, enumValProperty)
            } else {
                val args = DartLangParseUtil.findModelArgs(fullCodes, className)
                if (args.isEmpty()) return null
                return ModelInfo(className, args)
            }
        }
    }
}





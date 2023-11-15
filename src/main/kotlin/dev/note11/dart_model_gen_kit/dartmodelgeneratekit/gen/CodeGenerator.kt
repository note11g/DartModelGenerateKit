package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo

interface CodeGenerator<CI : ClassInfo> {
    fun generate(rawCode: String, classInfo: CI): String {
        val originalModelCode = removeGeneratedCodes(rawCode)
        val generatedCode = additionalCodesGenerate(classInfo)
        return injectGeneratedCodes(originalModelCode, generatedCode)
    }

    fun removeGeneratedCodes(rawCode: String): String

    fun additionalCodesGenerate(classInfo: CI): String

    fun injectGeneratedCodes(genClearedCodes: String, generatedCode: String): String
}
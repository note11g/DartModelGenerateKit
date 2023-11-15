package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.enum_model

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.CodeGenerator
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo

class EnumCodeGeneratorImpl : CodeGenerator<ClassInfo.EnumInfo> {
    override fun removeGeneratedCodes(rawCode: String): String {
        return rawCode.substringBefore(
            AUTO_GEN_COMMENT_SECTION_TEXT,
            missingDelimiterValue = rawCode.substringBeforeLast("}")
        )
    }

    override fun additionalCodesGenerate(classInfo: ClassInfo.EnumInfo): String {
        return AUTO_GEN_COMMENT_SECTION_TEXT + EnumMethodsGenerator.invoke(classInfo)
    }

    override fun injectGeneratedCodes(genClearedCodes: String, generatedCode: String): String {
        return "$genClearedCodes$generatedCode}\n"
    }

    companion object {
        private const val AUTO_GEN_COMMENT_SECTION_TEXT =
            "\n  ///\n  /// ----- Auto Generated Codes -----\n  ///\n"
    }
}
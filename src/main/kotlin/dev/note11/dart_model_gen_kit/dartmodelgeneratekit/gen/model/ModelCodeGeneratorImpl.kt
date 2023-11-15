package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.model

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.CodeGenerator
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo

class ModelCodeGeneratorImpl : CodeGenerator<ClassInfo.ModelInfo> {
    override fun removeGeneratedCodes(rawCode: String): String {
        return rawCode.substringBefore(COMMENT_SECTION_TEXT)
    }

    override fun additionalCodesGenerate(classInfo: ClassInfo.ModelInfo): String {
        return COMMENT_SECTION_TEXT +
                ModelSubClassGenerator.invoke(classInfo) + "\n\n" +
                ModelExtensionGenerator.invoke(classInfo)
    }

    override fun injectGeneratedCodes(genClearedCodes: String, generatedCode: String): String {
        return genClearedCodes + generatedCode
    }

    companion object {
        private const val COMMENT_SECTION_TEXT = "\n///\n" +
                "/// ----- Private Codes -----\n" + "///\n\n"
    }
}
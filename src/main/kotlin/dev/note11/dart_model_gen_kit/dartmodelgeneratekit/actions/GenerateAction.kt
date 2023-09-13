package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runWriteAction
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil.removeCodeComments

class GenerateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val clearedEditorText = removeCodeComments(editor.document.text)

        val modelInfo = parseModelInfo(clearedEditorText) ?: return
        println(modelInfo.toString() + "\n")

        val generatedCodes = commentSectionText +
                ModelSubClassGenerator.invoke(modelInfo) + "\n\n" +
                ModelExtensionGenerator.invoke(modelInfo)

        runWriteAction {
            var originalText = editor.document.text
            if (originalText.contains(commentSectionText)) {
                originalText = originalText.substringBefore(commentSectionText)
            }
            editor.document.setText(originalText + generatedCodes)
        }
    }

    private fun parseModelInfo(fullCodes: String): ModelInfo? {
        val className = DartLangParseUtil.extractClassName(fullCodes) ?: return null
        val args = DartLangParseUtil.findModelArgs(fullCodes, className)
        if (args.isEmpty()) return null
        return ModelInfo(className, args)
    }

    companion object {
        private const val commentSectionText = "\n///\n" +
                "/// ----- Private Codes -----\n" + "///\n\n"
    }
}


package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil.removeCodeComments

class GenerateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val document = e.getData(CommonDataKeys.EDITOR)?.document ?: return
        FileDocumentManager.getInstance().saveDocument(document)
        val clearedEditorText = removeCodeComments(document.text)

        val modelInfo = parseModelInfo(clearedEditorText) ?: return
        println(modelInfo.toString() + "\n")

        val generatedCodes = commentSectionText +
                ModelSubClassGenerator.invoke(modelInfo) + "\n\n" +
                ModelExtensionGenerator.invoke(modelInfo)

        runUndoTransparentWriteAction {
            var originalText = document.text
            if (originalText.contains(commentSectionText)) {
                originalText = originalText.substringBefore(commentSectionText)
            }

            document.setText(originalText + generatedCodes)

            FileDocumentManager.getInstance().saveDocument(document)
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


package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ModelInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil

class GenerateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val document = getDocument(e) ?: return
        saveDocument(document)
        val clearedEditorText = DartLangParseUtil.removeCodeComments(document.text)
        val modelInfo = parseModelInfo(clearedEditorText) ?: return

        val originalCode = originalModelCodeWithoutGeneratedCodes(document.text)
        val generatedCode = generateModelExtendsCode(modelInfo)

        runUndoTransparentWriteAction {
            document.setText(originalCode + generatedCode)
            saveDocument(document)
            openAlert("Model Generated! 😉", e)
        }
    }

    private fun getDocument(e: AnActionEvent): Document? {
        return e.getData(CommonDataKeys.EDITOR)?.document
    }

    private fun saveDocument(document: Document) {
        FileDocumentManager.getInstance().saveDocument(document)
    }

    private fun openAlert(message: String, e: AnActionEvent) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("Dart Model Gen Alert")
            .createNotification(message, NotificationType.INFORMATION)
            .setImportant(false)
        notification.notify(e.project)
    }

    private fun parseModelInfo(fullCodes: String): ModelInfo? {
        val className = DartLangParseUtil.extractClassName(fullCodes) ?: return null
        val args = DartLangParseUtil.findModelArgs(fullCodes, className)
        if (args.isEmpty()) return null
        return ModelInfo(className, args)
    }

    private fun generateModelExtendsCode(modelInfo: ModelInfo): String {
        return commentSectionText +
                ModelSubClassGenerator.invoke(modelInfo) + "\n\n" +
                ModelExtensionGenerator.invoke(modelInfo)
    }

    private fun originalModelCodeWithoutGeneratedCodes(fullCodes: String): String {
        if (alreadyCodeGenerated(fullCodes)) return removeGeneratedCodes(fullCodes)
        return fullCodes
    }

    private fun alreadyCodeGenerated(fullCodes: String): Boolean {
        return fullCodes.contains(commentSectionText)
    }

    private fun removeGeneratedCodes(fullCodes: String): String {
        return fullCodes.substringBefore(commentSectionText)
    }

    companion object {
        private const val commentSectionText = "\n///\n" +
                "/// ----- Private Codes -----\n" + "///\n\n"
    }
}


package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.enum_model.EnumCodeGeneratorImpl
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.model.ModelCodeGeneratorImpl
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.model.ModelExtensionGenerator
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.gen.model.ModelSubClassGenerator
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types.ClassInfo
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil

class GenerateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val document = getDocument(e) ?: return
        saveDocument(document)

        val resultCode: String
        try {
            val clearedEditorText = DartLangParseUtil.removeCodeComments(document.text)
            val modelInfo = ClassInfo.parseFromCodes(clearedEditorText) ?: return

            resultCode = when (modelInfo) {
                is ClassInfo.ModelInfo -> ModelCodeGeneratorImpl().generate(document.text, modelInfo)
                is ClassInfo.EnumInfo -> EnumCodeGeneratorImpl().generate(document.text, modelInfo)
            }
        } catch (ex: Exception) {
            e.openAlert("Model Generation Failed! : (${ex.message})")
            return
        }

        runUndoTransparentWriteAction {
            document.setText(resultCode)
            saveDocument(document)
            e.openAlert("Model Generated! 😉")
        }
    }

    private fun getDocument(e: AnActionEvent): Document? {
        return e.getData(CommonDataKeys.EDITOR)?.document
    }

    private fun saveDocument(document: Document) {
        FileDocumentManager.getInstance().saveDocument(document)
    }

    private fun AnActionEvent.openAlert(message: String) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("Dart Model Gen Alert")
            .createNotification(message, NotificationType.INFORMATION)
            .setImportant(false)
        notification.notify(this.project)
    }
}


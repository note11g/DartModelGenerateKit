package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.livetemplate

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class DartModelGenLiveTemplateContext: TemplateContextType("DartModelGen") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return templateActionContext.file.name.endsWith(".dart")
    }
}
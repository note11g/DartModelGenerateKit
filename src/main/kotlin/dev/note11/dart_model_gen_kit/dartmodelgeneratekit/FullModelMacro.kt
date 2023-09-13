package dev.note11.dart_model_gen_kit.dartmodelgeneratekit

import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.Result
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.TextResult
import com.intellij.codeInsight.template.macro.MacroBase

class FullModelMacro: MacroBase("fullModel", "fullModel()") {
    override fun calculateResult(params: Array<out Expression>, context: ExpressionContext?, quick: Boolean): Result? {
        println(context!!.editor!!.document.text)
        return TextResult("Test")
    }

    override fun isAcceptableInContext(context: TemplateContextType?): Boolean {
        println(context?.presentableName)
        return super.isAcceptableInContext(context)
    }
}
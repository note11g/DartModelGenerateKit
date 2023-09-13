package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util

fun String.addTabIndentation(tabCount: Int = 1): String {
    return split('\n').joinToString("\n") { "\t".repeat(tabCount) + it }
}
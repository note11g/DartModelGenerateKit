package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil

sealed class ModelArgumentType {
    private var _nullable: Boolean = false
    private val nullable: Boolean get() = _nullable

    private lateinit var _rawDartTypeString: String
    val rawDartTypeString: String get() = _rawDartTypeString

    val dartTypeWithOutNullable: String get() = rawDartTypeString.removeSuffix("?")

    val dartTypeOnFromPatternMatching
        get() = when (this) {
            is DString -> "String"
            is DInt -> "int"
            is DDouble -> "double"
            is DBool -> "bool"
            is DDateTime -> "int"
            is DCustom -> "Object"
            is DList ->
                if (valueType is NeedParseModelArgumentType) "List"
                else "List<${valueType.rawDartTypeString}>"
            is DMap ->
                if (valueType is NeedParseModelArgumentType) "Map"
                else "Map<${keyType.rawDartTypeString}, ${valueType.rawDartTypeString}>"
        } + if (nullable) "?" else ""

    fun getFromJsonConstructorParam(name: String): String = when (this) {
        !is NeedParseModelArgumentType -> name
        is DDateTime -> "DateTime.fromMillisecondsSinceEpoch($name)"
        is DCustom -> "${typeStr}.fromJson($name)"
        is DList ->
            if (valueType !is NeedParseModelArgumentType) name
            else "$name.map((e) => ${valueType.getFromJsonConstructorParam("e")}).toList()"

        is DMap ->
            if (valueType !is NeedParseModelArgumentType) name
            else "Map.fromEntries($name.entries.map((e) => ${valueType.getFromJsonConstructorParam("e")}))"
    }

    fun getToJsonValue(name: String): String = when (this) {
        !is NeedParseModelArgumentType -> name
        is DDateTime -> "$name.millisecondsSinceEpoch"
        is DCustom -> "$name.toJson()"
        is DList ->
            if (valueType !is NeedParseModelArgumentType) name
            else "$name.map((e) => ${valueType.getToJsonValue("e")}).toList()"

        is DMap ->
            if (valueType !is NeedParseModelArgumentType) name
            else "Map.fromEntries($name.map((k, v) => MapEntry(k, ${valueType.getToJsonValue("v")})))"
    }

    class DString : ModelArgumentType()

    class DInt : ModelArgumentType()

    class DDouble : ModelArgumentType()

    class DBool : ModelArgumentType()

    sealed class NeedParseModelArgumentType : ModelArgumentType()

    class DDateTime : NeedParseModelArgumentType()

    class DList(val valueType: ModelArgumentType) : NeedParseModelArgumentType()
    class DMap(val keyType: ModelArgumentType, val valueType: ModelArgumentType) : NeedParseModelArgumentType()
    class DCustom(val typeStr: String) : NeedParseModelArgumentType()

    companion object {
        fun parse(typeString: String): ModelArgumentType? {
            val typeString = typeString.trim()
            val nullable = typeString.endsWith('?')
            return when (typeString.removeSuffix("?")) {
                "String" -> DString()
                "int" -> DInt()
                "double" -> DDouble()
                "bool" -> DBool()
                "DateTime" -> DDateTime()
                else -> {
                    if (typeString.startsWith("List<")) {
                        val valueType = parse(typeString.removePrefix("List<").removeSuffix(">")) ?: return null
                        DList(valueType)
                    } else if (typeString.startsWith("Map<")) {
                        val innerTypes = typeString.removePrefix("Map<").removeSuffix(">").split(',')
                        val keyType = parse(innerTypes[0])
                        val valueType = parse(innerTypes[1])
                        if (keyType == null || valueType == null) return null
                        DMap(keyType, valueType)
                    } else if (!DartLangParseUtil.isValidClassOrVariable(typeString)) {
                        // unsupported type
                        return null
                    } else {
                        DCustom(typeString)
                    }
                }
            }.apply {
                this._rawDartTypeString = typeString
                if (nullable) this._nullable = true
            }
        }
    }

    override fun toString(): String {
        return this.javaClass.name.split('$').last() + if (nullable) "?" else ""
    }
}

package dev.note11.dart_model_gen_kit.dartmodelgeneratekit.types

import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil
import dev.note11.dart_model_gen_kit.dartmodelgeneratekit.util.DartLangParseUtil.parseGenericType

sealed class ModelArgumentType {
    val nullable: Boolean get() = _nullable
    val typeString: String get() = _typeStringWithNullable
    val dartTypeWithOutNullable: String get() = typeString.removeSuffix("?")
    val dartTypeOnFromPatternMatching
        get() = when (this) {
            is DString -> "String"
            is DInt -> "int"
            is DDouble -> "double"
            is DBool -> "bool"
            is DDateTime -> "int"
            is DCustom -> "Object"
            is DList -> {
                if (valueType is NeedParseModelArgumentType) "List"
                else "List<${valueType.typeString}>"
            }

            is DMap -> {
                assert(keyType !is NeedParseModelArgumentType)
                if (valueType is NeedParseModelArgumentType) "Map<${keyType.typeString}, dynamic>"
                else "Map<${keyType.typeString}, ${valueType.typeString}>"
            }
        } + if (nullable) "?" else ""

    private lateinit var _typeStringWithNullable: String
    private var _nullable: Boolean = false

    fun getFromJsonConstructorParam(name: String): String {
        return when (this) {
            !is NeedParseModelArgumentType -> name
            is DDateTime -> nullCheckExpressionCreator(name, "DateTime.fromMillisecondsSinceEpoch($name)")
            is DCustom -> nullCheckExpressionCreator(name, "${typeStr}.fromJson($name)")
            is DList -> {
                if (valueType !is NeedParseModelArgumentType) name
                else {
                    val nameWithNullOperator = if (nullable) "$name?" else name
                    "$nameWithNullOperator.map((e) => ${valueType.getFromJsonConstructorParam("e")}).toList()"
                }
            }

            is DMap -> {
                if (valueType !is NeedParseModelArgumentType) name
                else nullCheckExpressionCreator(
                    name, "Map.fromEntries($name.map((k, v)" +
                            " => MapEntry(k, ${valueType.getFromJsonConstructorParam("v")})).entries)"
                )
            }
        }
    }

    fun getToJsonValue(name: String): String {
        val nameWithNullOperator = if (nullable) "$name?" else name
        return when (this) {
            !is NeedParseModelArgumentType -> name
            is DDateTime -> "$nameWithNullOperator.millisecondsSinceEpoch"
            is DCustom -> "$nameWithNullOperator.toJson()"
            is DList -> {
                if (valueType !is NeedParseModelArgumentType) name
                else "$nameWithNullOperator.map((e) => ${valueType.getToJsonValue("e")}).toList()"
            }

            is DMap -> {
                if (valueType !is NeedParseModelArgumentType) name
                else nullCheckExpressionCreator(
                    name,
                    "Map.fromEntries($name.map((k, v) => " + "MapEntry(k, ${valueType.getToJsonValue("v")})).entries)"
                )
            }
        }
    }

    private fun nullCheckExpressionCreator(arg: String, parser: String): String {
        return if (nullable) "$arg != null ? $parser : null" else parser
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
        fun parse(rawTypeString: String): ModelArgumentType {
            val trimmedTypeString = rawTypeString.trim()
            val nullable = trimmedTypeString.endsWith('?')
            val typeStringWithoutNull = rawTypeString.removeSuffix("?")
            return when (typeStringWithoutNull) {
                "String" -> DString()
                "int" -> DInt()
                "double" -> DDouble()
                "bool" -> DBool()
                "DateTime" -> DDateTime()
                else -> with(typeStringWithoutNull) {
                    if (startsWith("List<")) {
                        DList(valueType = parse(parseGenericType(this).first()))
                    } else if (startsWith("Map<")) {
                        val innerTypes = parseGenericType(this)
                        DMap(keyType = parse(innerTypes[0]), valueType = parse(innerTypes[1]))
                    } else if (DartLangParseUtil.isValidClassOrVariable(this)) {
                        DCustom(this)
                    } else {
                        throw Exception("Unsupported type: $trimmedTypeString")
                    }
                }
            }.apply {
                this._typeStringWithNullable = trimmedTypeString
                if (nullable) this._nullable = true
            }
        }
    }

    override fun toString(): String {
        return this.javaClass.name.split('$').last() + if (nullable) "?" else ""
    }
}

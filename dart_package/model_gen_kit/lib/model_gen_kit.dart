library model_gen_kit;

class ParseFailedException implements Exception {
  final String modelName;

  const ParseFailedException(this.modelName);

  @override
  String toString() {
    return 'ParseFailedException($modelName is not matched from json data)';
  }
}

class CustomKey {
  const CustomKey(String key);
}

/// compatibility with snake_case keys on json
///
/// now, support `lowerCamelCase` -> `snake_case` only.
///
/// e.g.
///
/// ```
/// @SnakeCaseKey()
/// required int userId
///   gen --> json.user_id
/// ```
///
/// or using shortVer: `@snakeKey` instead.
///
/// added on `model_gen_kit 0.1.2`
class SnakeCaseKey {
  const SnakeCaseKey();
}

/// compatibility with snake_case keys on json
///
/// short version of `@SnakeCaseKey()`
///
/// added on `model_gen_kit 0.1.2`
const SnakeCaseKey snakeKey = SnakeCaseKey();
class DefaultVal {
  const DefaultVal(dynamic value);
}

class EnumVal {
  const EnumVal();
}

const EnumVal enumVal = EnumVal();

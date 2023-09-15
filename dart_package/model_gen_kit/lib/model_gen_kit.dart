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

class DefaultVal {
  const DefaultVal(dynamic value);
}

import 'package:model_gen_kit/model_gen_kit.dart';

void main() {
  final user = User(
    id: 1,
    name: "John",
  );

  print(user);
  print(user.toJson());
  print(User.fromJson(user.toJson()));

  final dynamic errorUserJson = {
    "uid": 1,
    "name": "John",
    "email": 34567,
  };

  print(User.fromJson(errorUserJson));
}

///
/// 1. On IntelliJ(or Android Studio), Install Plugin "Dart Model Generate Kit" : upload soon..
/// 2. Create empty dart file and write "gm" and press enter.
/// 3. Write your model class name and arguments.
/// 4. option + shift + enter (or alt + shift + enter) to generate model class.
///
class User with _UserExtension {
  const factory User({
    @CustomKey("uid") required int id,
    required String name,
    @DefaultVal("example@gmail.com") String email,
  }) = _User;

  const User._();

  factory User.fromJson(json) = _User.fromJson;
}

///
/// ----- Private Codes -----
///

class _User extends User {
  @override
  final int id;
  @override
  final String name;
  @override
  final String email;

  const _User({
    required this.id,
    required this.name,
    this.email = "example@gmail.com",
  }) : super._();

  factory _User.fromJson(json) => _UserExtension.createModelFromJson(json);
}

mixin _UserExtension {
  int get id => throw UnimplementedError();
  String get name => throw UnimplementedError();
  String get email => throw UnimplementedError();

  static _User createModelFromJson(Object json) {
    if (json
        case {
          "uid": int id,
          "name": String name,
          "email": String email,
        }) {
      return _User(
        id: id,
        name: name,
        email: email,
      );
    } else {
      throw ParseFailedException("User", json);
    }
  }

  Map<String, dynamic> toJson() => {
        "uid": id,
        "name": name,
        "email": email,
      };

  User copyWith({
    int? id,
    String? name,
    String? email,
  }) =>
      User(
        id: id ?? this.id,
        name: name ?? this.name,
        email: email ?? this.email,
      );

  @override
  String toString() => 'User{id: $id, name: $name, email: $email}';
}

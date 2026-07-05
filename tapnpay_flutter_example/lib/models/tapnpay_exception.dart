import 'package:flutter/services.dart';

/// Thrown when a `connect` or `sendTransaction` call fails.
class TapNPayException implements Exception {
  const TapNPayException(this.errorCode, this.message, this.errorCodeValue);

  final String errorCode;
  final String? message;
  final Object? errorCodeValue;

  factory TapNPayException.from(PlatformException exception) {
    return TapNPayException(exception.code, exception.message, exception.details);
  }

  @override
  String toString() {
    final suffix = errorCodeValue != null ? ' [$errorCodeValue]' : '';
    return '$errorCode$suffix${message != null ? '\n$message' : ''}';
  }
}

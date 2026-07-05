import 'tapnpay_connection_status.dart';

/// A connection status change streamed from the native bridge's `EventChannel`.
class TapNPayConnectionEvent {
  const TapNPayConnectionEvent(this.status, {this.errorCode, this.message});

  final TapNPayConnectionStatus status;
  final String? errorCode;
  final String? message;

  static TapNPayConnectionEvent fromMap(Map<Object?, Object?> map) {
    final status = switch (map['status']) {
      'connected' => TapNPayConnectionStatus.connected,
      'failed' => TapNPayConnectionStatus.failed,
      _ => TapNPayConnectionStatus.disconnected,
    };
    return TapNPayConnectionEvent(
      status,
      errorCode: map['errorCode'] as String?,
      message: map['message'] as String?,
    );
  }
}

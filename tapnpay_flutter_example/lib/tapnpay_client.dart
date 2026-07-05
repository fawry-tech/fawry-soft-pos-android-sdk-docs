import 'package:flutter/services.dart';

import 'models/models.dart';

/// Dart-side wrapper around the native `TapNPayBridge` platform channels.
///
/// This mirrors `FawrySdkClient` from `example/sdk-integration` so the same
/// demo flow (connect, send a transaction, disconnect) reads the same way
/// across every sample in this repository.
class TapNPayClient {
  TapNPayClient({
    MethodChannel? methodChannel,
    EventChannel? eventChannel,
  })  : _methodChannel = methodChannel ??
            const MethodChannel('com.fawry.softpos.tapnpay_flutter_example/sdk'),
        _eventChannel = eventChannel ??
            const EventChannel(
              'com.fawry.softpos.tapnpay_flutter_example/sdk/connectionEvents',
            );

  final MethodChannel _methodChannel;
  final EventChannel _eventChannel;

  Stream<TapNPayConnectionEvent>? _connectionEvents;

  /// Streams connection status changes, including spontaneous disconnects
  /// that are not tied to a specific [connect] or [disconnect] call.
  Stream<TapNPayConnectionEvent> get connectionEvents {
    return _connectionEvents ??= _eventChannel
        .receiveBroadcastStream()
        .map((event) => TapNPayConnectionEvent.fromMap(event as Map<Object?, Object?>));
  }

  Future<void> connect(FawryCredentials credentials) async {
    try {
      await _methodChannel.invokeMethod<void>('connect', {
        'isPartnerMode': credentials.isPartnerMode,
        'partnerCode': credentials.partnerCode,
      });
    } on PlatformException catch (e) {
      throw TapNPayException.from(e);
    }
  }

  Future<void> disconnect() async {
    await _methodChannel.invokeMethod<void>('disconnect');
  }

  Future<bool> isConnected() async {
    final connected = await _methodChannel.invokeMethod<bool>('isConnected');
    return connected ?? false;
  }

  /// Sends a transaction and returns the formatted response text on success,
  /// or throws a [TapNPayException] on failure.
  Future<String> sendTransaction(
    FawryOperation operation,
    FawryTransactionRequest request,
  ) async {
    try {
      final response = await _methodChannel.invokeMethod<String>('sendTransaction', {
        'operation': operation.nativeName,
        'amount': request.amount,
        'btc': request.btc,
        'orderId': request.orderId,
        'merchantToken': request.merchantToken,
        'merchantAccountNumber': request.merchantAccountNumber,
        'referenceNumber': request.referenceNumber,
      });
      return response ?? '';
    } on PlatformException catch (e) {
      throw TapNPayException.from(e);
    }
  }
}

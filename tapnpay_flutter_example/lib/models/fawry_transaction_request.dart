/// Transaction input collected by the demo UI, mirroring
/// `FawryTransactionRequest` in `example/sdk-integration`.
class FawryTransactionRequest {
  const FawryTransactionRequest({
    required this.amount,
    required this.btc,
    required this.orderId,
    required this.merchantToken,
    required this.merchantAccountNumber,
    this.referenceNumber = '',
  });

  final String amount;
  final String btc;
  final String orderId;
  final String merchantToken;
  final String merchantAccountNumber;
  final String referenceNumber;
}

/// Operations supported by this demo, mirroring `FawryOperation` in
/// `example/sdk-integration` on the native Android side.
enum FawryOperation {
  cardSale('Card Sale', 'Pay Now', false),
  cardRefund('Card Refund', 'Refund', true),
  cardVoid('Card Void', 'Void', true),
  inquiry('Inquiry', 'Run Inquiry', true),
  clearCache('Clear Cache', 'Clear Cache', false);

  const FawryOperation(this.label, this.actionLabel, this.needsReference);

  final String label;
  final String actionLabel;
  final bool needsReference;

  /// Matches the Kotlin enum constant name (e.g. `CARD_SALE`) expected by
  /// `TapNPayBridge` on the native side.
  String get nativeName => switch (this) {
        FawryOperation.cardSale => 'CARD_SALE',
        FawryOperation.cardRefund => 'CARD_REFUND',
        FawryOperation.cardVoid => 'CARD_VOID',
        FawryOperation.inquiry => 'INQUIRY',
        FawryOperation.clearCache => 'CLEAR_CACHE',
      };
}

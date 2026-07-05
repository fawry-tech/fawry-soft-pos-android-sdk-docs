/// Credentials required to establish the TapNPay IPC connection.
class FawryCredentials {
  const FawryCredentials({required this.isPartnerMode, this.partnerCode = ''});

  final bool isPartnerMode;
  final String partnerCode;
}

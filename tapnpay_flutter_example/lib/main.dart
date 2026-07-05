import 'package:flutter/material.dart';

import 'screens/tapnpay_demo_page.dart';

void main() {
  runApp(const TapNPayExampleApp());
}

class TapNPayExampleApp extends StatelessWidget {
  const TapNPayExampleApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fawry TapNPay Example',
      theme: ThemeData(colorSchemeSeed: const Color(0xFF0066CC), useMaterial3: true),
      home: const TapNPayDemoPage(),
    );
  }
}

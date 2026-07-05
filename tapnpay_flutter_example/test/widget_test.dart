import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:tapnpay_flutter_example/main.dart';

void main() {
  testWidgets('Demo screen renders connection, credentials, operation, and response cards',
      (WidgetTester tester) async {
    await tester.pumpWidget(const TapNPayExampleApp());

    expect(find.text('Fawry TapNPay Example (Flutter)'), findsOneWidget);
    expect(find.text('Connection'), findsOneWidget);
    expect(find.text('Credentials'), findsOneWidget);
    // Appears twice: the card title and the dropdown's input label.
    expect(find.text('Operation'), findsWidgets);
    expect(find.text('Response'), findsOneWidget);
    expect(find.widgetWithText(FilledButton, 'Connect'), findsOneWidget);
  });
}

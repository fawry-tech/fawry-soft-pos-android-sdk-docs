import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';

import '../models/models.dart';
import '../tapnpay_client.dart';
import '../widgets/demo_card.dart';

/// Demo screen mirroring the Android samples: enter credentials, connect to
/// TapNPay, pick an operation, and view the formatted response.
class TapNPayDemoPage extends StatefulWidget {
  const TapNPayDemoPage({super.key});

  @override
  State<TapNPayDemoPage> createState() => _TapNPayDemoPageState();
}

class _TapNPayDemoPageState extends State<TapNPayDemoPage> {
  final _client = TapNPayClient();
  StreamSubscription<TapNPayConnectionEvent>? _connectionSubscription;

  bool _isPartnerMode = true;
  final _partnerCodeController = TextEditingController(text: '100');
  final _merchantTokenController = TextEditingController();
  final _merchantAccountController = TextEditingController(text: 'ACCOUNT_NUMBER');

  FawryOperation _selectedOperation = FawryOperation.cardSale;
  final _amountController = TextEditingController(text: '10.00');
  final _btcController = TextEditingController(text: '99901');
  final _orderIdController = TextEditingController();
  final _referenceController = TextEditingController();

  String _connectionStatus = 'Disconnected';
  bool _isConnected = false;
  bool _isConnecting = false;
  bool _isSending = false;
  String _responseText = 'Response will appear here...';

  @override
  void initState() {
    super.initState();
    _generateOrderId();
    _connectionSubscription = _client.connectionEvents.listen(_onConnectionEvent);
  }

  @override
  void dispose() {
    _connectionSubscription?.cancel();
    _partnerCodeController.dispose();
    _merchantTokenController.dispose();
    _merchantAccountController.dispose();
    _amountController.dispose();
    _btcController.dispose();
    _orderIdController.dispose();
    _referenceController.dispose();
    super.dispose();
  }

  void _onConnectionEvent(TapNPayConnectionEvent event) {
    setState(() {
      switch (event.status) {
        case TapNPayConnectionStatus.connected:
          _connectionStatus = 'Connected';
          _isConnected = true;
          _isConnecting = false;
        case TapNPayConnectionStatus.disconnected:
          _connectionStatus = 'Disconnected';
          _isConnected = false;
          _isConnecting = false;
        case TapNPayConnectionStatus.failed:
          _connectionStatus = 'Failed: ${event.errorCode}';
          _isConnected = false;
          _isConnecting = false;
          _responseText = '${event.errorCode}\n${event.message ?? ''}';
      }
    });
  }

  void _generateOrderId() {
    final random = Random();
    final bytes = List<int>.generate(16, (_) => random.nextInt(256));
    bytes[6] = (bytes[6] & 0x0F) | 0x40;
    bytes[8] = (bytes[8] & 0x3F) | 0x80;
    final hex = bytes.map((b) => b.toRadixString(16).padLeft(2, '0')).join();
    final orderId =
        '${hex.substring(0, 8)}-${hex.substring(8, 12)}-${hex.substring(12, 16)}-${hex.substring(16, 20)}-${hex.substring(20)}';
    setState(() => _orderIdController.text = orderId);
  }

  Future<void> _connect() async {
    setState(() {
      _connectionStatus = 'Connecting...';
      _isConnecting = true;
      _responseText = '';
    });

    try {
      await _client.connect(
        FawryCredentials(
          isPartnerMode: _isPartnerMode,
          partnerCode: _partnerCodeController.text.trim(),
        ),
      );
      if (!mounted) return;
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Connected to TapNPay')));
    } on TapNPayException catch (e) {
      if (!mounted) return;
      setState(() {
        _connectionStatus = 'Failed: ${e.errorCode}';
        _isConnecting = false;
        _responseText = e.toString();
      });
    }
  }

  Future<void> _disconnect() async {
    await _client.disconnect();
  }

  Future<void> _sendTransaction() async {
    if (!await _client.isConnected()) {
      if (!mounted) return;
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Connect to TapNPay first')));
      return;
    }

    setState(() {
      _isSending = true;
      _responseText = 'Processing...';
    });

    try {
      final response = await _client.sendTransaction(
        _selectedOperation,
        FawryTransactionRequest(
          amount: _amountController.text,
          btc: _btcController.text,
          orderId: _orderIdController.text,
          merchantToken: _merchantTokenController.text.trim(),
          merchantAccountNumber: _merchantAccountController.text.trim(),
          referenceNumber: _referenceController.text.trim(),
        ),
      );
      if (!mounted) return;
      setState(() => _responseText = response);
    } on TapNPayException catch (e) {
      if (!mounted) return;
      setState(() => _responseText = e.toString());
    } finally {
      if (mounted) setState(() => _isSending = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Fawry TapNPay Example (Flutter)')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _buildConnectionCard(),
            const SizedBox(height: 12),
            _buildCredentialsCard(),
            const SizedBox(height: 12),
            _buildOperationCard(),
            const SizedBox(height: 12),
            _buildResponseCard(),
          ],
        ),
      ),
    );
  }

  Widget _buildConnectionCard() {
    return DemoCard(
      title: 'Connection',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('Connection: $_connectionStatus'),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: FilledButton(
                  onPressed: (_isConnected || _isConnecting) ? null : _connect,
                  child: const Text('Connect'),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: OutlinedButton(
                  onPressed: (_isConnected || _isConnecting) ? _disconnect : null,
                  child: const Text('Disconnect'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCredentialsCard() {
    return DemoCard(
      title: 'Credentials',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          RadioGroup<bool>(
            groupValue: _isPartnerMode,
            onChanged: (value) => setState(() => _isPartnerMode = value!),
            child: const Row(
              children: [
                Expanded(
                  child: RadioListTile<bool>(
                    contentPadding: EdgeInsets.zero,
                    title: Text('Partner Code'),
                    value: true,
                  ),
                ),
                Expanded(
                  child: RadioListTile<bool>(
                    contentPadding: EdgeInsets.zero,
                    title: Text('Merchant Token'),
                    value: false,
                  ),
                ),
              ],
            ),
          ),
          TextField(
            controller: _partnerCodeController,
            decoration: const InputDecoration(labelText: 'Partner Code'),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _merchantTokenController,
            decoration: const InputDecoration(labelText: 'Merchant Token'),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _merchantAccountController,
            decoration: const InputDecoration(labelText: 'Account Number'),
          ),
        ],
      ),
    );
  }

  Widget _buildOperationCard() {
    return DemoCard(
      title: 'Operation',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          DropdownButtonFormField<FawryOperation>(
            initialValue: _selectedOperation,
            decoration: const InputDecoration(labelText: 'Operation'),
            items: FawryOperation.values
                .map((op) => DropdownMenuItem(value: op, child: Text(op.label)))
                .toList(),
            onChanged: (value) => setState(() => _selectedOperation = value!),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _amountController,
            decoration: const InputDecoration(labelText: 'Amount'),
            keyboardType: TextInputType.number,
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _btcController,
            decoration: const InputDecoration(labelText: 'Bill Type Code (BTC)'),
            keyboardType: TextInputType.number,
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _orderIdController,
                  decoration: const InputDecoration(labelText: 'Order ID'),
                ),
              ),
              TextButton(onPressed: _generateOrderId, child: const Text('New')),
            ],
          ),
          if (_selectedOperation.needsReference) ...[
            const SizedBox(height: 8),
            TextField(
              controller: _referenceController,
              decoration: const InputDecoration(labelText: 'Reference Number (FCRN)'),
            ),
          ],
          const SizedBox(height: 16),
          FilledButton(
            onPressed: _isSending ? null : _sendTransaction,
            child: Text(_selectedOperation.actionLabel),
          ),
        ],
      ),
    );
  }

  Widget _buildResponseCard() {
    return DemoCard(
      title: 'Response',
      child: Container(
        width: double.infinity,
        height: 200,
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFFEEEEEE),
          borderRadius: BorderRadius.circular(4),
        ),
        child: SingleChildScrollView(
          child: SelectableText(
            _responseText,
            style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
          ),
        ),
      ),
    );
  }
}

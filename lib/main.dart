import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  static const platform = MethodChannel('scrcpy_channel');

  const MyApp({super.key});

  Future<void> _startScrcpy() async {
    try {
      final result = await platform.invokeMethod('startScrcpy', {
        'serverAdr': '192.168.1.100', // Substitua pelo IP do dispositivo remoto
        'videoBitrate': 8000000,
        'maxHeight': 1920,
      });
      print(result);
    } catch (e) {
      print("Erro ao iniciar Scrcpy: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text("App Remote")),
        body: Center(
          child: ElevatedButton(
            onPressed: _startScrcpy,
            child: const Text("Iniciar Scrcpy"),
          ),
        ),
      ),
    );
  }
}

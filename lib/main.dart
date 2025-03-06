import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('scrcpy_channel');
  int? textureId;

  Future<void> _startScrcpy() async {
    try {
      final result = await platform.invokeMethod('startScrcpy', {
        'serverAdr': '192.168.0.3', // Substitua pelo IP real
        'videoBitrate': 8000000,
        'maxHeight': 1920,
      });
      setState(() {
        textureId = result;
      });
      print("Texture ID: $textureId");
    } catch (e) {
      print("Erro ao iniciar Scrcpy: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(title: const Text("App Remote")),
        body: Column(
          children: [
            if (textureId != null)
              Expanded(
                child: Texture(textureId: textureId!),
              ),
            ElevatedButton(
              onPressed: _startScrcpy,
              child: const Text("Iniciar Scrcpy"),
            ),
          ],
        ),
      ),
    );
  }
}

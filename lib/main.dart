import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('scrcpy_channel');
  int? textureId;

  Future<void> _startScrcpy() async {
    try {
      final result = await platform.invokeMethod('startScrcpy', {
        'serverAdr': '192.168.1.100', // Substitua pelo IP real
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
      home: Scaffold(
        appBar: AppBar(title: Text("App Remote")),
        body: Column(
          children: [
            if (textureId != null)
              Expanded(
                child: Texture(textureId: textureId!),
              ),
            ElevatedButton(
              onPressed: _startScrcpy,
              child: Text("Iniciar Scrcpy"),
            ),
          ],
        ),
      ),
    );
  }
}

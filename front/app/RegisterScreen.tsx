import React, { useState, useEffect } from 'react';
import { View, TextInput, Button, Alert, Text, NativeModules } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AccessibilityInfo } from 'react-native';

export default function RegisterScreen({ navigation }) {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [document, setDocument] = useState('');
  const [email, setEmail] = useState('');
  const [isRegistered, setIsRegistered] = useState(false);
  const [isScreenReaderEnabled, setIsScreenReaderEnabled] = useState(false);

  useEffect(() => {
    // Verifica si hay datos guardados
    AsyncStorage.getItem('user_data').then(data => {
      if (data) setIsRegistered(true);
    });
    // Verifica si el lector de pantalla está activo (no es lo mismo que accesibilidad personalizada, pero es lo que permite RN)
    AccessibilityInfo.isScreenReaderEnabled().then(setIsScreenReaderEnabled);
  }, []);

  const handleRegister = async () => {
    if (!name || !phone || !document || !email) {
      Alert.alert('Completa todos los campos');
      return;
    }
    // Guarda los datos en AsyncStorage
    await AsyncStorage.setItem('user_data', JSON.stringify({ name, phone, document, email }));
    try {
      await NativeModules.UserDataBridge.saveUserData(name, phone, document, email);
      Alert.alert('Registro exitoso');
      setIsRegistered(true);
      navigation.goBack();
    } catch (e) {
      Alert.alert('Error al guardar los datos');
    }
  };

  const openAccessibilitySettings = () => {
    if (NativeModules.AccessibilityIntent && NativeModules.AccessibilityIntent.openAccessibilitySettings) {
      NativeModules.AccessibilityIntent.openAccessibilitySettings();
    } else {
      Alert.alert('No disponible', 'No se puede abrir la configuración de accesibilidad.');
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <Text style={{ marginBottom: 10, fontWeight: 'bold' }}>
        Estado de registro: {isRegistered ? 'Registrado' : 'No registrado'}
      </Text>
      <Text style={{ marginBottom: 10 }}>
        Lector de pantalla activo: {isScreenReaderEnabled ? 'Sí' : 'No'}
      </Text>
      <Button title="Abrir configuración de accesibilidad" onPress={openAccessibilitySettings} />
      <View style={{ height: 20 }} />
      <TextInput placeholder="Nombre" value={name} onChangeText={setName} style={{ marginBottom: 10 }} />
      <TextInput placeholder="Teléfono" value={phone} onChangeText={setPhone} style={{ marginBottom: 10 }} />
      <TextInput placeholder="Documento" value={document} onChangeText={setDocument} style={{ marginBottom: 10 }} />
      <TextInput placeholder="Email" value={email} onChangeText={setEmail} style={{ marginBottom: 10 }} />
      <Button title="Registrar" onPress={handleRegister} />
    </View>
  );
}
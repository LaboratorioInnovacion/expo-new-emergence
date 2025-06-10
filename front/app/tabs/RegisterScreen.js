import React, { useState } from 'react';
import { View, TextInput, Button, Alert,NativeModules } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';


export default function RegisterScreen({ navigation }) {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [document, setDocument] = useState('');
  const [email, setEmail] = useState('');

  const handleRegister = async () => {
    if (!name || !phone || !document || !email) {
      Alert.alert('Completa todos los campos');
      return;
    }
    // Guarda los datos en AsyncStorage
    await AsyncStorage.setItem('user_data', JSON.stringify({ name, phone, document, email }));
    await NativeModules.UserDataBridge.saveUserData(name, phone, document, email);
    Alert.alert('Registro exitoso');
    navigation.goBack();
  };

  return (
    <View style={{ padding: 20 }}>
      <TextInput placeholder="Nombre" value={name} onChangeText={setName} style={{ marginBottom: 10 }} />
      <TextInput placeholder="Teléfono" value={phone} onChangeText={setPhone} style={{ marginBottom: 10 }} />
      <TextInput placeholder="Documento" value={document} onChangeText={setDocument} style={{ marginBottom: 10 }} />
      <TextInput placeholder="Email" value={email} onChangeText={setEmail} style={{ marginBottom: 10 }} />
      <Button title="Registrar" onPress={handleRegister} />
    </View>
  );
}
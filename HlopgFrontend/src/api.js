import axios from 'axios';
import AsyncStorage from "@react-native-async-storage/async-storage";
import { Platform } from "react-native";

// Update with your backend URL
const API_BASE_URL = 'http://10.0.2.2:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  // headers: {
  //   'Content-Type': 'application/json',
  // },
});

// Request interceptor to add token
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('hlopgToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      await AsyncStorage.removeItem('hlopgToken');
      await AsyncStorage.removeItem('hlopgUser');
      await AsyncStorage.removeItem('hlopgOwner');
      // You might want to navigate to login screen here
    }
    return Promise.reject(error);
  }
);

export default api;
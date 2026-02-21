
import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Image,
  ScrollView,
  StyleSheet,
  Alert,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation, useRoute } from "@react-navigation/native";
import api from "../api";
import Icon from "react-native-vector-icons/Ionicons";


const StudentLogin = () => {
  const navigation = useNavigation();
    const route = useRoute();
   const { role } = route.params || {};
  const [loginType, setLoginType] = useState("USER"); // USER | OWNER


  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleLogin = async () => {
  if (!identifier || !password) {
    setError("Please enter email/mobile and password");
    return;
  }

  try {
    setLoading(true);
    setError("");

     const url =
      loginType === "OWNER"
        ? "/auth/login/owner"
        : "/auth/login/user";

    const res = await api.post(url, {
      identifier,
      password,
    });

    const userData = res.data.data;

    await AsyncStorage.setItem("hlopgToken", userData.token);
    await AsyncStorage.setItem("hlopgUser", JSON.stringify(userData));
    await AsyncStorage.setItem("hlopgRole", loginType);

    const role = userData.userType;

    // navigation.replace(
    //   role === "OWNER" ? "OwnerStack" : "Main"
    // );

     // IMPORTANT: Check if there's a pending card click
      const pendingHostelId = await AsyncStorage.getItem("pendingHostelId");
      
      if (pendingHostelId && role !== "OWNER") {
        // If user clicked on a card before login and is not an owner
        await AsyncStorage.removeItem("pendingHostelId");
        navigation.replace("HostelDetails", {
          hostelId: pendingHostelId,
        });
      } else {
        // Normal navigation
        navigation.replace(
          role === "OWNER" ? "OwnerStack" : "Main"
        );
      }

  } catch (err) {
    console.log("LOGIN ERROR:", err.response?.data || err.message);
    setError(err.response?.data?.message || "Invalid credentials");
  } finally {
    setLoading(false);
  }
};

const handleGoogleLogin = async () => {
  try {
    Alert.alert(
      "Google Sign-In",
      "Google login integration pending.\nBackend + Firebase required."
    );

    // later you’ll integrate:
    // 1. Firebase Google Auth
    // 2. Send token to backend
    // 3. Store JWT & navigate
  } catch (error) {
    console.log("GOOGLE LOGIN ERROR:", error);
    Alert.alert("Error", "Google login failed");
  }
};



  return (
    <ScrollView contentContainerStyle={styles.screen}>
      {/* TOP IMAGE */}
      <Image
        source={require("../assets/pg7.png")}
        style={styles.topImage}
      />
      {/* BACK BUTTON */}
<TouchableOpacity
  style={styles.backBtn}
  onPress={() => navigation.goBack()}
>
  <Icon name="arrow-back" size={22} color="#111827" />
</TouchableOpacity>


      {/* CARD */}
      <View style={styles.card}>
        {/* LOGO */}
        <View style={styles.header}>
  <Image
    source={require("../assets/logo.png")}
    style={styles.logo}
  />
  <Text style={styles.title}>Login</Text>
</View>

        {error ? <Text style={styles.error}>{error}</Text> : null}

        {/* EMAIL / MOBILE */}
        <TextInput
          placeholder="Email or Mobile Number"
          style={styles.input}
          value={identifier}
          onChangeText={setIdentifier}
          autoCapitalize="none"
        />

        {/* PASSWORD */}
        <TextInput
          placeholder="Password"
          style={styles.input}
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        {/* FORGOT */}
        <TouchableOpacity
          onPress={() => navigation.navigate("ForgotPassword")}
          style={styles.forgot}
        >
          <Text style={styles.forgotText}>Forgot password?</Text>
        </TouchableOpacity>

        {/* LOGIN BUTTON */}
        <TouchableOpacity
          style={styles.loginBtn}
          onPress={handleLogin}
          disabled={loading}
        >
          <Text style={styles.loginText}>
            {loading ? "Logging in..." : "Login"}
          </Text>
        </TouchableOpacity>

        {/* REGISTER */}
        {/* <View style={styles.registerRow}>
          <Text>New User?</Text>
          <TouchableOpacity
            onPress={() => navigation.navigate("StudentSignup")}
          >
            <Text style={styles.registerText}> Register now</Text>
          </TouchableOpacity>
        </View> */}

        {/* OR */}
        <Text style={styles.orText}>OR</Text>

        {/* GOOGLE */}
        <TouchableOpacity style={styles.googleBtn}>
          <Icon
    name="logo-google"
    size={20}
    color="#ffffff"
    style={styles.googleIcon}
  />
          <Text style={styles.googleText}>Sign in with Google</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

export default StudentLogin;


const styles = StyleSheet.create({
  screen: {
    flexGrow: 1,
    backgroundColor: "#eef7fb",
    alignItems: "center",
    paddingBottom: 40,

  },

  /* TOP IMAGE */
  topImage: {
    width: "100%",
    height: 270,
    resizeMode: "cover",
  },

  /* CARD */
  card: {
    width: "85%",
    backgroundColor: "#ffffff",
    padding: 24,
    borderRadius: 16,
    marginTop: 60,
    alignSelf: "center", 
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.15,
    shadowRadius: 10,
    elevation: 6,
  },

  /* HEADER */
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 20,
  },

  logo: {
    width: 40,
    height: 40,
    resizeMode: "contain",
    marginRight: 10,
  },

  title: {
    fontSize: 22,
    fontWeight: "700",
    color: "#111827",
  },

  error: {
    color: "#dc2626",
    textAlign: "center",
    marginBottom: 10,
  },

  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    padding: 12,
    marginBottom: 14,
    backgroundColor: "#ffffff",
  },

  forgot: {
    alignItems: "flex-end",
    marginBottom: 10,
  },

  forgotText: {
    color: "#4f46e5",
    fontSize: 14,
    fontWeight: "500",
  },

  button: {
    backgroundColor: "#4f46e5",
    paddingVertical: 14,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 10,
  },

  buttonText: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "600",
  },

  orText: {
    textAlign: "center",
    marginVertical: 16,
    color: "#9ca3af",
    fontWeight: 500,
  },

  /* GOOGLE BUTTON */
  googleBtn: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    paddingVertical: 12,
    backgroundColor: "#2f11ee",
  },

  /* WHITE CIRCLE FIX */
  googleIconWrapper: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: "#ffffff",
    alignItems: "center",
    justifyContent: "center",
    marginRight: 12,
  },

  googleIcon: {
    width: 18,
    height: 18,
    resizeMode: "contain",
    marginRight:10,
  },

  googleText: {
    fontSize: 15,
    fontWeight: "500",
    color: "#edf0f7",
  },

  loginBtn: {
  backgroundColor: "#4f46e5",
  paddingVertical: 14,
  borderRadius: 8,
  alignItems: "center",
  marginTop: 10,
},

loginText: {
  color: "#ffffff",
  fontSize: 16,
  fontWeight: "600",
},

registerRow: {
  flexDirection: "row",
  justifyContent: "center",
  marginTop: 14,
},

registerText: {
  color: "#4f46e5",
  fontWeight: "600",
},

backBtn: {
  position: "absolute",
  top: 16,
  left: 16,
  zIndex: 10,
},


});
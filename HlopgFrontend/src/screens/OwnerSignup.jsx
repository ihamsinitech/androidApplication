import React, { useEffect, useRef, useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Image,
  Alert,
} from "react-native";
import Ionicons from "react-native-vector-icons/Ionicons";
import { useNavigation } from "@react-navigation/native";
import api from "../api";
import Icon from "react-native-vector-icons/Ionicons";

const OwnerSignup = () => {
  const navigation = useNavigation();

  // STEP: 1 = Signup | 2 = OTP
  const [step, setStep] = useState(1);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: "",
  });

  const [showPwd, setShowPwd] = useState(false);
  const [showConfirmPwd, setShowConfirmPwd] = useState(false);
  const [agreed, setAgreed] = useState(false);

  // OTP
  const [otp, setOtp] = useState(["", "", "", ""]);
  const otpRefs = useRef([]);
  const [timer, setTimer] = useState(30);

  // TIMER
  useEffect(() => {
    if (step !== 2 || timer === 0) return;
    const i = setInterval(() => setTimer(t => t - 1), 1000);
    return () => clearInterval(i);
  }, [step, timer]);

  const handleChange = (k, v) =>
    setFormData(p => ({ ...p, [k]: v }));

  // SIGNUP
  const handleSignup = async () => {
    const { name, email, phone, password, confirmPassword } = formData;

    if (!name || !email || !phone || !password)
      return Alert.alert("Error", "All fields required");

    if (password !== confirmPassword)
      return Alert.alert("Error", "Passwords do not match");

    try {
      await api.post("/auth/register/owner", {
        name,
        email,
        phone,
        password,
      });

      setStep(2);
      setTimer(30);
      setOtp(["", "", "", ""]);
    } catch (e) {
      Alert.alert("Error", e.response?.data?.message || "Signup failed");
    }
  };

  // OTP INPUT
  const onOtpChange = (v, i) => {
    const arr = [...otp];
    arr[i] = v.replace(/\D/, "");
    setOtp(arr);
    if (v && i < 3) otpRefs.current[i + 1].focus();
  };

  // VERIFY OTP
  const verifyOtp = async () => {
    try {
      await api.post("/auth/verify-otp", {
        identifier: formData.phone,
        otpCode: otp.join(""),
        purpose: "REGISTRATION",
      });

      Alert.alert("Success", "Account verified");
      navigation.navigate("StudentLogin");
    } catch {
      Alert.alert("Error", "Invalid OTP");
    }
  };

  const resendOtp = async () => {
    await api.post("/auth/resend-otp", {
      identifier: formData.phone,
      purpose: "REGISTRATION",
    });
    setTimer(30);
  };

  /* ===================== UI ===================== */

  return (
    <ScrollView contentContainerStyle={styles.container}>

      {/* ---------- SIGNUP SCREEN ---------- */}
      {step === 1 && (
        <>
             {/* BACK BUTTON */}
       <TouchableOpacity
         style={styles.backBtn}
         onPress={() => navigation.goBack()}
       >
         <Icon name="arrow-back" size={22} color="#111827" />
       </TouchableOpacity>
          <View style={styles.card}>
            <View style={styles.headers}>
              <Image
                source={require("../assets/logo.png")}
                style={styles.logo}
              />
              <Text style={styles.titles}>Owner Signup</Text>
            </View>
            {["name", "email", "phone"].map(f => (
              <TextInput
                key={f}
                style={styles.input}
                placeholder={f.replace(/([A-Z])/g, " $1")}
                value={formData[f]}
                onChangeText={t => handleChange(f, t)}
              />
            ))}

            {/* PASSWORD */}
            <View style={styles.passwordBox}>
              <TextInput
                placeholder="Password"
                secureTextEntry={!showPwd}
                style={styles.passwordInput}
                value={formData.password}
                onChangeText={t => handleChange("password", t)}
              />
              <TouchableOpacity onPress={() => setShowPwd(!showPwd)}>
                <Ionicons name={showPwd ? "eye-off" : "eye"} size={20} />
              </TouchableOpacity>
            </View>

            {/* CONFIRM PASSWORD */}
            <View style={styles.passwordBox}>
              <TextInput
                placeholder="Confirm Password"
                secureTextEntry={!showConfirmPwd}
                style={styles.passwordInput}
                value={formData.confirmPassword}
                onChangeText={t => handleChange("confirmPassword", t)}
              />
              <TouchableOpacity onPress={() => setShowConfirmPwd(!showConfirmPwd)}>
                <Ionicons name={showConfirmPwd ? "eye-off" : "eye"} size={20} />
              </TouchableOpacity>
            </View>

            {/* TERMS */}
            <View style={styles.termsRow}>
              <TouchableOpacity
                style={styles.checkbox}
                onPress={() => setAgreed(!agreed)}
              >
                {agreed && <View style={styles.checked} />}
              </TouchableOpacity>
              <Text>I agree to Terms & Conditions</Text>
            </View>

            <TouchableOpacity
              style={[styles.button, !agreed && styles.disabled]}
              disabled={!agreed}
              onPress={handleSignup}
            >
              <Text style={styles.buttonText}>Sign Up</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => navigation.navigate("StudentLogin")}>
              <Text style={styles.link}>Already have an account? Login</Text>
            </TouchableOpacity>

            <Text style={styles.or}>OR</Text>

            <TouchableOpacity style={styles.googleBtn}>
              <Icon
                  name="logo-google"
                  size={20}
                  color="#ffffff"
                  style={styles.googleIcon}
                />

              <Text style={styles.googleText}>Continue with Google</Text>
            </TouchableOpacity>
          </View>
        </>
      )}

      {/* ---------- OTP SCREEN ---------- */}
      {step === 2 && (
        <>
          <View style={styles.header}>
            <TouchableOpacity onPress={() => setStep(1)}>
              <Ionicons name="arrow-back" size={24} />
            </TouchableOpacity>
            <Text style={styles.headerTitle}> OTP Verification</Text>
          </View>

          <View style={styles.card}>
            <Text>We have sent a code to</Text>
            <Text style={styles.bold}>{formData.phone}</Text>

            <View style={styles.otpRow}>
              {otp.map((v, i) => (
                <TextInput
                  key={i}
                  ref={r => (otpRefs.current[i] = r)}
                  style={styles.otpBox}
                  maxLength={1}
                  keyboardType="numeric"
                  value={v}
                  onChangeText={t => onOtpChange(t, i)}
                />
              ))}
            </View>

            <Text style={styles.timer}>
              {timer > 0 ? `00:${timer}` : "Resend OTP"}
            </Text>

            {timer === 0 && (
              <TouchableOpacity onPress={resendOtp}>
                <Text style={styles.link}>Resend OTP</Text>
              </TouchableOpacity>
            )}

            <TouchableOpacity style={styles.button} onPress={verifyOtp}>
              <Text style={styles.buttonText}>Verify</Text>
            </TouchableOpacity>
          </View>
        </>
      )}
    </ScrollView>
  );
};

export default OwnerSignup;

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: 20, backgroundColor: "#fff" ,},

   headers: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 20,
  
  
  },

  logo: {
    width: 60,
    height: 50,
    resizeMode: "contain",
    marginRight: 12,
    
  
    
  },

  titles: {
    fontSize: 22,
    fontWeight: "700",
    color: "#111827",
  
  },
  card: {
    backgroundColor: "#fff",
    borderRadius: 14,
    padding: 20,
    borderWidth: 1,
    borderColor: "#e5e7eb",
  },

  title: { fontSize: 24, fontWeight: "700", textAlign: "flex",flexDirection:"row" ,marginBottom: 20 },

  input: {
    borderWidth: 1,
    borderColor: "#50dcf5",
    borderRadius: 8,
    padding: 12,
    marginBottom: 12,
  },

  passwordBox: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#50dcf5",
    borderRadius: 8,
    paddingHorizontal: 12,
    marginBottom: 12,
  },

  passwordInput: { flex: 1, paddingVertical: 12 },

  termsRow: { flexDirection: "row", alignItems: "center", marginVertical: 10 },

  checkbox: {
    width: 20,
    height: 20,
    borderWidth: 1,
    borderColor: "#4f46e5",
    marginRight: 10,
    justifyContent: "center",
    alignItems: "center",
  },

  checked: { width: 12, height: 12, backgroundColor: "#4f46e5" },

  button: {
    backgroundColor: "#4f46e5",
    padding: 14,
    borderRadius: 10,
    alignItems: "center",
    marginTop: 10,
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

  disabled: { backgroundColor: "#9ca3af" },

  buttonText: { color: "#fff", fontWeight: "600" },

  link: { color: "#4f46e5", textAlign: "center", marginTop: 10 },

  or: { textAlign: "center", marginVertical: 12, color: "#6b7280" },

 
  

  
  header: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginBottom: 60,
  },

  headerTitle: { fontSize: 18, fontWeight: "600" },

  otpRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginVertical: 20,
  },

  otpBox: {
    width: 55,
    height: 55,
    borderWidth: 1,
    borderColor: "#50dcf5",
    borderRadius: 10,
    textAlign: "center",
    fontSize: 18,
  },

  timer: { textAlign: "right", color: "#4f46e5" },

  bold: { fontWeight: "700", marginTop: 6 },
});
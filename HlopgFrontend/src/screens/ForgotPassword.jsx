import React, { useEffect, useRef, useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
} from "react-native";
import Ionicons from "react-native-vector-icons/Ionicons";
import api from "../api";

const ForgotPassword = ({ navigation }) => {
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);

  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState(["", "", "", ""]);
  const inputs = useRef([]);

  const [timer, setTimer] = useState(60);

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [showConfirmPwd, setShowConfirmPwd] = useState(false);

  // TIMER
  useEffect(() => {
    if (step !== 2 || timer === 0) return;
    const interval = setInterval(() => setTimer(t => t - 1), 1000);
    return () => clearInterval(interval);
  }, [step, timer]);

  // SEND OTP
  const sendOtp = async () => {
    setLoading(true);
    await api.post("/auth/forgot-password/mobile", { phone });
    setStep(2);
    setTimer(60);
    setLoading(false);
  };

  // HANDLE OTP INPUT
  const handleOtpChange = (value, index) => {
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    if (value && index < 3) {
      inputs.current[index + 1].focus();
    }

    if (newOtp.join("").length === 4) {
      verifyOtp(newOtp.join(""));
    }
  };

  // VERIFY OTP
  const verifyOtp = async (code) => {
    await api.post("/auth/verify-otp", {
      identifier: phone,
      otpCode: code,
      purpose: "PASSWORD_RESET",
    });
    setStep(3);
  };

  // RESET PASSWORD
  const resetPassword = async () => {
    await api.post("/auth/reset-password", {
      identifier: phone,
      otpCode: otp.join(""),
      newPassword: password,
    });
    navigation.goBack();
  };

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() =>
            step === 1 ? navigation.goBack() : setStep(step - 1)
          }
        >
          <Ionicons name="arrow-back" size={24} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>
          {step === 3 ? "Set Password" : "Forgot Password"}
        </Text>
      </View>

      

      {/* STEP 1 CARD */}
      {step === 1 && (
        <View>
          <Text style={styles.title}>Reset Password</Text>
          <Text style={styles.desc}>
            Enter your mobile number to reset your password
          </Text>
        <View style={styles.card}>
          
         <Text style={styles.text}>Enter Number</Text>
          <TextInput
            placeholder="Mobile Number"
            style={styles.input}
            keyboardType="phone-pad"
            value={phone}
            onChangeText={setPhone}
          />

          <TouchableOpacity style={styles.button} onPress={sendOtp}>
            <Text style={styles.buttonText}>
              {loading ? "Sending..." : "Send OTP"}
            </Text>
          </TouchableOpacity>
        </View>
        </View>
      )}

      {/* STEP 2 CARD */}
      {step === 2 && (
        <View>
          <Text style={styles.title}>Check your message box</Text>
          <Text style={styles.desc}>
            We sent a 4 digit code to the mobile number
          </Text>
         <View style={styles.card}>
          <View style={styles.infoBox}>
            <Text>We have sent a verification code to</Text>
            <Text style={styles.bold}>{phone}</Text>
          </View>

          <View style={styles.otpRow}>
            {otp.map((digit, i) => (
              <TextInput
                key={i}
                ref={ref => (inputs.current[i] = ref)}
                style={styles.otpBox}
                keyboardType="number-pad"
                maxLength={1}
                value={digit}
                onChangeText={(v) => handleOtpChange(v, i)}
              />
            ))}
          </View>

          <Text style={styles.timer}>
            {timer > 0 ? `00:${timer}` : "Resend OTP"}
          </Text>
          </View>
        </View>
      )}

      {/* STEP 3 CARD */}
      {step === 3 && (
        <View>
          <Text style={styles.title}>Set a New Password</Text>
          <Text style={styles.desc}>
            Create a new password. Ensure it differs from previous ones.
          </Text>

         <View style={styles.card}>
          
          <View style={styles.passwordBox}>
            <TextInput
              placeholder="Password"
              secureTextEntry={!showPwd}
              style={styles.passwordInput}
              value={password}
              onChangeText={setPassword}
            />
            <TouchableOpacity onPress={() => setShowPwd(!showPwd)}>
              <Ionicons name={showPwd ? "eye-off" : "eye"} size={20} />
            </TouchableOpacity>
          </View>

          <View style={styles.passwordBox}>
            <TextInput
              placeholder="Confirm Password"
              secureTextEntry={!showConfirmPwd}
              style={styles.passwordInput}
              value={confirmPassword}
              onChangeText={setConfirmPassword}
            />
            <TouchableOpacity
              onPress={() => setShowConfirmPwd(!showConfirmPwd)}
            >
              <Ionicons
                name={showConfirmPwd ? "eye-off" : "eye"}
                size={20}
              />
            </TouchableOpacity>
          </View>

          <TouchableOpacity style={styles.button} onPress={resetPassword}>
            <Text style={styles.buttonText}>Update Password</Text>
          </TouchableOpacity>
        </View>
        </View>
      )}
    </View>
  );
};

export default ForgotPassword;


const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 20 },
  

  header: { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 40, marginTop:60 },
  headerTitle: { fontSize: 18, fontWeight: "600" },

  title: { fontSize: 22, fontWeight: "700", marginBottom: 6,marginLeft:10 },
  desc: { color: "#6b7280", marginBottom: 30,marginLeft:10 },
  card: {
  backgroundColor: "#fff",
  borderRadius: 14,
  padding: 20,
  borderWidth: 1,
  borderColor: "#e5e7eb",
},

text: {
   fontSize:15,
   marginBottom:10,
},

  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 10,
    padding: 14,
    marginBottom: 20,

  },

  infoBox: {
    borderWidth: 1,
    borderColor: "#e5e7eb",
    padding: 16,
    borderRadius: 10,
    marginBottom: 30,
  },

  bold: { fontWeight: "700", marginTop: 4 },

  otpRow: { flexDirection: "row", justifyContent: "space-between", marginBottom: 20 },
  otpBox: {
    width: 55,
    height: 55,
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 10,
    textAlign: "center",
    fontSize: 18,
  },

  timer: { textAlign: "right", color: "#4f46e5" },

  passwordBox: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 10,
    paddingHorizontal: 12,
    marginBottom: 16,
  },
  passwordInput: { flex: 1, paddingVertical: 12 },

  button: {
    backgroundColor: "#4f46e5",
    paddingVertical: 14,
    borderRadius: 10,
    alignItems: "center",
    marginTop: 20,
  },
  buttonText: { color: "#fff", fontWeight: "600", fontSize: 16 },
});
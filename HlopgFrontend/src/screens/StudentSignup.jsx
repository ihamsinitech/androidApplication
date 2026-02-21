import React, { useState, useRef, useEffect } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
  Modal,
  Alert,
  Dimensions ,
  ScrollView,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import api from "../api";
import { Picker } from "@react-native-picker/picker";
import Icon from "react-native-vector-icons/Ionicons";


const StudentSignup = () => {
  const navigation = useNavigation();
  const [showGenderPicker, setShowGenderPicker] = useState(false);
  


  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: "",
    gender: "", 
  });

  const [errors, setErrors] = useState({});
  const [showOTP, setShowOTP] = useState(false);
  const [otp, setOtp] = useState(["", "", "", ""]);
  const [timer, setTimer] = useState(30);

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const phoneRegex = /^[6-9]\d{9}$/;

  useEffect(() => {
    if (!showOTP) return;
    const interval = setInterval(() => {
      setTimer((t) => (t > 0 ? t - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [showOTP]);

  const handleChange = (name, value) => {
    setFormData({ ...formData, [name]: value });
  };

  const validate = () => {
    let errs = {};

    if (formData.name.length < 3)
      errs.name = "Name must be at least 3 characters";
    if (!emailRegex.test(formData.email))
      errs.email = "Invalid email";
    if (!phoneRegex.test(formData.phone))
      errs.phone = "Invalid phone number";
    if (!formData.gender)
    errs.gender = "Gender is required";
    if (formData.password.length < 6)
      errs.password = "Password too short";
    if (formData.password !== formData.confirmPassword)
      errs.confirmPassword = "Passwords do not match";

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const submit = async () => {
  if (!validate()) return;

  try {
    const res = await api.post("/auth/register/user", {
      name: formData.name,
      email: formData.email,
      phone: formData.phone,
      password: formData.password,
      gender: formData.gender,
    });

    console.log("REGISTER SUCCESS:", res.data);

    setShowOTP(true);
    setTimer(30);

  } catch (e) {
    console.log("REGISTER ERROR FULL:", e);
    console.log("REGISTER ERROR DATA:", e.response?.data);

    Alert.alert(
      "Error",
      e.response?.data?.message || e.message || "Registration failed"
    );
  }
};


  const verifyOTP = async () => {
    try {
      await api.post("/auth/verify-otp", {
  identifier: formData.phone,
  otpCode: otp.join(""),
  purpose: "REGISTRATION",
});

      Alert.alert("Success", "Account created");
      setShowOTP(false);
      navigation.navigate("StudentLogin");
    } catch {
      Alert.alert("Error", "Invalid OTP");
    }
  };

  return (
  <View style={styles.screen}>

          {/* BACK BUTTON */}
    <TouchableOpacity
      style={styles.backBtn}
      onPress={() => navigation.goBack()}
    >
      <Icon name="arrow-back" size={22} color="#111827" marginTop={20} marginLeft={20} />
    </TouchableOpacity>
    <ScrollView
      contentContainerStyle={styles.scrollContent}
      keyboardShouldPersistTaps="handled"
      showsVerticalScrollIndicator={false}
    >
      {/* LOGO */}
      <Image
        source={require("../assets/logo.png")} // adjust path if needed
        style={styles.logo}
      />

      {/* CARD */}
      <View style={styles.card}>
        <Text style={styles.title}>Student Signup</Text>

        <TextInput
          placeholder="Full Name"
          style={styles.input}
          onChangeText={(v) => handleChange("name", v)}
        />
        {errors.name && <Text style={styles.errorText}>{errors.name}</Text>}

        <TextInput
          placeholder="Email"
          style={styles.input}
          keyboardType="email-address"
          autoCapitalize="none" 
          autoCorrect={false}
          onChangeText={(v) => handleChange("email", v)}
        />
        {errors.email && <Text style={styles.errorText}>{errors.email}</Text>}

        <TextInput
          placeholder="Phone"
          style={styles.input}
          keyboardType="number-pad"
          onChangeText={(v) => handleChange("phone", v)}
        /> 
      {errors.phone && <Text style={styles.errorText}>{errors.phone}</Text>}


  <TextInput
          placeholder="Gender"
          style={styles.input}
          keyboardType="text"
          onChangeText={(v) => handleChange("gender", v)}
        /> 

{errors.gender && (
  <Text style={styles.errorText}>{errors.gender}</Text>
)}


        <TextInput
          placeholder="Password"
          style={styles.input}
          secureTextEntry
          onChangeText={(v) => handleChange("password", v)}
        />

        <TextInput
          placeholder="Confirm Password"
          style={styles.input}
          secureTextEntry
          onChangeText={(v) => handleChange("confirmPassword", v)}
        />

        <TouchableOpacity style={styles.button} onPress={submit}>
          <Text style={styles.buttonText}>Sign Up</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => navigation.navigate("StudentLogin")}>
          <Text style={styles.link}>
            Already have an account? Login
          </Text>
        </TouchableOpacity>
      </View>
    </ScrollView>


  


    {/* OTP MODAL */}
    <Modal visible={showOTP} transparent animationType="fade">
      <View style={styles.modalBackdrop}>
        <View style={styles.modalBox}>
          <Text style={styles.modalTitle}>Enter OTP</Text>

          <View style={styles.otpRow}>
            {otp.map((v, i) => (
              <TextInput
                key={i}
                style={styles.otpInput}
                maxLength={1}
                keyboardType="number-pad"
                onChangeText={(val) => {
                  const arr = [...otp];
                  arr[i] = val;
                  setOtp(arr);
                }}
              />
            ))}
          </View>

          <TouchableOpacity style={styles.button} onPress={verifyOTP}>
            <Text style={styles.buttonText}>Verify OTP</Text>
          </TouchableOpacity>

          <Text style={styles.resendText}>
            Resend OTP in {timer}s
          </Text>
        </View>
      </View>
    </Modal>
  </View>
);

};

export default StudentSignup;



const { width } = Dimensions.get("window");

const styles = StyleSheet.create({
  /* =============== LOGIN LAYOUT =============== */
  container: {
    flex: 1,
    backgroundColor: "#eef7fb",
    // justifyContent: "center",
    // alignItems: "center",
    paddingHorizontal: 20,
  
  },
  screen: {
  flex: 1,
  backgroundColor: "#eef7fb",
},

scrollContent: {
  alignItems: "center",
  paddingVertical: 40,
  paddingHorizontal: 20,
},

logo: {
  width: width * 0.6,
  height: 220,
  resizeMode: "contain",
  marginBottom: 30,
},


  image: {
    height: 260,
    width: width * 0.75,
    borderRadius: 20,
    resizeMode: "cover",
    marginBottom: 20,
    

  },

  /* =============== CARD =============== */
  card: {
    width: "100%",
    maxWidth: 420,
    backgroundColor: "#f9fdff",
    borderRadius: 16,
    padding: 24,

    /* shadow */
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.15,
    shadowRadius: 12,
    elevation: 6,
  },

  title: {
    fontSize: 24,
    fontWeight: "600",
    color: "#333",
    textAlign: "center",
    marginBottom: 20,
  },

  /* =============== FORM INPUTS =============== */
  input: {
    borderWidth: 1,
    borderColor: "#50dcf5",
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 15,
    fontSize: 16,
    backgroundColor: "#fff",
    marginBottom: 12,
  },

  inputValid: {
    borderColor: "#22c55e",
  },

  inputInvalid: {
    borderColor: "#e63946",
  },

  errorText: {
    color: "#e63946",
    fontSize: 13,
    marginBottom: 8,
  },

  /* =============== BUTTON =============== */
  button: {
    backgroundColor: "#4f46e5",
    paddingVertical: 14,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 10,
  },

  buttonText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },

  link: {
    textAlign: "center",
    marginTop: 18,
    color: "#4f46e5",
    fontSize: 14,
    fontWeight: "500",
  },

  /* =============== OTP MODAL =============== */
  modalBackdrop: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.6)",
    justifyContent: "center",
    alignItems: "center",
  },

  modalBox: {
    backgroundColor: "#fff",
    width: "90%",
    maxWidth: 380,
    borderRadius: 12,
    padding: 24,

    shadowColor: "#000",
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.25,
    shadowRadius: 12,
    elevation: 8,
  },

  modalTitle: {
    fontSize: 20,
    fontWeight: "600",
    color: "#4f46e5",
    marginBottom: 10,
    textAlign: "center",
  },

  modalText: {
    fontSize: 14,
    color: "#333",
    textAlign: "center",
    marginBottom: 20,
  },

  otpRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 20,
  },

  otpInput: {
    width: 55,
    height: 55,
    borderWidth: 1,
    borderColor: "#50dcf5",
    borderRadius: 8,
    textAlign: "center",
    fontSize: 22,
  },

  otpButtonRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 10,
  },

  cancelButton: {
    backgroundColor: "#888",
  },

  resendText: {
    marginTop: 12,
    color: "#4f46e5",
    textAlign: "center",
    fontSize: 14,
    fontWeight: "500",
  },

  otpError: {
    color: "red",
    textAlign: "center",
    marginBottom: 8,
    fontWeight: "500",
  },

  pickerWrapper: {
  borderWidth: 1,
  borderColor: "#50dcf5",
  borderRadius: 8,
  backgroundColor: "#fff",
  marginBottom: 1,
  height:50,
},
genderInput: {
  flexDirection: "row",
  alignItems: "center",
  justifyContent: "space-between",

  borderWidth: 1,
  borderColor: "#50dcf5",
  borderRadius: 8,

  paddingVertical: 12,
  paddingHorizontal: 15,

  backgroundColor: "#fff",
  marginBottom: 12,
},

genderText: {
  fontSize: 16,
  color: "#111827",
},

hiddenPicker: {
  position: "absolute",
  left: 0,
  right: 0,
  backgroundColor: "#fff",
  zIndex: 999,
  marginTop:50,
},


});


import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation } from "@react-navigation/native";
import { SafeAreaView } from "react-native-safe-area-context";
import MaterialIcons from "react-native-vector-icons/MaterialIcons";
import Icon from "react-native-vector-icons/Ionicons";

const RoleSelection = () => {
  const [selectedRole, setSelectedRole] = useState(null);
  const navigation = useNavigation();

  // 🔒 Auto redirect if already logged in
  useEffect(() => {
    const checkAuth = async () => {
      const token = await AsyncStorage.getItem("hlopgToken");
      const role = await AsyncStorage.getItem("hlopgRole");

      if (token && role === "student") {
        navigation.replace("UserPanel");
      }

      if (token && role === "owner") {
        navigation.replace("OwnerDashboard");
      }
    };

    checkAuth();
  }, []);

  // 👉 CREATE ACCOUNT
  const handleSignup = () => {
    if (!selectedRole) {
      Alert.alert("Select role", "Please select a role to continue");
      return;
    }

    if (selectedRole === "student") {
      navigation.navigate("StudentSignup");
    } else {
      navigation.navigate("OwnerSignup");
    }
  };

  // 👉 LOGIN (shared login screen)
  const handleLogin = () => {
    if (!selectedRole) {
      Alert.alert("Select role", "Please select a role first");
      return;
    }

    navigation.navigate("StudentLogin", {
      role: selectedRole, // 🔥 THIS is the key
    });
  };

  return (
    <SafeAreaView style={styles.safe}>
         {/* BACK BUTTON */}
          <TouchableOpacity
            style={styles.backBtn}
            onPress={() => navigation.goBack()}
          >
            <Icon name="arrow-back" size={22} color="#111827" marginTop={20} marginLeft={20} />
          </TouchableOpacity>
      <View style={styles.container}>
        <Text style={styles.title}>Join as a User or Owner</Text>

        {/* ROLE OPTIONS */}
        <TouchableOpacity
          style={[
            styles.card,
            selectedRole === "student" && styles.selected,
          ]}
          onPress={() => setSelectedRole("student")}
        >
          <MaterialIcons name="people" size={44} color="#4f46e5" />
          <Text style={styles.cardText}>
            I’m a Student or Professional,{"\n"}looking for a PG / Hostel
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            styles.card,
            selectedRole === "owner" && styles.selected,
          ]}
          onPress={() => setSelectedRole("owner")}
        >
          <MaterialIcons name="apartment" size={44} color="#4f46e5" />
          <Text style={styles.cardText}>
            I’m an Owner,{"\n"}hosting a PG / Hostel
          </Text>
        </TouchableOpacity>

        {/* CREATE ACCOUNT */}
        <TouchableOpacity
          style={[
            styles.primaryBtn,
            !selectedRole && styles.disabledBtn,
          ]}
          onPress={handleSignup}
          disabled={!selectedRole}
        >
          <Text style={styles.primaryText}>Create An Account</Text>
        </TouchableOpacity>

        {/* LOGIN */}
        <TouchableOpacity onPress={handleLogin}>
          <Text style={styles.loginText}>
            Already have an account?{" "}
            <Text style={styles.loginLink}>Login</Text>
          </Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

export default RoleSelection;

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: "#f9f9ff",
  },
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "center",
  },
  title: {
    fontSize: 26,
    fontWeight: "700",
    textAlign: "center",
    marginBottom: 30,
    color: "#333",
    
  },
  card: {
    width: "100%",
    backgroundColor: "#fff",
    borderRadius: 18,
    padding: 22,
    marginBottom: 18,
    borderWidth: 2,
    borderColor: "#ddd",
    alignItems: "center",
    
  },
  selected: {
    borderColor: "#4f46e5",
    shadowColor: "#4f46e5",
    shadowOpacity: 0.3,
    shadowRadius: 10,
    elevation: 6,
  },
  cardText: {
    marginTop: 14,
    fontSize: 15,
    color: "#555",
    textAlign: "center",
    lineHeight: 20,
  },
  primaryBtn: {
    marginTop: 30,
    backgroundColor: "#4f46e5",
    paddingVertical: 14,
    borderRadius: 14,
    alignItems: "center",
  },
  primaryText: {
    color: "#fff",
    fontSize: 17,
    fontWeight: "600",
  },
  disabledBtn: {
    backgroundColor: "#ccc",
  },
  loginText: {
    marginTop: 16,
    textAlign: "center",
    fontSize: 14,
    color: "#555",
  },
  loginLink: {
    color: "#4f46e5",
    fontWeight: "600",
  },
});
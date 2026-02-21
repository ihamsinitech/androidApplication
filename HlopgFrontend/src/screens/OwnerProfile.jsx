import React, { useCallback, useState } from "react";
import {
  View,
  Text,
  Image,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Modal,
} from "react-native";
import { useNavigation, useFocusEffect } from "@react-navigation/native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import Icon from "react-native-vector-icons/Ionicons";
import api from "../api";
import { Platform } from "react-native";

const OwnerProfile = () => {
  const navigation = useNavigation();
  const [owner, setOwner] = useState(null);
  const [loading, setLoading] = useState(true);
  const [logoutVisible, setLogoutVisible] = useState(false);

  const BASE_URL =  "http://10.0.2.2:8080";
    // Platform.OS === "ios"
    //   ? "http://127.0.0.1:8080"
    //   : "http://10.0.2.2:8080";

useFocusEffect(
  useCallback(() => {
    let active = true;

    const loadOwner = async () => {
      try {
        // const res = await api.get("/auth/ownerid");
        const res = await api.get("/auth/owner/profile");

        if (active) setOwner(res.data);
      } catch (e) {
        const status = e.response?.status;

        console.log("OWNER PROFILE ERROR:", status, e.response?.data);

        // 🔥 ONLY logout if token is invalid
        if (status === 401) {
          await AsyncStorage.multiRemove(["hlopgToken", "hlopgRole"]);
          navigation.reset({
            index: 0,
            routes: [{ name: "Main" }],
          });
        }
        // ❌ DO NOTHING for 403 / 500 / network errors
      } finally {
        if (active) setLoading(false);
      }
    };

    loadOwner();
    return () => {
      active = false;
    };
  }, [])
);


  const logout = async () => {
    await AsyncStorage.multiRemove(["hlopgToken", "hlopgRole"]);
    navigation.replace("Main");
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <Text>Loading profile...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Account</Text>

      {/* PROFILE */}
      <View style={styles.profileBox}>
        <Image
          source={{
            uri: owner?.profileImage
              ? `${BASE_URL}${owner.profileImage}?t=${Date.now()}`
              : "https://cdn-icons-png.flaticon.com/512/4140/4140048.png",
          }}
          style={styles.avatar}
        />
        <View style={styles.nameRow}>
          <Text style={styles.name}>{owner?.name}</Text>
          <TouchableOpacity
            onPress={() => navigation.navigate("OwnerEditProfile")}
          >
            <Icon name="pencil" size={18} color="#4f46e5" />
          </TouchableOpacity>
        </View>
      </View>

      {/* MENU */}
      <MenuItem
        icon="notifications-outline"
        label="Notifications"
        onPress={() => navigation.navigate("OwnerNotifications")}
      />

      <MenuItem
        icon="bed-outline"
        label="My Rooms"
        onPress={() => navigation.navigate("MyRooms")}
      />

      <MenuItem
        icon="create-outline"
        label="Edit PG's Info"
        onPress={() => navigation.navigate("EditPGList")}
      />

      <MenuItem
        icon="document-text-outline"
        label="Terms and Conditions"
        onPress={() => navigation.navigate("Terms")}
      />

      <MenuItem
        icon="help-circle-outline"
        label="Help and Support"
        onPress={() => navigation.navigate("Help")}
      />

      <MenuItem
        icon="log-out-outline"
        label="Logout"
        danger
        onPress={() => setLogoutVisible(true)}
      />

      {/* LOGOUT MODAL */}
      <Modal visible={logoutVisible} transparent animationType="fade">
        <View style={styles.modalOverlay}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>Logout</Text>
            <Text style={styles.modalText}>
              Are you sure you want to logout?
            </Text>
            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={styles.cancelBtn}
                onPress={() => setLogoutVisible(false)}
              >
                <Text>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
                <Text style={{ color: "#fff" }}>Logout</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </ScrollView>
  );
};

const MenuItem = ({ icon, label, onPress, danger }) => (
  <TouchableOpacity style={styles.menuItem} onPress={onPress}>
    <View style={styles.menuLeft}>
      <Icon name={icon} size={20} color={danger ? "#ef4444" : "#6366f1"} />
      <Text style={[styles.menuText, danger && { color: "#ef4444" }]}>
        {label}
      </Text>
    </View>
    <Icon name="chevron-forward" size={18} color="#9ca3af" />
  </TouchableOpacity>
);

export default OwnerProfile;

/* ================= STYLES ================= */

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 16,
  },
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  header: {
    fontSize: 18,
    fontWeight: "600",
    textAlign: "center",
    marginTop: 20,
    marginBottom: 20,
  },
  profileBox: {
    alignItems: "center",
    marginBottom: 20,
  },
  avatar: {
    width: 90,
    height: 90,
    borderRadius: 45,
    marginBottom: 10,
  },
  nameRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  name: {
    fontSize: 16,
    fontWeight: "600",
  },
  menuItem: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#e5e7eb",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  menuLeft: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
  },
  menuText: {
    fontSize: 15,
    color: "#111827",
  },

  /* MODAL */
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.4)",
    justifyContent: "center",
    alignItems: "center",
  },
  modalBox: {
    width: "85%",
    backgroundColor: "#fff",
    borderRadius: 16,
    padding: 20,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "600",
    marginBottom: 8,
  },
  modalText: {
    fontSize: 14,
    color: "#4b5563",
    marginBottom: 20,
  },
  modalButtons: {
    flexDirection: "row",
    justifyContent: "flex-end",
    gap: 12,
  },
  cancelBtn: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: "#e5e7eb",
    borderRadius: 8,
  },
  logoutBtn: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: "#ef4444",
    borderRadius: 8,
  },
});
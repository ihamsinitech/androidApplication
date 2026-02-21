import React, { useEffect,useCallback, useState } from "react";
import {
  View,
  Text,
  Image,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Alert,
  Modal
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import Icon from "react-native-vector-icons/Ionicons";
import { useNavigation } from "@react-navigation/native";
import api from "../api";
import { useFocusEffect } from "@react-navigation/native";



const UserPanel= () => {
   const navigation = useNavigation();
  const [user, setUser] = useState(null);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [loading, setLoading] = useState(true);
const [error, setError] = useState(null);


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


//   useFocusEffect(
//   useCallback(() => {
//     const loadProfile = async () => {
//       try {
//         const token = await AsyncStorage.getItem("hlopgToken");
//         if (!token) {
//           navigation.replace("StudentLogin");
//           return;
//         }

//         const res = await api.get("/auth/userid", {
//           headers: { Authorization: `Bearer ${token}` },
//         });

//         setUser(res.data);
//       } catch (e) {
//         console.log("PROFILE ERROR:", e);
//       }
//     };

//     loadProfile();
//   }, [])
// );

useFocusEffect(
  useCallback(() => {
    let active = true;

    const loadProfile = async () => {
      try {
        const res = await api.get("/auth/userid"); // interceptor adds token
        if (active) setUser(res.data);
      } catch (e) {
  console.log("PROFILE ERROR:", e.response?.data || e.message);

  // 🔥 USER DOES NOT EXIST / TOKEN INVALID
  await AsyncStorage.multiRemove([
    "hlopgToken",
    "hlopgRole",
    "hlopgUser",
    "hlopgOwner",
  ]);

  navigation.replace("Main"); // back to BottomTabs
}
 finally {
        if (active) setLoading(false);
      }
    };

    loadProfile();

    return () => {
      active = false;
    };
  }, [])
);


const BASE_URL =  "http://10.0.2.2:8080";
//  Platform.OS === "ios"
//   ? "http://127.0.0.1:8080"
//   : "http://10.0.2.2:8080";


  const logout = async () => {
    await AsyncStorage.multiRemove(["hlopgToken", "hlopgUser","hlopgRole",]);
    navigation.replace("Main");
  };

//   if (!user) {
//   return (
//     <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
//       <Text>Loading profile...</Text>
//     </View>
//   );
// }

if (loading) {
  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <Text>Loading profile...</Text>
    </View>
  );
}

if (error) {
  return (
    <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
      <Text>{error}</Text>
    </View>
  );
}


  return (
    <ScrollView style={styles.container}>
      {/* HEADER */}
      <Text style={styles.header}>Account</Text>

      {/* PROFILE */}
      <View style={styles.profileBox}>
        <Image
          source={{
            uri: user.profileImage
              ? `${BASE_URL}${user.profileImage}?t=${Date.now()}`
              : "https://cdn-icons-png.flaticon.com/512/4140/4140048.png",
          }}
          style={styles.avatar}
        />

        <View style={styles.nameRow}>
          <Text style={styles.name}>{user.name}</Text>
          <TouchableOpacity onPress={() => navigation.navigate("EditProfile")}>
            <Icon name="pencil" size={18} color="#4f46e5" />
          </TouchableOpacity>
        </View>
      </View>

      {/* MENU */}
      <MenuItem icon="notifications-outline" label="Notifications"
        onPress={() => navigation.navigate("Notifications")} />

      <MenuItem icon="heart-outline" label="Favourite List"
        onPress={() => navigation.navigate("Favourites")} />

      <MenuItem icon="card-outline" label="Payment Details"
        onPress={() => navigation.navigate("Payments")} />

      <MenuItem icon="document-text-outline" label="Terms and Conditions"
        onPress={() => navigation.navigate("Terms")} />

      <MenuItem icon="help-circle-outline" label="Help and Support"
        onPress={() => navigation.navigate("Help")} />

      <MenuItem
  icon="log-out-outline"
  label="Logout"
  danger
  onPress={() => setShowLogoutModal(true)}
/>


    

      {/* LOGOUT CONFIRMATION MODAL */}
<Modal
  visible={showLogoutModal}
  transparent
  animationType="fade"
>
  <View style={styles.modalOverlay}>
    <View style={styles.modalBox}>
      
      {/* Close Icon */}
      <TouchableOpacity
        style={styles.closeIcon}
        onPress={() => setShowLogoutModal(false)}
      >
        <Icon name="close" size={22} color="#6b7280" />
      </TouchableOpacity>

      <Text style={styles.modalTitle}>Logout</Text>
      <Text style={styles.modalText}>
        Are you sure you want to logout?
      </Text>

      <View style={styles.modalButtons}>
        <TouchableOpacity
          style={styles.cancelBtn}
          onPress={() => setShowLogoutModal(false)}
        >
          <Text style={styles.cancelText}>Cancel</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.logoutBtn}
          onPress={async () => {
            setShowLogoutModal(false);
            await logout();
          }}
        >
          <Text style={styles.logoutText}>Logout</Text>
        </TouchableOpacity>
      </View>
    </View>
  </View>
</Modal>

    </ScrollView>
  );
};

export default UserPanel;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#ffffff",
    padding: 15,
  },
  header: {
    fontSize: 18,
    fontWeight: "600",
    textAlign: "center",
    marginBottom: 10,
    marginTop:10,
    backgroundColor:"white"
  },
  profileBox: {
    padding: 10,
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
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#e5e7eb",
    
   
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
  version: {
    textAlign: "center",
    color: "#9ca3af",
    marginTop: 30,
  },

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
  position: "relative",
},
closeIcon: {
  position: "absolute",
  top: 12,
  right: 12,
},
modalTitle: {
  fontSize: 18,
  fontWeight: "600",
  marginBottom: 10,
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
  borderRadius: 8,
  backgroundColor: "#e5e7eb",
},
cancelText: {
  color: "#111827",
  fontWeight: "500",
},
logoutBtn: {
  paddingVertical: 8,
  paddingHorizontal: 16,
  borderRadius: 8,
  backgroundColor: "#ef4444",
},
logoutText: {
  color: "#fff",
  fontWeight: "500",
},

});
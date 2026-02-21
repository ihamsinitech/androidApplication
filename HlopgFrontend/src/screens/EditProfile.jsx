import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
  Alert,
  ActivityIndicator,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { launchImageLibrary } from "react-native-image-picker";
import { Picker } from "@react-native-picker/picker";
import Icon from "react-native-vector-icons/Ionicons";
import api from "../api";
import { Platform } from "react-native";


const EditProfile = ({ navigation }) => {
  const [user, setUser] = useState(null);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [gender, setGender] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);

  // Fetch user
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = await AsyncStorage.getItem("hlopgToken");
        const res = await api.get("/auth/userid", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUser(res.data);
        setName(res.data.name);
        setEmail(res.data.email);
        setGender(res.data.gender);
      } catch {
        Alert.alert("Error", "Failed to load profile");
      }
    };
    fetchUser();
  }, []);

  // Pick image
//   const handleImagePick = async () => {
//   launchImageLibrary({ mediaType: "photo" }, async (res) => {
//     if (res.didCancel || !res.assets) return;

//     try {
//       setUploading(true);
//       const token = await AsyncStorage.getItem("hlopgToken");

//       const formData = new FormData();
//       formData.append("profileImage", {
//         uri: res.assets[0].uri,
//         type: res.assets[0].type,
//         name: res.assets[0].fileName || "profile.jpg",
//       });

//       const uploadRes = await api.post(
//         "/auth/update-profile-image",
//         formData,
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             "Content-Type": "multipart/form-data",
//           },
//         }
//       );

//       // ✅ update UI with new image
//       setUser((prev) => ({
//         ...prev,
//         profileImage: uploadRes.data.data.profileImage,
//       }));

//     } catch (err) {
//       Alert.alert("Error", "Image upload failed");
//     } finally {
//       setUploading(false);
//     }
//   });
// };
const handleImagePick = () => {
  launchImageLibrary(
    {
      mediaType: "photo",
      quality: 0.5,
maxWidth: 800,
maxHeight: 800,

      selectionLimit: 1,
      includeExtra: true, // 🔥 REQUIRED FOR iOS SIMULATOR
    },
    async (res) => {
      if (res.didCancel || !res.assets?.length) return;

      try {
        setUploading(true);

        const asset = res.assets[0];

        // ✅ FIX FOR iOS SIMULATOR (ph:// issue)
        const imageUri =
          Platform.OS === "ios" && asset.uri?.startsWith("ph://")
            ? asset.fileCopyUri
            : asset.uri;

        if (!imageUri) {
          Alert.alert("Error", "Image path not found");
          return;
        }

        const token = await AsyncStorage.getItem("hlopgToken");

        const formData = new FormData();
        formData.append("profileImage", {
          uri: imageUri,
          type: asset.type || "image/jpeg",
          name: asset.fileName || `profile_${Date.now()}.jpg`,
        });

        const uploadRes = await api.post(
          "/auth/update-profile-image",
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );

        // ✅ Update UI immediately
        setUser((prev) => ({
          ...prev,
          profileImage: uploadRes.data.data.profileImage,
        }));

        Alert.alert("Success", "Profile image updated");
      } catch (err) {
        console.log("UPLOAD ERROR:", err.response?.data || err.message);
        Alert.alert("Error", "Image upload failed");
      } finally {
        setUploading(false);
      }
    }
  );
};


  // Save profile
  const handleSave = async () => {
  if (!name || !email || !gender) {
    Alert.alert("Validation", "All fields are required");
    return;
  }

  try {
    setLoading(true);
    const token = await AsyncStorage.getItem("hlopgToken");

    const payload = {
      name,
      email,
      gender,
    };

    // 🔐 password only if user typed
    if (password && password.trim().length >= 6) {
      payload.password = password;
    }

    await api.put("/auth/update-profile", payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    Alert.alert("Success", "Profile updated successfully");
    navigation.goBack();
  } catch (err) {
    Alert.alert("Error", err.response?.data?.message || "Update failed");
  } finally {
    setLoading(false);
  }
};



  if (!user) return <ActivityIndicator style={{ marginTop: 80 }} size="large" />;

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Icon name="arrow-back" size={24} color="#111827"/>
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Edit Profile</Text>
      </View>

      {/* PROFILE IMAGE */}
      <TouchableOpacity onPress={handleImagePick} style={styles.avatarWrap}>
        <Image
  source={{
    uri: user.profileImage
      ? `http://10.0.2.2:8080${user.profileImage}`
      : "https://cdn-icons-png.flaticon.com/512/4140/4140048.png",
  }}
  style={styles.avatar}
/>

        <Icon name="camera" size={18} color="#fff" style={styles.cameraIcon} />
      </TouchableOpacity>
      {uploading && <Text style={styles.uploading}>Uploading...</Text>}

      {/* FORM */}
      <View style={styles.form}>
        <Text style={styles.label}>Username</Text>
        <TextInput style={styles.input} value={name} onChangeText={setName} />

        <Text style={styles.label}>Email</Text>
        <TextInput
          style={styles.input}
          value={email}
          onChangeText={setEmail}
  autoCapitalize="none"
  keyboardType="email-address"
        />

        <Text style={styles.label}>Gender</Text>
<TextInput
  style={styles.input}
  value={gender}
  onChangeText={setGender}
/>



        <Text style={styles.label}>Password</Text>
        <View style={styles.passwordBox}>
          <TextInput
            style={styles.passwordInput}
            secureTextEntry={!showPassword}
            value={password}
            onChangeText={setPassword}
            placeholder="********"
          />
          <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
            <Icon
              name={showPassword ? "eye-off" : "eye"}
              size={20}
              color="#6b7280"
            />
          </TouchableOpacity>
        </View>

        <TouchableOpacity style={styles.saveBtn} onPress={handleSave}>
          <Text style={styles.saveText}>
            {loading ? "Saving..." : "Save Changes"}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default EditProfile;


const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 20 },

  header: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 10,
    gap: 12,
  },
  headerTitle: { fontSize: 20, fontWeight: "500", color: "#111827" ,marginLeft:100},

  avatarWrap: { alignSelf: "center", marginTop: 10 },
  avatar: { width: 110, height: 110, borderRadius: 55 },
  cameraIcon: {
    position: "absolute",
    bottom: 6,
    right: 6,
    backgroundColor: "#4f46e5",
    borderRadius: 14,
    padding: 4,
  },

  uploading: { textAlign: "center", marginTop: 6, color: "#4f46e5" },

  form: { marginTop: 30 },

  label: { fontSize: 13, color: "#6b7280", marginBottom: 4 },

  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    padding: 12,
    marginBottom: 16,
    fontSize: 15,
  },

  readonly: { backgroundColor: "#f3f4f6", color: "#6b7280" },

  dropdown: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 8,
    marginBottom: 16,
    height: 48,
  },

  picker: { flex: 1 },

  passwordBox: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    paddingHorizontal: 12,
    marginBottom: 24,
    height: 48,
  },

  passwordInput: { flex: 1, fontSize: 15 },

  saveBtn: {
    backgroundColor: "#4f46e5",
    paddingVertical: 14,
    borderRadius: 10,
    alignItems: "center",
  },

  saveText: { color: "#fff", fontSize: 16, fontWeight: "600" },
});
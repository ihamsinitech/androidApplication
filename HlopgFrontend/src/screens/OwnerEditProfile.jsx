import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Image,
  ActivityIndicator,
} from "react-native";
import Icon from "react-native-vector-icons/Ionicons";
import { launchImageLibrary } from "react-native-image-picker";
import api from "../api";
import { Platform } from "react-native";

const OwnerEditProfile = ({ navigation }) => {
  const [owner, setOwner] = useState(null);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const BASE_URL = "http://10.0.2.2:8080";
    // Platform.OS === "ios"
    //   ? "http://127.0.0.1:8080"
    //   : "http://10.0.2.2:8080";

  useEffect(() => {
  const loadOwner = async () => {
    try {
      const res = await api.get("/auth/owner/profile");
      setOwner(res.data);
      setName(res.data.name);
      setEmail(res.data.email);
    } catch (e) {
      console.log("EDIT OWNER LOAD ERROR", e.response?.status);
    }
  };

  loadOwner();
}, []);


//   const pickImage = () => {
//     launchImageLibrary({ mediaType: "photo", quality: 0.6 }, async (res) => {
//       if (res.didCancel || !res.assets?.length) return;

//       const formData = new FormData();
//       formData.append("profileImage", {
//         uri: res.assets[0].uri,
//         type: "image/jpeg",
//         name: "profile.jpg",
//       });
//       const upload = await api.post(
//         // "/auth/update-owner-profile-image",
//         "/auth/owner/update-profile-image",

//         formData
//         // { headers: { "Content-Type": "multipart/form-data" } }
//       );
//       setOwner((p) => ({ ...p, profileImage: upload.data.data.profileImage }));
//     });
//   };

const pickImage = () => {
  launchImageLibrary({ mediaType: "photo", quality: 0.6 }, async (res) => {
    if (res.didCancel || !res.assets?.length) return;

    const formData = new FormData();
    formData.append("profileImage", {
      uri: res.assets[0].uri,
      type: res.assets[0].type || "image/jpeg",
      name: res.assets[0].fileName || "profile.jpg",
    });

    try {
      const upload = await api.post(
        "/auth/owner/update-profile-image",
        formData
      );

      setOwner((prev) => ({
        ...prev,
        profileImage: upload.data.data.profileImage,
      }));

    } catch (e) {
      console.log("IMAGE UPLOAD ERROR", e.response?.status, e.response?.data);
    }
  });
};

// const pickImage = () => {
//   launchImageLibrary(
//     {
//       mediaType: "photo",
//       quality: 0.6,
//     },
//     async (res) => {
//       if (res.didCancel || !res.assets?.length) return;

//       const asset = res.assets[0];

//       console.log("ANDROID IMAGE OBJECT:", asset);

//       const formData = new FormData();

//       formData.append("image", {   // ✅ MATCH BACKEND
//         uri: asset.uri,
//         type: asset.type || "image/jpeg",
//         name: asset.fileName || `photo_${Date.now()}.jpg`,
//       });

//       try {
//         const upload = await api.post(
//           "/auth/owner/update-profile-image",
//           formData
//           // ❌ DO NOT SET HEADERS
//         );

//         console.log("UPLOAD SUCCESS:", upload.data);

//         setOwner((prev) => ({
//           ...prev,
//           profileImage: upload.data.data.profileImage,
//         }));

//       } catch (e) {
//         console.log("UPLOAD ERROR MESSAGE:", e.message);
//         console.log("UPLOAD ERROR STATUS:", e.response?.status);
//         console.log("UPLOAD ERROR DATA:", e.response?.data);
//       }
//     }
//   );
// };

  const saveProfile = async () => {
    const payload = { name, email };
    if (password) payload.password = password;
    await api.put("/auth/owner/update-profile", payload);
    navigation.goBack();
  };

  if (!owner) return <ActivityIndicator style={{ marginTop: 80 }} size="large" />;

  return (
    <View style={edit.container}>
      <View style={edit.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Icon name="arrow-back" size={24} />
        </TouchableOpacity>
        <Text style={edit.headerTitle}>Edit Profile</Text>
      </View>

      <TouchableOpacity style={edit.avatarWrap} onPress={pickImage}>
        <Image
          source={{
            uri: owner.profileImage
              ? `${BASE_URL}${owner.profileImage}?t=${Date.now()}`
              : "https://cdn-icons-png.flaticon.com/512/4140/4140048.png",
          }}
          style={edit.avatar}
        />
        <Icon name="camera" size={18} color="#fff" style={edit.cameraIcon} />
      </TouchableOpacity>

      <View style={edit.form}>
        <TextInput style={edit.input} value={name} onChangeText={setName} />
        <TextInput
          style={edit.input}
          value={email}
          onChangeText={setEmail}
        />

        <View style={edit.passwordBox}>
          <TextInput
            style={edit.passwordInput}
            secureTextEntry={!showPassword}
            value={password}
            onChangeText={setPassword}
            placeholder="Password"
          />
          <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
            <Icon name={showPassword ? "eye-off" : "eye"} size={20} />
          </TouchableOpacity>
        </View>

        <TouchableOpacity style={edit.saveBtn} onPress={saveProfile}>
          <Text style={edit.saveText}>Save Changes</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default OwnerEditProfile;

/* ============ STYLES ============ */

const edit = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 20 },
  header: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginTop: 5,

  },
  headerTitle: { fontSize: 18, fontWeight: "600" ,marginLeft:100},
  avatarWrap: { alignSelf: "center", marginTop: 20 },
  avatar: { width: 110, height: 110, borderRadius: 55 },
  cameraIcon: {
    position: "absolute",
    bottom: 6,
    right: 6,
    backgroundColor: "#4f46e5",
    borderRadius: 14,
    padding: 4,
  },
  form: { marginTop: 30 },
  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    padding: 12,
    marginBottom: 16,
  },
  passwordBox: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    paddingHorizontal: 12,
    marginBottom: 24,
    height:50
  },
  passwordInput: { flex: 1 },
  saveBtn: {
    backgroundColor: "#4f46e5",
    paddingVertical: 14,
    borderRadius: 10,
    alignItems: "center",
  },
  saveText: { color: "#fff", fontWeight: "600", fontSize: 16 },
});
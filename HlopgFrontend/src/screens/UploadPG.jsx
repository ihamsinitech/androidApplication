

import React, { useState ,useEffect} from "react";
import {
  View,
  Text,
  TextInput,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Image,
  Alert,
} from "react-native";
import { launchImageLibrary } from "react-native-image-picker";
import Ionicons from "react-native-vector-icons/Ionicons";
import AsyncStorage from "@react-native-async-storage/async-storage";
import api from "../api";
import { useRoute,useNavigation } from "@react-navigation/native";
const BASE_URL = api.defaults.baseURL.replace("/api", "");




const AMENITIES = [
  { key: "Free Wifi", icon: "wifi-outline" },
  { key: "bed", icon: "bed-outline" },
  { key: "fan", icon: "sync-outline" },
  { key: "food", icon: "restaurant-outline" },
  { key: "ac", icon: "snow-outline" },
  { key: "washing", icon: "water-outline" },
  { key: "lights", icon: "bulb-outline" },
  { key: "cupboard", icon: "file-tray-outline" },
  { key: "geyser", icon: "flame-outline" },
  { key: "water", icon: "water-outline" },
  { key: "fridge", icon: "cube-outline" },
  { key: "gym", icon: "barbell-outline" },
  { key: "tv", icon: "tv-outline" },
  { key: "parking", icon: "car-outline" },
  { key: "lift", icon: "arrow-up-outline" },
  { key: "cams", icon: "videocam-outline" },
  { key: "self cooking", icon: "fast-food-outline" },
];



const UploadPG = () => {
  const [pgName, setPgName] = useState("");
  const [pgInfo, setPgInfo] = useState("");
  const [pgType, setPgType] = useState("");
  const [images, setImages] = useState([]);
  const [foodMenu, setFoodMenu] = useState({});
const [sharing, setSharing] = useState({});
const [numFloors, setNumFloors] = useState("");
const [roomsPerFloor, setRoomsPerFloor] = useState("");
const [startingRoom, setStartingRoom] = useState("");
const [advance, setAdvance] = useState("");
const navigation = useNavigation();




  const route = useRoute();
// const isEdit = route.params?.mode === "edit";
const isEdit = route.name === "EditPG";
const hostelId = route.params?.hostelId;

  const [location, setLocation] = useState({
  address: "",
  city: "",
  area: "",
  pincode: "",
});


  const [amenities, setAmenities] = useState([]);

  const toggleItem = value => {
    setAmenities(prev =>
      prev.includes(value)
        ? prev.filter(i => i !== value)
        : [...prev, value]
    );
  };

  const DAYS = [
  { label: "MON-D", key: "monday" },
  { label: "TUES-D", key: "tuesday" },
  { label: "WED-D", key: "wednesday" },
  { label: "THUR-D", key: "thursday" },
  { label: "FRI-D", key: "friday" },
  { label: "SAT-D", key: "saturday" },
  { label: "SUN-D", key: "sunday" },
];


  const isFormValid =
  pgName.trim() &&
  pgInfo.trim() &&
  pgType &&
  location.city.trim() &&
  location.area.trim() &&
  location.pincode.trim() &&
  location.address.trim() &&
  images.length > 0 &&
  amenities.length > 0;



  /* IMAGE PICKER */
  const pickImages = async () => {
    const res = await launchImageLibrary({
      mediaType: "photo",
      selectionLimit: 5,
      quality: 0.7,
    });
    if (res.assets) setImages(res.assets);
  };

  
  const handleSubmit = async () => {
  try {
    const token = await AsyncStorage.getItem("hlopgToken");

   

    // const sharing = {
    //   "2-Sharing": 6000,
    //   "3-Sharing": 5000,
    //   "4-Sharing": 4000,
    // };

    const formData = new FormData();

    formData.append("pgName", pgName);
    formData.append("pgInfo", pgInfo);
    formData.append("pgType", pgType);

    formData.append("city", location.city);
    formData.append("area", location.area);
    formData.append("pincode", location.pincode);
    formData.append("address", location.address);

    formData.append(
  "amenities",
  JSON.stringify(
    amenities.reduce((o, a) => ({ ...o, [a]: true }), {})
  )
);


    formData.append("foodMenu", JSON.stringify(foodMenu));
    formData.append("sharing", JSON.stringify(sharing));

   formData.append("numFloors", numFloors);
formData.append("roomsPerFloor", roomsPerFloor);
formData.append("startingRoomNumber", startingRoom);

    formData.append("advanceAmount", advance);


    images.forEach(img => {
      formData.append("images", {
        uri: img.uri,
        name: img.fileName || "pg.jpg",
        type: img.type || "image/jpeg",
      });
    });

    // await api.post("/hostel/addhostel/ios", formData, {
    //   headers: {
    //     Authorization: `Bearer ${token}`,
    //     "Content-Type": "multipart/form-data",
    //   },
    // });

    const url = isEdit
  ? `/hostel/update/${hostelId}`
  : "/hostel/addhostel/ios";

const method = isEdit ? api.put : api.post;

await method(url, formData, {
  headers: {
    Authorization: `Bearer ${token}`,
    "Content-Type": "multipart/form-data",
  },
});


    Alert.alert(
  "Success",
  isEdit ? "PG updated successfully" : "PG uploaded successfully"
);

  } catch (e) {
    console.log(e.response?.data || e.message);
    Alert.alert("Error", "Upload failed");
  }
};



useEffect(() => {
  if (!isEdit || !hostelId) return;

  const loadPG = async () => {
    try {
      const token = await AsyncStorage.getItem("hlopgToken");

      const res = await api.get(`/hostel/${hostelId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const pg = res.data.data;

      setPgName(pg.hostel_name || "");
      setPgInfo(pg.description || "");
      setPgType(pg.pg_type || "");

      setLocation({
        city: pg.city || "",
        area: pg.area || "",
        pincode: pg.pincode || "",
        address: pg.address || "",
      });

      setAmenities(Object.keys(pg.facilities || {}));
      setFoodMenu(pg.food_menu || {});
      setSharing(pg.sharing_data || {});
    

     setNumFloors(pg.numFloors?.toString() || "");
setRoomsPerFloor(pg.roomsPerFloor?.toString() || "");
setStartingRoom(pg.startingRoom?.toString() || "");


      setAdvance(pg.advance?.toString() || "");

      setImages(
        (pg.images || []).map(img => ({
          uri: `${BASE_URL}${img}`,
        }))
      );
    } catch (e) {
      console.log("Edit PG load error", e);
      Alert.alert("Error", "Failed to load PG details");
    }
  };

  loadPG();
}, [isEdit, hostelId]);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.headerRow}>
  <TouchableOpacity onPress={() => navigation.goBack()}>
    <Ionicons name="arrow-back" size={24} color="#111827" />
  </TouchableOpacity>

  <Text style={styles.headerTitle}>
    {isEdit ? "Edit PG Details" : "Upload PG Details"}
  </Text>
</View>

      


      {/* PG NAME */}
      <Text>PG Name</Text>
      <TextInput style={styles.input} value={pgName} onChangeText={setPgName} />

      {/* PG INFO */}
      <Text>PG Information</Text>
      <TextInput style={styles.input} value={pgInfo} onChangeText={setPgInfo} />

      {/* PG TYPE */}
      <Text style={styles.label}>PG Type</Text>
      <View style={styles.row}>
        {["Men", "Women", "Co-Living"].map(t => (
          <TouchableOpacity
            key={t}
            style={[styles.chip, pgType === t && styles.chipActive]}
            onPress={() => setPgType(t)}
          >
            <Text style={pgType === t && styles.chipTextActive}>{t}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* LOCATION */}
      <Text style={styles.label}>Enter PG Location</Text>
<Text>Enter City</Text>
      <TextInput
  style={styles.input}
  value={location.city}
  onChangeText={v => setLocation({ ...location, city: v })}
/>

<Text>Enter Area</Text>
<TextInput
  style={styles.input}
  value={location.area}
  onChangeText={v => setLocation({ ...location, area: v })}
/>

<Text>Enter Pincode</Text>
<TextInput
  style={styles.input}
  keyboardType="number-pad"
  maxLength={6}
  value={location.pincode}
  onChangeText={v => setLocation({ ...location, pincode: v })}
/>

<Text>Enter Location</Text>
<TextInput
  style={styles.input}
  value={location.address}
  onChangeText={v => setLocation({ ...location, address: v })}
/>

      {/* IMAGES */}
      <Text style={styles.label}>PG Images</Text>
      <View style={styles.imageRow}>
        {images.map((img, i) => (
          <Image key={i} source={{ uri: img.uri }} style={styles.image} />
        ))}
        <TouchableOpacity style={styles.addImage} onPress={pickImages}>
          <Ionicons name="add" size={28} />
        </TouchableOpacity>
      </View>

      
      {/* FOOD MENU */}
      <Text style={styles.foodTitle}>Food Menu</Text>

  {/* Header Row */}
  <View style={styles.foodContainer}>
  <View style={styles.foodHeaderRow}>
    <View style={[styles.headerCell, styles.dayHeader]}>
      <Text style={styles.headerText}>DAY</Text>
    </View>
    <View style={[styles.headerCell, styles.breakfastHeader]}>
      <Text style={styles.headerText}>BREAK FAST</Text>
    </View>
    <View style={[styles.headerCell, styles.lunchHeader]}>
      <Text style={styles.headerText}>LUNCH</Text>
    </View>
    <View style={[styles.headerCell, styles.dinnerHeader]}>
      <Text style={styles.headerText}>DINNER</Text>
    </View>
  </View>


{DAYS.map(({ label, key }) => (
  <View key={key} style={styles.foodRow}>
    <View style={styles.dayCell}>
      <Text style={styles.dayText}>{label}</Text>
    </View>

    <TextInput
      style={styles.breakfastCell}
      value={foodMenu?.[key]?.breakfast || ""}
      onChangeText={v =>
        setFoodMenu(prev => ({
          ...prev,
          [key]: { ...prev[key], breakfast: v },
        }))
      }
    />

    <TextInput
      style={styles.lunchCell}
      value={foodMenu?.[key]?.lunch || ""}
      onChangeText={v =>
        setFoodMenu(prev => ({
          ...prev,
          [key]: { ...prev[key], lunch: v },
        }))
      }
    />

    <TextInput
      style={styles.dinnerCell}
      value={foodMenu?.[key]?.dinner || ""}
      onChangeText={v =>
        setFoodMenu(prev => ({
          ...prev,
          [key]: { ...prev[key], dinner: v },
        }))
      }
    />
  </View>
))}

</View>


      {/* AMENITIES (ICON + NAME) */}
      {/* AMENITIES */}
<Text style={styles.labels}>Amenities</Text>

<View style={styles.amenitiesRow}>
  {AMENITIES.map(a => {
    const selected = amenities.includes(a.key);
    return (
      <TouchableOpacity
        key={a.key}
        style={styles.amenityItem}
        onPress={() => toggleItem(a.key)}
        activeOpacity={0.7}
      >
        <Ionicons
          name={a.icon}
          size={24}
          color={selected ? "#2563eb" : "#9ca3af"}
        />
        <Text
          style={[
            styles.amenityLabel,
            selected && styles.amenityLabelActive,
          ]}
        >
          {a.key}
        </Text>
      </TouchableOpacity>
    );
  })}
</View>


  {/* Select PG Floors and Rooms*/}
 {/* PG FLOORS & ROOMS */}
<Text style={styles.mainTitle}>Select PG Floors and Rooms</Text>

<View style={styles.blueBox}>
  <View style={styles.sideRow}>
    <Text style={styles.sideLabel}>
      1. Number Floors In your building
    </Text>
    <TextInput
  style={styles.sideInput}
  keyboardType="number-pad"
  value={numFloors}
  onChangeText={setNumFloors}
/>
  </View>

  <View style={styles.sideRow}>
    <Text style={styles.sideLabel}>
      2. Number Rooms In a Floor
    </Text>
    <TextInput
  style={styles.sideInput}
  keyboardType="number-pad"
  value={roomsPerFloor}
  onChangeText={setRoomsPerFloor}
/>
  </View>

  <View style={styles.sideRow}>
    <Text style={styles.sideLabel}>
      3. Enter Starting Room number
    </Text>
    <TextInput
  style={styles.sideInput}
  keyboardType="number-pad"
  value={startingRoom}
  onChangeText={setStartingRoom}
/>
  </View>
</View>





      {/* SHARING */}
      <Text style={styles.mainTitle}>Enter Sharing price</Text>

<View style={styles.sharingRow}>
 {["2-Sharing", "3-Sharing", "4-Sharing", "5-Sharing"].map(key => (
  <View key={key} style={styles.shareBox}>
    <Text style={styles.shareLabel}>{key}</Text>
    <TextInput
      style={styles.shareInput}
      keyboardType="number-pad"
      value={sharing[key]?.toString() || ""}
      onChangeText={v =>
        setSharing(prev => ({ ...prev, [key]: v }))
      }
    />
  </View>
))}

</View>


      
        {/* Advance*/}
      <Text style={styles.mainTitle}>Enter Advance Amount</Text>

<TextInput
  style={styles.advanceInput}
  keyboardType="number-pad"
  value={advance}
  onChangeText={setAdvance}
/>



    

      {/* SUBMIT */}
      <TouchableOpacity
  style={[
    styles.submitBtn,
    !isFormValid && styles.submitBtnDisabled,
  ]}
  disabled={!isFormValid}
  onPress={handleSubmit}
>
  <Text
    style={[
      styles.submitText,
      !isFormValid && styles.submitTextDisabled,
    ]}
  >
    {isEdit ? "Update PG" : "Upload PG"}

  </Text>
</TouchableOpacity>

    </ScrollView>
  );
};

export default UploadPG;


const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: "#fff",
    
  },

  title: {
    fontSize: 22,
    fontWeight: "700",
    marginBottom: 16,
  },

  label: {
    fontWeight: "600",
    marginTop: 16,
    marginBottom:10
  },

  input: {
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 8,
    padding: 12,
    marginTop: 10,
    marginBottom:10
  },

  row: {
    flexDirection: "row",
    flexWrap: "wrap",
    marginTop: 8,
  },

  chip: {
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#d1d5db",
    marginRight: 8,
    marginBottom: 8,
  },

  chipActive: {
    backgroundColor: "#4f46e5",
    borderColor: "#4f46e5",
  },

  chipTextActive: {
    color: "#fff",
  },

  imageRow: {
    flexDirection: "row",
    marginTop: 10,
  },

  image: {
    width: 70,
    height: 70,
    borderRadius: 8,
    marginRight: 8,
  },

  addImage: {
    width: 70,
    height: 70,
    borderWidth: 1,
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
  },

/* AMENITIES – ICON ONLY */
labels: {
  fontSize:16,
  fontWeight:500,
  marginTop:20
},

amenitiesRow: {
  flexDirection: "row",
  flexWrap: "wrap",
  justifyContent: "space-between",
  marginTop: 12,
},

amenityItem: {
  width: "22%",            // 4 icons per row
  alignItems: "center",
  marginBottom: 18,
},

amenityLabel: {
  marginTop: 6,
  fontSize: 12,
  textAlign: "center",
  color: "#9ca3af",        // inactive gray
},

amenityLabelActive: {
  color: "#2563eb",        // active blue
  fontWeight: "600",
},


  submitBtn: {
    backgroundColor: "#4f46e5",
    padding: 16,
    borderRadius: 10,
    alignItems: "center",
    marginTop: 20,
    marginBottom:110,
  },

  submitText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "600",
  },

  submitBtnDisabled: {
  backgroundColor: "#c7c7c7",
},

submitTextDisabled: {
  color: "#6b7280",
},



/* FOOD MENU TABLE */

foodContainer: {
  marginTop: 10,
  padding: 12,
  borderRadius: 12,
  borderWidth: 1,
  borderColor: "#e5e7eb",
  backgroundColor: "#ffffff",
},

foodTitle: {
  fontSize: 18,
  fontWeight: "700",
  marginBottom: 1,
  color: "#111827",
  marginTop:25
},

foodHeaderRow: {
  flexDirection: "row",
  marginBottom: 10,
},

headerCell: {
  flex: 1,
  height: 45,
  justifyContent: "center",
  alignItems: "center",
  borderRadius: 6,
  marginHorizontal: 3,
},

dayHeader: {
  backgroundColor: "#b9b2e6",
  flex: 0.8,
},

breakfastHeader: {
  backgroundColor: "#7fbfa4",
},

lunchHeader: {
  backgroundColor: "#ee8f77",
},

dinnerHeader: {
  backgroundColor: "#d96bb3",
},

headerText: {
  color: "#ffffff",
  fontWeight: "600",
  fontSize: 12,
},

foodRow: {
  flexDirection: "row",
  marginBottom: 10,
},

dayCell: {
  flex: 0.8,
  backgroundColor: "#e5e5ea",
  borderRadius: 6,
  justifyContent: "center",
  alignItems: "center",
  height: 48,
  marginHorizontal: 3,
},

dayText: {
  fontSize: 12,
  fontWeight: "600",
  color: "#111827",
},

breakfastCell: {
  flex: 1,
  backgroundColor: "#e7fbf2",
  borderRadius: 6,
  height: 48,
  marginHorizontal: 3,
  paddingHorizontal: 6,
  fontSize: 12,
  textAlign:"center"
},

lunchCell: {
  flex: 1,
  backgroundColor: "#fdecea",
  borderRadius: 6,
  height: 48,
  marginHorizontal: 3,
  paddingHorizontal: 6,
  fontSize: 12,
  textAlign:"center"
},

dinnerCell: {
  flex: 1,
  backgroundColor: "#f6def4",
  borderRadius: 6,
  height: 48,
  marginHorizontal: 3,
  paddingHorizontal: 6,
  fontSize: 12,
  textAlign:"center"
},

/* COMMON TITLES */
mainTitle: {
  fontSize: 18,
  fontWeight: "600",
  marginTop: 24,
  marginBottom: 14,
  color: "#111827",
},

/* SECTION 1 – BLUE BOX */
blueBox: {
  backgroundColor: "#f1f8ff",
  borderRadius: 12,
  padding: 16,
},

sideRow: {
  flexDirection: "row",
  alignItems: "center",
  justifyContent: "space-between",
  marginBottom: 18,
},

sideLabel: {
  width: "70%",
  fontSize: 14,
  color: "#111827",
},

sideInput: {
  width: "25%",
  height: 42,
  backgroundColor: "#ffffff",
  borderRadius: 6,
  textAlign: "center",
  borderWidth: 1,
  borderColor: "#e5e7eb",
},

/* SECTION 2 – SHARING */
sharingRow: {
  flexDirection: "row",
  justifyContent: "space-between",
},

shareBox: {
  width: "23%",
},

shareLabel: {
  fontSize: 13,
  marginBottom: 6,
  textAlign: "center",
  color: "#111827",
},

shareInput: {
  height: 42,
  backgroundColor: "#e5e5e5",
  borderRadius: 6,
  textAlign: "center",
  fontSize: 12,
},

/* SECTION 3 – ADVANCE */
advanceInput: {
  width: "40%",
  height: 44,
  backgroundColor: "#e5e5e5",
  borderRadius: 6,
  marginBottom: 30,
  textAlign: "center",
},


headerRow: {
  flexDirection: "row",
  alignItems: "center",
  gap: 12,
  marginBottom: 25,
},
headerTitle: {
  fontSize: 22,
  fontWeight: "700",
  marginLeft:60,

},

});
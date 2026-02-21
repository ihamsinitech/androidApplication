

import React, { useEffect, useRef, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  Dimensions,
  TextInput,
  Platform,
  FlatList,
  Pressable,
  Alert,
  Linking,
} from "react-native";

import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";
import FontAwesome5 from "react-native-vector-icons/FontAwesome5";
import api from "../api";
import AsyncStorage from "@react-native-async-storage/async-storage";



// import Geolocation from "@react-native-community/geolocation";
// import Geolocation from "react-native-geolocation-service";



const { width } = Dimensions.get("window");
const CARD_WIDTH = (width - 48) / 2;
const BASE_URL = api.defaults.baseURL.replace("/api", "");

const banners = [
  require("../assets/home-banner.jpg"),
  require("../assets/home-banner.jpg"),
  require("../assets/home-banner.jpg"),
];

const Home = () => {
  const navigation = useNavigation();
  const bannerRef = useRef(null);

  const [bannerIndex, setBannerIndex] = useState(0);
  const [pgList, setPgList] = useState([]);
  const [likedMap, setLikedMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [likeLoading, setLikeLoading] = useState({});
  const [searchText, setSearchText] = useState("");
const [filteredPgList, setFilteredPgList] = useState([]);

// /* ================= LOCATION ================= */
//   useEffect(() => {
//     if (Platform.OS === "ios") {
//       // Ask permission once
//       Geolocation.requestAuthorization();

//       // Get live location
//       Geolocation.getCurrentPosition(
//         (position) => {
//           console.log("LIVE LOCATION:", position.coords);

//           const { latitude, longitude } = position.coords;

//           // 👉 later you can send this to backend
//           // api.post("/location", { latitude, longitude });
//         },
//         (error) => {
//           console.log("LOCATION ERROR:", error);

//           if (error.code === 1) {
//             Alert.alert(
//               "Location Required",
//               "Enable location to see nearby PGs",
//               [
//                 { text: "Cancel", style: "cancel" },
//                 {
//                   text: "Open Settings",
//                   onPress: () => Linking.openSettings(),
//                 },
//               ]
//             );
//           }
//         },
//         {
//           enableHighAccuracy: true,
//           timeout: 15000,
//           maximumAge: 10000,
//         }
//       );
//     }
//   }, []);


  /* ---------------- AUTO SLIDE BANNER ---------------- */
  useEffect(() => {
    const interval = setInterval(() => {
      const next = (bannerIndex + 1) % banners.length;
      bannerRef.current?.scrollTo({ x: next * width, animated: true });
      setBannerIndex(next);
    }, 3000);

    return () => clearInterval(interval);
  }, [bannerIndex]);

  /* ---------------- FETCH HOSTELS ---------------- */
  useEffect(() => {
    fetchHostels();
  }, []);

  const fetchHostels = async () => {
    try {
      const res = await api.get("/hostel/gethostels");
      const hostels = res.data?.hostels || [];
      setPgList(hostels);
      setFilteredPgList(hostels);
      fetchLikeStatus(hostels);
    } catch (e) {
      console.log("HOME FETCH ERROR", e);
    } finally {
      setLoading(false);
    }
  };

  /* ---------------- LIKE STATUS ---------------- */
  const fetchLikeStatus = async (hostels) => {
  try {
    const map = {};

    await Promise.all(
      hostels.map(async (h) => {
        try {
          const res = await api.get(`/hostel/check-like/${h.hostel_id}`);
          map[h.hostel_id] = res.data.liked === true;
        } catch {
          map[h.hostel_id] = false;
        }
      })
    );

    setLikedMap(map);
  } catch (e) {
    console.log("LIKE STATUS ERROR", e);
  }
};


  const toggleLike = async (hostelId) => {
  // prevent double-tap spam
  if (likeLoading[hostelId]) return;

  // optimistic UI update
  setLikedMap((prev) => ({
    ...prev,
    [hostelId]: !prev[hostelId],
  }));

  setLikeLoading((prev) => ({
    ...prev,
    [hostelId]: true,
  }));

  try {
    const res = await api.post("/hostel/like-hostel", {
      hostel_id: hostelId,
    });

    // backend is the source of truth
    setLikedMap((prev) => ({
      ...prev,
      [hostelId]: res.data.liked === true,
    }));
  } catch (e) {
    console.log("LIKE ERROR", e.response?.data || e.message);

    // rollback if API fails
    setLikedMap((prev) => ({
      ...prev,
      [hostelId]: !prev[hostelId],
    }));
  } finally {
    setLikeLoading((prev) => ({
      ...prev,
      [hostelId]: false,
    }));
  }
};

const handleSearch = (text) => {
  setSearchText(text);

  if (!text.trim()) {
    setFilteredPgList(pgList);
    return;
  }

  const lowerText = text.toLowerCase();

  const filtered = pgList.filter((pg) =>
    pg.hostel_name?.toLowerCase().includes(lowerText) ||
    pg.area?.toLowerCase().includes(lowerText) ||
    pg.city?.toLowerCase().includes(lowerText)
  );

  setFilteredPgList(filtered);
};



  /* ---------------- PG CARD ---------------- */
  const renderPG = ({ item }) => {
    const imageSource = item.img
      ? { uri: `${BASE_URL}${item.img}` }
      : require("../assets/Universal.jpeg");

    const handleCardPress = async () => {
    try {
      // Check if anyone is logged in (either user or owner)
      const token = await AsyncStorage.getItem("hlopgToken");
      const user = await AsyncStorage.getItem("hlopgUser");
      const role = await AsyncStorage.getItem("hlopgRole");
      
      if (token && (user || role)) {
        // Someone is logged in (either user or owner), navigate to HostelDetails
        navigation.navigate("HostelDetails", {
          hostelId: item.hostel_id,
        });
      } else {
        // No one is logged in, navigate to RoleSelection
          await AsyncStorage.setItem("pendingHostelId", item.hostel_id.toString()); 
        navigation.navigate("RoleSelection");
      }
    } catch (error) {
      console.log("AUTH CHECK ERROR:", error);
      // If error checking auth, default to RoleSelection for safety
      navigation.navigate("RoleSelection");
    }
  };

    return (
          <Pressable
      style={styles.card}
      onPress={handleCardPress}
    >

        {/* IMAGE */}
        <View style={styles.imageWrap}>
          <Image source={imageSource} style={styles.cardImage} />

          <Pressable
            style={styles.likeIcon}
            onPress={() => toggleLike(item.hostel_id)}
          >
            <Ionicons
              name={likedMap[item.hostel_id] ? "heart" : "heart-outline"}
              size={18}
              color={likedMap[item.hostel_id] ? "#ef4444" : "#fff"}
            />
          </Pressable>
        </View>

        {/* BODY */}
        <View style={styles.cardBody}>
          <Text style={styles.pgName} numberOfLines={1}>
            {item.hostel_name} ({item.area})
          </Text>
            
          <View style={styles.rowBetween}>
            <View
              style={[
                styles.tag,
                {
                  backgroundColor:
                    item.pg_type === "Women" ? "#ec4899" : "#4f46e5",
                },
              ]}
            >
              <Text style={styles.tagText}>{item.pg_type}</Text>
            </View>

            <View style={styles.ratingRow}>
              <Ionicons name="star" size={14} color="#facc15" />
              <Text style={styles.ratingText}>
                {item.rating || "4.5"}/5
              </Text>
            </View>
          </View>

          {/* ICONS */}
          <View style={styles.iconRow}>
            <View style={styles.iconBubble}>
              <Ionicons name="wifi-outline" size={14} color="#7c5cff" />
            </View>
            <View style={styles.iconBubble}>
              <Ionicons name="bed-outline" size={14} color="#7c5cff" />
            </View>
            <View style={styles.iconBubble}>
              <Ionicons name="restaurant-outline" size={14} color="#7c5cff" />
            </View>
            <View style={styles.iconBubble}>
              <Text style={styles.moreText}>+8</Text>
            </View>
          </View>

          <Text style={styles.price}>₹{item.price} / Month</Text>
        </View>
      </Pressable>
    );
  };

  /* ================= RENDER ================= */
  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={{ paddingBottom: 120 }}>
        {/* ---------------- HEADER ---------------- */}
        <View style={styles.header}>
          <View style={styles.headerTop}>
            <View style={styles.logoRow}>
              <Image
                source={require("../assets/logo.png")}
                style={[styles.logoImage, { tintColor: "#fff" }]}
              />
              <View>
                <Text style={styles.logoText}>Hlo PG</Text>
                <Text style={styles.cityText}>Hyderabad ⌄</Text>
              </View>
            </View>

            <View style={styles.bell}>
              <Ionicons
                name="notifications-outline"
                size={20}
                color="#111827"
              />
            </View>
          </View>

          {/* SEARCH */}
          <View style={styles.searchWrapper}>
            <Ionicons name="search-outline" size={18} color="#6b7280" />
            <TextInput
  placeholder="Search PG / Hostels"
  style={styles.searchInput}
  value={searchText}
  onChangeText={handleSearch}
/>

          </View>
 </View>
          {/* BANNER */}
          <ScrollView
            ref={bannerRef}
            horizontal
            pagingEnabled
            showsHorizontalScrollIndicator={false}
            style={{ marginTop: 20 }}
          >
            {banners.map((img, i) => (
              <Image key={i} source={img} style={styles.banner} />
            ))}
          </ScrollView>
       

        {/* ---------------- LIST ---------------- */}
        <View style={styles.listWrapper}>
          <Text style={styles.sectionTitle}>Nearby PGs</Text>

          {loading ? (
            <Text style={{ textAlign: "center" }}>Loading...</Text>
          ) : (
            <FlatList
              // data={pgList}
              data={filteredPgList}

              renderItem={renderPG}
              keyExtractor={(item) => item.hostel_id.toString()}
              numColumns={2}
              columnWrapperStyle={{ justifyContent: "space-between" }}
              scrollEnabled={false}
            />
          )}
        </View>
      </ScrollView>
    </View>
  );
};

export default Home;


const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "rgb(245, 245, 245)" },

  header: {
    backgroundColor: "#7c5cff",
    paddingTop: Platform.OS === "ios" ? 60 : 40,
    paddingBottom: 60,
    borderBottomLeftRadius: 30,
    borderBottomRightRadius: 30,

  },

  headerTop: {
    paddingHorizontal: 20,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginTop:30
  },

  logoRow: { flexDirection: "row", alignItems: "center" },
  logoImage: { width: 42, height: 42, marginRight: 10 },
  logoText: { fontSize: 22, fontWeight: "800", color: "#fff" },
  cityText: { color: "#e5e7eb", fontSize: 14 },

  bell: { backgroundColor: "#fff", padding: 10, borderRadius: 20 },

  searchWrapper: {
    position: "absolute",
    bottom: -30, // 👈 floats like Image-1
    left: 16,
    right: 16,
    backgroundColor: "#fff",
    borderRadius: 30,
    paddingHorizontal: 16,
    height: 54,
    flexDirection: "row",
    alignItems: "center",
    elevation: 8,
  },

  searchInput: { flex: 1,
    marginLeft: 10,
    fontSize: 15,
  },

  banner: {
    width: width - 32,
    height: 160,
   borderRadius: 18,
    marginHorizontal: 16,
    marginTop: 30,
    
  
  },

  listWrapper: { padding: 16 ,marginTop: 10,},
  sectionTitle: { fontSize: 18, fontWeight: "700", marginBottom: 12 },

  card: {
    width: (width - 38) / 2,
    backgroundColor: "#fff",
    borderRadius: 18,
    marginBottom: 10,
    padding: 10,
    elevation: 4,
    borderWidth:1.5,
    borderColor:"#201e1e21",
    
  },

  imageWrap: { borderRadius: 10, overflow: "hidden" },
  cardImage: { width: "100%", height: 120 },

  likeIcon: {
    position: "absolute",
    top: 8,
    right: 8,
    backgroundColor: "rgba(0,0,0,0.45)",
    padding: 2,
    borderRadius: 20,
  },

  cardBody: { paddingTop: 8 },

  pgName: { fontSize: 14, fontWeight: "700" },

  rowBetween: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 6,
  },

  tag: { paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 },
  tagText: { color: "#fff", fontSize: 11, fontWeight: "600" },

  ratingRow: { flexDirection: "row", alignItems: "center", gap: 4 },
  ratingText: { fontSize: 12, fontWeight: "600" },

  iconRow: { flexDirection: "row", gap: 8, marginTop: 8 },
  iconBubble: {
    backgroundColor: "#eef2ff",
    padding: 6,
    borderRadius: 20,
  },

  moreText: { fontSize: 11, fontWeight: "700", color: "#7c5cff" },

  price: {
    marginTop: 8,
    fontSize: 13,
    fontWeight: "700",
    color: "#111827",
  },
});
import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Image,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation } from "@react-navigation/native";
import api from "../api";
const BASE_URL = api.defaults.baseURL.replace("/api", "");

// fallback image
const pgDefaultImg = require("../assets/pg1.png");

const MyPGs = () => {
  const [pgs, setPgs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigation = useNavigation();

  useEffect(() => {
    const fetchOwnerPGs = async () => {
      try {
        const token = await AsyncStorage.getItem("hlopgToken");
        if (!token) throw new Error("Token missing");

        const res = await api.get("/hostel/owner/pgs", {
          headers: { Authorization: `Bearer ${token}` },
        });

        setPgs(res.data?.data || []);
      } catch (err) {
        console.log("Error fetching PGs:", err);
        setError("Failed to load PGs");
      } finally {
        setLoading(false);
      }
    };

    fetchOwnerPGs();
  }, []);

 

  // 🔹 CARD UI
//   const renderPG = ({ item }) => {
//     const imageUrl =
//       item.img && item.img !== ""
//         ? { uri: item.img }
//         : pgDefaultImg;

//     return (
//       <TouchableOpacity
//         style={styles.pgCard}
//         activeOpacity={0.8}
//         onPress={() =>
//           navigation.navigate("UploadPG", {
//             mode: "edit",
//             hostelId: item.hostel_id,
//           })
//         }
//       >
//         <Image source={
//   item.img
//     ? { uri: `${BASE_URL}${item.img}` }
//     : pgDefaultImg
// }
// style={styles.pgImage} />

//         <Text style={styles.pgName}>{item.hostel_name}</Text>

        
//       </TouchableOpacity>
//     );
//   };

const renderPG = ({ item }) => {
  const imageSource = item.img
    ? { uri: `${BASE_URL}${item.img}` }
    : pgDefaultImg;

  const facilities = item.facilities || {};

  return (
    <TouchableOpacity
      style={styles.card}
      activeOpacity={0.85}
      onPress={() =>
        navigation.navigate("EditPG", {
          // mode: "edit",
          hostelId: item.hostel_id,
        })
      }
    >
      {/* LEFT IMAGE */}
      <Image source={imageSource} style={styles.cardImage} />

      {/* RIGHT CONTENT */}
      <View style={styles.cardRight}>
        <Text style={styles.pgTitle}>{item.hostel_name}</Text>

        <View style={styles.rowBetween}>
          <View style={styles.typeBadge}>
            <Text style={styles.typeText}>{item.pg_type}</Text>
          </View>

          <View style={styles.ratingRow}>
            <Text style={styles.star}>★</Text>
            <Text style={styles.ratingText}>
              {item.rating || "4.5"}
            </Text>
          </View>
        </View>

        {/* FACILITIES */}
        <View style={styles.facilitiesRow}>
          {facilities["Free Wifi"] && <Text style={styles.icon}>📶</Text>}
          {facilities["bed"] && <Text style={styles.icon}>🛏️</Text>}
          {facilities["food"] && <Text style={styles.icon}>🍽️</Text>}
          {Object.keys(facilities).length > 3 && (
            <Text style={styles.more}>+{Object.keys(facilities).length - 3}</Text>
          )}
        </View>

        {/* PRICE */}
        <Text style={styles.price}>₹{item.price} / Month</Text>
      </View>
    </TouchableOpacity>
  );
};

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#5b5ff8" />
        <Text>Loading PGs...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.center}>
        <Text style={styles.errorText}>{error}</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>My PG's List</Text>
      <Text style={{marginBottom:20,marginTop:10}}>By Clicking on the PG you can edit them.</Text>

      {pgs.length === 0 ? (
        <Text style={styles.emptyText}>No PGs uploaded yet</Text>
      ) : (
        <FlatList
  data={pgs}
  keyExtractor={(item) => item.hostel_id.toString()}
  renderItem={renderPG}
  contentContainerStyle={{ paddingBottom: 30 }}
/>

      )}
    </View>
  );
};

export default MyPGs;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: "#f5f5f5",
    marginTop:10,
  },

  title: {
    fontSize: 20,
    fontWeight: "600",
    marginBottom: 15,
    textAlign:"center",

  },

  row: {
    justifyContent: "space-between",
  },

  pgCard: {
    backgroundColor: "#fff",
    borderRadius: 12,
    padding: 10,
    marginBottom: 15,
    width: "48%",
    elevation: 4,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 3 },
  },

  pgImage: {
    width: "100%",
    height: 130,
    borderRadius: 8,
    marginBottom: 8,
  },

  pgName: {
    fontSize: 15,
    fontWeight: "600",
    textAlign: "center",
    marginBottom: 6,
  },



  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },

  errorText: {
    color: "red",
    fontSize: 14,
  },

  emptyText: {
    textAlign: "center",
    color: "#6b7280",
    marginTop: 30,
  },
  card: {
  flexDirection: "row",
    backgroundColor: "#fff",
    borderRadius: 14,
    borderWidth:0.5,
    marginRight: 14,
    elevation: 3,
    overflow: "hidden",
    width: "100%", 
    padding:10,                   // ✅ FULL WIDTH

  // SHADOW
  shadowColor: "#6f5cf1",
  shadowOffset: { width: 0, height: 4 },
  shadowOpacity: 0.15,
  shadowRadius: 8,
  elevation: 4,
  marginBottom:10
  },

  cardImage: {
    width: 180,
  height: 150,
  borderTopLeftRadius: 16,
  borderBottomLeftRadius: 16,
  },

cardRight: {
  flex: 1,
  padding: 10,
  justifyContent: "space-between",
},

pgTitle: {
  fontSize: 16,
  fontWeight: "700",
  color: "#111827",
},

rowBetween: {
  flexDirection: "row",
  justifyContent: "space-between",
  alignItems: "center",
  marginTop: 6,
},

typeBadge: {
  backgroundColor: "#4f46e5",
  paddingHorizontal: 10,
  paddingVertical: 4,
  borderRadius: 12,
},

typeText: {
  color: "#fff",
  fontSize: 12,
  fontWeight: "600",
},

ratingRow: {
  flexDirection: "row",
  alignItems: "center",
},

star: {
  color: "#facc15",
  fontSize: 14,
  marginRight: 2,
},

ratingText: {
  fontSize: 13,
  fontWeight: "600",
},

facilitiesRow: {
  flexDirection: "row",
  alignItems: "center",
  marginTop: 6,
  gap: 6,
},

icon: {
  fontSize: 16,
},

more: {
  fontSize: 12,
  color: "#6b7280",
},

price: {
  fontSize: 15,
  fontWeight: "700",
  color: "#111827",
  marginTop: 6,
},

});
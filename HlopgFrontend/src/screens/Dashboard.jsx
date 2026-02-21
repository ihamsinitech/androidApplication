

import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  Image,
  FlatList,
  TextInput,
  ScrollView,
  Alert,
  Linking,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import api from "../api";
import { TouchableOpacity } from "react-native";
import { useNavigation } from "@react-navigation/native";


import { Dimensions } from "react-native";


import { Platform } from "react-native";
// import Geolocation from "@react-native-community/geolocation";


import pgDefaultImg from "../assets/pg1.png";
import userImg from "../assets/user.png";

const BASE_URL = api.defaults.baseURL.replace("/api", "");

const Dashboard = ({ user }) => {
  const [pgUpdate, setPgUpdate] = useState(
    "Update here any new rules / food changes / announcements..."
  );
  const [isEditing, setIsEditing] = useState(false);
  const [pgs, setPgs] = useState([]);
  const [totalAmount, setTotalAmount] = useState(0);

  const screenWidth = Dimensions.get("window").width;

  const ownerId = user?.id;

  // /* ================= LOCATION ================= */
  // useEffect(() => {
  //   if (Platform.OS === "ios") {
  //     // Ask permission once
  //     Geolocation.requestAuthorization();

  //     // Get live location
  //     Geolocation.getCurrentPosition(
  //       (position) => {
  //         console.log("LIVE LOCATION:", position.coords);

  //         const { latitude, longitude } = position.coords;

  //         // 👉 later you can send this to backend
  //         // api.post("/location", { latitude, longitude });
  //       },
  //       (error) => {
  //         console.log("LOCATION ERROR:", error);

  //         if (error.code === 1) {
  //           Alert.alert(
  //             "Location Required",
  //             "Enable location to see nearby PGs",
  //             [
  //               { text: "Cancel", style: "cancel" },
  //               {
  //                 text: "Open Settings",
  //                 onPress: () => Linking.openSettings(),
  //               },
  //             ]
  //           );
  //         }
  //       },
  //       {
  //         enableHighAccuracy: true,
  //         timeout: 15000,
  //         maximumAge: 10000,
  //       }
  //     );
  //   }
  // }, []);



  /* -------- BOOKINGS -------- */
  useEffect(() => {
    if (!ownerId) return;

    const fetchBookings = async () => {
      try {
        const res = await api.get(`/booking/owner/${ownerId}`);
        const bookings = res.data.bookings || [];

        const amount = bookings.reduce(
          (sum, b) => sum + (b.totalAmount || 0),
          0
        );
        setTotalAmount(amount);
      } catch (err) {
        console.log("BOOKING ERROR:", err);
      }
    };

    fetchBookings();
  }, [ownerId]);

  /* -------- PG LIST -------- */
  useEffect(() => {
    const fetchPGs = async () => {
      const token = await AsyncStorage.getItem("hlopgToken");
      if (!token) return;

      try {
        const res = await api.get("/hostel/owner/pgs", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPgs(res.data?.data || []);
      } catch (e) {
        console.log("PG ERROR:", e);
      }
    };

    fetchPGs();
  }, []);

  const complaints = [
  {
    name: "Chaitanya Thota",
    location: "Hyderabad",
    text: "🚿 Water issue reported on 2nd floor. Please resolve quickly.",
  },
  {
    name: "Rahul Verma",
    location: "Madhapur",
    text: "📶 WiFi is slow during night hours.",
  },
];

const reviews = [
  {
    name: "Chaitanya Thota",
    location: "Hyderabad",
    text: "Hlo PG made finding my perfect PG so easy. Verified listings gave peace of mind.",
    stars: "⭐⭐⭐⭐⭐",
  },
  {
    name: "Anjali Sharma",
    location: "Gachibowli",
    text: "Clean rooms, good food and peaceful environment.",
    stars: "⭐⭐⭐⭐☆",
  },
];


  /* -------- SAME CARD AS MyPGs (NO CLICK) -------- */
  const navigation = useNavigation();
  const renderPG = ({ item }) => {
    const imageSource = item.img
      ? { uri: `${BASE_URL}${item.img}` }
      : pgDefaultImg;

    const facilities = item.facilities || {};

    return (
       <TouchableOpacity
      activeOpacity={0.85}
      onPress={() =>
        navigation.navigate("MyPGs", {
          hostelId: item.hostel_id,
        })
      }
    >
      <View style={styles.card}>
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
              <Text style={styles.more}>
                +{Object.keys(facilities).length - 3}
              </Text>
            )}
          </View>

          {/* PRICE */}
          <Text style={styles.price}>₹{item.price} / Month</Text>
        </View>
      </View>
      </TouchableOpacity>
    );
  };

  return (
    
    <ScrollView style={styles.container}>

      {/* HEADER */}
<View style={styles.header}>
  <View style={styles.headerLeft}>
  {/* LOGO */}
  <Image
    source={require("../assets/logo.png")}
    style={[styles.logoImage, { tintColor: "#fff" }]}
  />

  {/* TEXT BLOCK */}
  <View style={styles.headerTextBlock}>
    <Text style={styles.logoText}>Hlo PG</Text>

    <View style={styles.locationRow}>
      <Text style={styles.locationText}>Hyderabad</Text>
      <Text style={styles.dropdown}>▼</Text>
    </View>
  </View>
</View>


  <View style={styles.headerRight}>
    <View style={styles.bellCircle}>
      <Text style={styles.bell}>🔔</Text>
    </View>
  </View>
</View>

      

      {/* MY PGs */}
      <Text style={styles.sectionTitle}>My PGs</Text>

      <FlatList
      
        data={pgs}
        keyExtractor={(item) => item.hostel_id.toString()}
        showsHorizontalScrollIndicator={false}
        renderItem={renderPG}
        scrollEnabled={false}          // ✅ REQUIRED
  contentContainerStyle={{ paddingBottom: 10 }}
        
      />

      {/* DAILY UPDATE */}
      <Text style={styles.sectionTitle}>PG Daily Updates</Text>
      <View style={styles.box}>
        {isEditing ? (
          <TextInput
            value={pgUpdate}
            onChangeText={setPgUpdate}
            multiline
            style={styles.textArea}
          />
        ) : (
          <Text>{pgUpdate}</Text>
        )}

        <Text
          style={styles.editBtn}
          onPress={() => setIsEditing(!isEditing)}
        >
          {isEditing ? "Save" : "Edit"}
        </Text>
      </View>

      {/* PAYMENTS */}
      <Text style={styles.sectionTitle}>Payments</Text>

<View style={styles.box}>
  <Text style={styles.amount}>₹ 1,24,000</Text>
  {/* <Text style={styles.muted}>Last 6 months</Text> */}

  <Image
    source={require("../assets/image.png")}
    style={styles.graphImage}
    resizeMode="contain"
  />
</View>




      {/* COMPLAINTS */}
<Text style={styles.sectionTitle}>Complaints</Text>

<ScrollView horizontal showsHorizontalScrollIndicator={false}>
  {complaints.map((item, index) => (
    <View key={index} style={styles.feedbackCard}>
      <View style={styles.feedbackHeader}>
        <Image source={userImg} style={styles.avatar} />
        <View>
          <Text style={styles.feedbackName}>{item.name}</Text>
          <Text style={styles.feedbackLocation}>{item.location}</Text>
        </View>
      </View>

      <Text style={styles.feedbackText}>{item.text}</Text>
    </View>
  ))}
</ScrollView>



      {/* REVIEWS */}
      <Text style={styles.sectionTitle}>Reviews</Text>

<ScrollView horizontal showsHorizontalScrollIndicator={false}>
  {reviews.map((item, index) => (
    <View key={index} style={styles.feedbackCard}>
      <View style={styles.feedbackHeader}>
        <Image source={userImg} style={styles.avatar} />
        <View>
          <Text style={styles.feedbackName}>{item.name}</Text>
          <Text style={styles.feedbackLocation}>{item.location}</Text>
        </View>
      </View>

      <Text style={styles.feedbackText}>{item.text}</Text>

      <Text style={styles.stars}>{item.stars}</Text>
    </View>
  ))}
</ScrollView>


        
      
    </ScrollView>
  );
};

export default Dashboard;

const styles = StyleSheet.create({
  container: {
    // padding: 16,
    backgroundColor: "#f8f9fb",
  
  },

  header: {
    backgroundColor: "#7c5cff",
    paddingTop: Platform.OS === "ios" ? 60 : 40,
    paddingBottom: 10,
    borderBottomLeftRadius: 30,
    borderBottomRightRadius: 30,
      flexDirection: "row",
  justifyContent: "space-between",
  alignItems: "center",
  paddingHorizontal: 16,
  marginBottom:20


  
},
headerLeft: {
  flexDirection: "row",
  alignItems: "center",
  // marginTop:10,
  marginBottom:40
},
headerTextBlock: {
  marginLeft: 12,

},


logoImage: {
  height: 48,
  width: 48,
  resizeMode: "contain",
  marginLeft:10
},

logoText: {
  fontSize: 22,
  fontWeight: "700",
  color: "#ffffff",
},


locationRow: {
  flexDirection: "row",
  alignItems: "center",
  marginTop: 4,
},

locationText: {
  color: "#e0ddff",
  fontSize: 14,
  marginRight: 6,
},

dropdown: {
  color: "#e0ddff",
  fontSize: 12,
},

headerRight: {
  justifyContent: "center",
  alignItems: "center",
  marginBottom:40
},

bellCircle: {
  width: 38,
  height: 38,
  borderRadius: 19,
  backgroundColor: "#ffffff",
  justifyContent: "center",
  alignItems: "center",
  // marginLeft:280,
  
  
},

bell: {
  fontSize: 18,
  
},


  welcome: { fontSize: 18, marginBottom: 20 },
  highlight: { color: "#5b5ff8", fontWeight: "600" },

  sectionTitle: {
    fontSize: 16,
    fontWeight: "600",
    marginVertical: 10,
    marginLeft:10
  },

  card: {
    flexDirection: "row",
    backgroundColor: "#fff",
    borderRadius: 14,
    borderWidth:0.5,
    marginRight: 14,
    elevation: 3,
    overflow: "hidden",
    width: "95%", 
    padding:10,  
    marginLeft:10 , 
                  // ✅ FULL WIDTH

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
    fontSize: 15,
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
    fontSize: 14,
    fontWeight: "700",
    marginTop: 6,
  },

  box: {
    backgroundColor: "#fff",
    padding: 14,
    borderRadius: 12,
    marginBottom: 16,
  },

  textArea: {
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 8,
    padding: 10,
    minHeight: 80,
  },

  editBtn: {
    marginTop: 10,
    color: "#5b5ff8",
    fontWeight: "600",
    alignSelf: "flex-end",
  },

  amount: {
    fontSize: 20,
    fontWeight: "700",
    color: "#5b5ff8",
  },

  muted: { color: "#6b7280" },

  reviewCard: {
    flexDirection: "row",
    backgroundColor: "#fff",
    padding: 12,
    borderRadius: 12,
  },

  userImg: {
    width: 45,
    height: 45,
    borderRadius: 22,
    marginRight: 10,
  },

  userName: { fontWeight: "600" },
  reviewText: { fontSize: 13, color: "#555" },
  complaintsWrapper: {
  marginBottom: 16,
},

complaintCard: {
  flexDirection: "row",
  alignItems: "center",
  backgroundColor: "#ffffff",
  padding: 14,
  borderRadius: 14,
  marginBottom: 12,
  elevation: 3,
  shadowColor: "#000",
  shadowOpacity: 0.08,
  shadowRadius: 6,
  shadowOffset: { width: 0, height: 3 },
  
  
},

complaintIcon: {
  fontSize: 26,
  marginRight: 12,
},

complaintTextBox: {
  flex: 1,
},

complaintTitle: {
  fontSize: 15,
  fontWeight: "700",
  color: "#111827",
},

complaintDesc: {
  fontSize: 13,
  color: "#6b7280",
  marginTop: 2,
},

graphImage: {
  width: "100%",
  height: 270,
  marginTop: 12,
  borderRadius: 10,
},

feedbackCard: {
  backgroundColor: "#ffffff",
  borderRadius: 16,
  padding: 16,
  width: 260,
  marginRight: 14,
  elevation: 4,
  shadowColor: "#000",
  shadowOpacity: 0.08,
  shadowRadius: 8,
  shadowOffset: { width: 0, height: 4 },
  marginTop:10,

},

feedbackHeader: {
  flexDirection: "row",
  alignItems: "center",
  marginBottom: 10,
},

avatar: {
  width: 42,
  height: 42,
  borderRadius: 21,
  marginRight: 10,
},

feedbackName: {
  fontSize: 15,
  fontWeight: "700",
  color: "#111827",
},

feedbackLocation: {
  fontSize: 13,
  color: "#6b7280",
},

feedbackText: {
  fontSize: 14,
  color: "#374151",
  marginTop: 6,
  lineHeight: 20,
},

stars: {
  marginTop: 8,
  fontSize: 16,
  color: "#facc15",
},

});
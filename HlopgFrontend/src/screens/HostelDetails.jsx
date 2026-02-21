import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  Dimensions,
  TouchableOpacity,
} from "react-native";
import Ionicons from "react-native-vector-icons/Ionicons";
import FontAwesome from "react-native-vector-icons/FontAwesome";
import api from "../api";
import { useRoute, useNavigation } from "@react-navigation/native";
import AsyncStorage from "@react-native-async-storage/async-storage";


const { width } = Dimensions.get("window");
const BASE_URL = api.defaults.baseURL.replace("/api", "");

const AMENITY_ICONS = {
  "Free Wifi": "wifi-outline",
  fan: "sync-outline",
  bed: "bed-outline",
  washing: "water-outline",
  lights: "bulb-outline",
  cupboard: "file-tray-outline",
  geyser: "flame-outline",
  water: "water-outline",
  fridge: "cube-outline",
  gym: "barbell-outline",
  tv: "tv-outline",
  ac: "snow-outline",
  parking: "car-outline",
  food: "restaurant-outline",
  lift: "arrow-up-outline",
  cams: "videocam-outline",
  "self cooking": "fast-food-outline",
};


const HostelDetails = () => {
  const route = useRoute();
  const navigation = useNavigation();
  const { hostelId } = route.params;

  const [pg, setPg] = useState(null);
  const [selectedSharing, setSelectedSharing] = useState(null);
  const [showMenu, setShowMenu] = useState(false);

  const [showBookingModal, setShowBookingModal] = useState(false);
const [sending, setSending] = useState(false);
const [user, setUser] = useState(null);


 const fetchUser = async () => {
  try {
    const storedUser = await AsyncStorage.getItem("hlopgUser");

    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  } catch (e) {
    console.log("USER FETCH ERROR", e);
  }
};

  useEffect(() => {
    fetchPG();
    fetchUser();
  }, []);

  const fetchPG = async () => {
    try {
      const res = await api.get(`/hostel/${hostelId}`);
      setPg(res.data.data);
    } catch (e) {
      console.log(e);
    }
  };

  if (!pg) return null;

  const image =
    pg.images?.length > 0
      ? `${BASE_URL}${pg.images[0]}`
      : `${BASE_URL}/uploads/default_pg.jpg`;

  const sharing = pg.sharing_data || {};
  const amenities = pg.facilities || {};
  const foodMenu = pg.food_menu || {};

 


const sendBookingRequest = async () => {
  try {
    setSending(true);

    const payload = {
      userId: user?.id,
      userName: user?.name,
      userEmail: user?.email,
      userMobile: user?.mobile || user?.phone || user?.mobileNumber || "",

      hostelId: pg.hostel_id,
      hostelName: pg.hostel_name,
      area: pg.area,
      city: pg.city,
    };

    // 🔹 API call (later connect to backend)
    await api.post("/booking/request", payload);


    setTimeout(() => {
      setSending(false);
      setShowBookingModal(false);
      alert("Request sent successfully. Owner will contact you.");

       navigation.navigate("Main");
    }, 1200);

  } catch (e) {
    setSending(false);
    alert("Failed to send request");
  }
};


  return (
    <View style={styles.screen}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.container}>
          {/* HEADER */}
          <View style={styles.headerRow}>
            <TouchableOpacity onPress={() => navigation.goBack()}>
              <Ionicons name="arrow-back" size={22} />
            </TouchableOpacity>

            <View style={styles.headerIcons}>
              <Ionicons name="heart-outline" size={22} />
              <Ionicons name="share-social-outline" size={22} />
            </View>
          </View>

          {/* LOCATION BOX */}
          <View style={styles.locationBox}>
            <Ionicons name="location-outline" size={16} />
            <Text style={styles.locationText}>
              {pg.hostel_name}, {pg.area}, {pg.city}
            </Text>
          </View>

          {/* IMAGE */}
          <Image source={{ uri: image }} style={styles.mainImage} />

          {/* TITLE */}
          <Text style={styles.title}>
            {pg.hostel_name}
            <Text style={{ color: "#22c55e" }}> ({pg.pg_type})</Text>
            <Text style={{ color: "#2563eb" }}> Available</Text>
          </Text>

          {/* DESCRIPTION */}
          <View style={styles.descBox}>
            <Text style={styles.desc}>{pg.description}</Text>
          </View>

          {/* TAG */}
          <View style={styles.tag}>
            <Text style={styles.tagText}>{pg.pg_type}</Text>
          </View>

          {/* SHARING */}
          <Text style={styles.sectionTitle}>Select Sharing Type</Text>
          <View style={styles.sharingRow}>
            {Object.keys(sharing).map((key) => (
              <TouchableOpacity
                key={key}
                style={[
                  styles.circle,
                  selectedSharing === key && styles.circleActive,
                ]}
                onPress={() => setSelectedSharing(key)}
              >
                <Text
                  style={[
                    styles.circleText,
                    selectedSharing === key && { color: "#fff" },
                  ]}
                >
                  {key.replace("-Sharing", "")}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          {selectedSharing && (
            <Text style={styles.price}>₹ {sharing[selectedSharing]}</Text>
          )}

          {/* AMENITIES */}
          <Text style={styles.sectionTitle}>Amenities</Text>
          <View style={styles.amenitiesGrid}>
            {Object.keys(amenities).map(
              (a) =>
                amenities[a] && (
                  <View key={a} style={styles.amenityItem}>
                    <Ionicons
            name={AMENITY_ICONS[a] || "checkmark-circle-outline"}
            size={24}
            color="#6366f1"
          />
                    <Text style={styles.amenityText}>{a}</Text>
                  </View>
                )
            )}
          </View>

          {/* FOOD MENU */}
          <TouchableOpacity
  style={styles.foodBtn}
  onPress={() => setShowMenu(!showMenu)}
>
  <Text style={styles.foodText}>
    Food Menu :- Day to day menu
  </Text>

  <Ionicons
    name={showMenu ? "chevron-up" : "chevron-down"}
    size={18}
    color="#6366f1"
  />
</TouchableOpacity>


          {showMenu && pg.food_menu && (
  <View style={styles.foodContainer}>
    {/* HEADER ROW */}
    <View style={styles.foodHeaderRow}>
      <View style={[styles.headerCell, styles.dayHeader]}>
        <Text style={styles.headerText}>DAY</Text>
      </View>
      <View style={[styles.headerCell, styles.breakfastHeader]}>
        <Text style={styles.headerText}>BREAKFAST</Text>
      </View>
      <View style={[styles.headerCell, styles.lunchHeader]}>
        <Text style={styles.headerText}>LUNCH</Text>
      </View>
      <View style={[styles.headerCell, styles.dinnerHeader]}>
        <Text style={styles.headerText}>DINNER</Text>
      </View>
    </View>

    {/* FOOD ROWS */}
    {Object.keys(pg.food_menu).map((day) => {
      const menu = pg.food_menu[day];
      if (!menu) return null;

      return (
        <View key={day} style={styles.foodRow}>
          <View style={styles.dayCell}>
            <Text style={styles.dayText}>
              {day.slice(0, 3).toUpperCase()}-D
            </Text>
          </View>

          <View style={styles.breakfastCell}>
            <Text style={styles.foodValue}>
              {menu.breakfast || "-"}
            </Text>
          </View>

          <View style={styles.lunchCell}>
            <Text style={styles.foodValue}>
              {menu.lunch || "-"}
            </Text>
          </View>

          <View style={styles.dinnerCell}>
            <Text style={styles.foodValue}>
              {menu.dinner || "-"}
            </Text>
          </View>
        </View>
      );
    })}
  </View>
)}




          {/* LOCATION MAP */}
          <Text style={styles.sectionTitle}>PG Location</Text>
          <View style={styles.mapBox}>
            <Ionicons name="location-outline" size={26} color="#6366f1" />
          </View>

          {/* RATINGS */}
          <Text style={styles.sectionTitle}>Ratings and Reviews</Text>
          <View style={styles.ratingBox}>
            <Text style={styles.ratingTitle}>Very Good</Text>

            <View style={styles.starRow}>
              {[1, 2, 3, 4, 5].map((i) => (
                <FontAwesome key={i} name="star" size={16} color="#facc15" />
              ))}
            </View>

            <Text style={{ fontSize: 12 }}>
              Total 19 ratings and 10 reviews
            </Text>
          </View>

          {/* DUMMY REVIEW */}
          <View style={styles.reviewCard}>
            <Image
              source={{
                uri: "https://randomuser.me/api/portraits/men/32.jpg",
              }}
              style={styles.avatar}
            />
            <Text style={styles.reviewText}>
              Absolutely loved my stay at this PG! Clean rooms, friendly staff,
              and peaceful environment.
            </Text>
          </View>
        </View>
      </ScrollView>

      {/* BOOK BUTTON */}
      <TouchableOpacity
  style={styles.bookBtn}
  onPress={() => setShowBookingModal(true)}
>
  <Text style={styles.bookText}>Book Now</Text>
</TouchableOpacity>



{showBookingModal && (
  <View style={styles.modalOverlay}>
    <View style={styles.modalBox}>
      <Text style={styles.modalTitle}>Send Booking Request</Text>

      {/* USER DETAILS (dummy / from storage later) */}
      <View style={styles.modalRow}>
  <Text style={styles.label}>Name</Text>
  <Text style={styles.value}>{user?.name || "-"}</Text>
</View>

<View style={styles.modalRow}>
  <Text style={styles.label}>Email</Text>
  <Text style={styles.value}>{user?.email || "-"}</Text>
</View>

<View style={styles.modalRow}>
  <Text style={styles.label}>Mobile</Text>
  <Text style={styles.value}>
  {user?.mobile || user?.phone || user?.mobileNumber || "-"}
</Text>

</View>


      {/* HOSTEL DETAILS */}
      <View style={styles.modalDivider} />

      <View style={styles.modalRow}>
        <Text style={styles.label}>Hostel</Text>
        <Text style={styles.value}>{pg.hostel_name}</Text>
      </View>

      <View style={styles.modalRow}>
        <Text style={styles.label}>Address</Text>
        <Text style={styles.value}>
          {pg.area}, {pg.city}
        </Text>
      </View>

      {/* ACTIONS */}
      <View style={styles.modalActions}>
        <TouchableOpacity
          style={styles.cancelBtn}
          onPress={() => setShowBookingModal(false)}
        >
          <Text>Cancel</Text>
        </TouchableOpacity>

        <TouchableOpacity
  style={styles.sendBtn}
  onPress={sendBookingRequest}
>
  <Text style={{ color: "#fff" }}>
    {sending ? "Sending..." : "Send Request"}
  </Text>
</TouchableOpacity>

      </View>
    </View>
  </View>
)}


    </View>
  );
};

export default HostelDetails;

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#fff",},

  container: {
    backgroundColor: "#fff",
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
    marginTop: 10,
    padding: 16,
  },

  headerRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },

  headerIcons: {
    flexDirection: "row",
    gap: 16,
  },

  locationBox: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#f3f4f6",
    padding: 10,
    borderRadius: 8,
    marginTop: 15,
    gap: 6,
    height:50
  },

  locationText: { fontSize: 15 },

  mainImage: {
    width: "100%",
    height: 200,
    borderRadius: 16,
    marginTop: 16,
  },

  title: {
    fontSize: 18,
    fontWeight: "700",
    marginTop: 16,
  },

  descBox: {
    backgroundColor: "#f3f4f6",
    padding: 12,
    borderRadius: 8,
    marginTop: 10,
  },

  desc: { fontSize: 12, color: "#6b7280" },

  tag: {
    backgroundColor: "#22c55e",
    alignSelf: "flex-start",
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 6,
    marginTop: 10,
  },

  tagText: { color: "#fff", fontSize: 12 },

  sectionTitle: {
    marginTop: 20,
    fontSize: 16,
    fontWeight: "700",
  },

  sharingRow: {
    flexDirection: "row",
    gap: 12,
    marginTop: 10,
  },

  circle: {
    width: 36,
    height: 36,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#d1d5db",
    alignItems: "center",
    justifyContent: "center",
  },

  circleActive: {
    backgroundColor: "#6366f1",
  },

  circleText: { fontSize: 12 },

  price: {
    marginTop: 8,
    fontSize: 16,
    fontWeight: "700",
    color: "#2563eb",
  },

  amenitiesGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    // justifyContent: "space-between",
    marginTop: 10,
  },

  amenityItem: {
    width: "22%",
    alignItems: "center",
    marginBottom: 16,
  },

  amenityText: {
    fontSize: 11,
    marginTop: 4,
    textAlign: "center",
  },

  foodBtn: {
    marginTop: 16,
    borderWidth: 1,
    borderColor: "#c7d2fe",
    padding: 12,
    borderRadius: 8,
    flexDirection: "row",
    justifyContent: "space-between",
  },

  foodText: { color: "#6366f1", fontWeight: "600" },

  foodRow: {
    backgroundColor: "#f9fafb",
    padding: 10,
    borderRadius: 8,
    marginTop: 8,
  },

  day: { fontWeight: "700", marginBottom: 4 },

  mapBox: {
    height: 120,
    backgroundColor: "#e5e7eb",
    borderRadius: 12,
    marginTop: 10,
    justifyContent: "center",
    alignItems: "center",
  },

  ratingBox: {
    marginTop: 12,
  },

  ratingTitle: { fontSize: 14, fontWeight: "600" },

  starRow: {
    flexDirection: "row",
    marginVertical: 6,
    gap: 4,
  },

  reviewCard: {
    backgroundColor: "#f3f4f6",
    padding: 16,
    borderRadius: 12,
    marginTop: 16,
    alignItems: "center",
  },

  avatar: {
    width: 60,
    height: 60,
    borderRadius: 30,
    marginBottom: 10,
  },

  reviewText: {
    fontSize: 12,
    textAlign: "center",
    color: "#374151",
  },

  bookBtn: {
    backgroundColor: "#6366f1",
    padding: 16,
    margin: 16,
    borderRadius: 10,
    alignItems: "center",
    marginLeft:30,
    marginRight:30
  },

  bookText: {
    color: "#fff",
    fontSize: 16,
    fontWeight: "700",
  },

  /* FOOD MENU TABLE (Details Page Version) */

foodContainer: {
  marginTop: 12,
  padding: 12,
  borderRadius: 12,
  borderWidth: 1,
  borderColor: "#e5e7eb",
  backgroundColor: "#ffffff",
},

foodHeaderRow: {
  flexDirection: "row",
  marginBottom: 10,
},

headerCell: {
  flex: 1,
  height: 40,
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
  fontSize: 11,
},

foodRow: {
  flexDirection: "row",
  marginBottom: 8,
},

dayCell: {
  flex: 0.8,
  backgroundColor: "#e5e5ea",
  borderRadius: 6,
  justifyContent: "center",
  alignItems: "center",
  height: 44,
  marginHorizontal: 3,
},

dayText: {
  fontSize: 11,
  fontWeight: "600",
  color: "#111827",
},

breakfastCell: {
  flex: 1,
  backgroundColor: "#e7fbf2",
  borderRadius: 6,
  height: 44,
  marginHorizontal: 3,
  justifyContent: "center",
  alignItems: "center",
},

lunchCell: {
  flex: 1,
  backgroundColor: "#fdecea",
  borderRadius: 6,
  height: 44,
  marginHorizontal: 3,
  justifyContent: "center",
  alignItems: "center",
},

dinnerCell: {
  flex: 1,
  backgroundColor: "#f6def4",
  borderRadius: 6,
  height: 44,
  marginHorizontal: 3,
  justifyContent: "center",
  alignItems: "center",
},

foodValue: {
  fontSize: 11,
  textAlign: "center",
  paddingHorizontal: 4,
},

modalOverlay: {
  position: "absolute",
  top: 0,
  bottom: 0,
  left: 0,
  right: 0,
  backgroundColor: "rgba(0,0,0,0.5)",
  justifyContent: "center",
  alignItems: "center",
},

modalBox: {
  width: "85%",
  backgroundColor: "#fff",
  borderRadius: 16,
  padding: 16,
},

modalTitle: {
  fontSize: 18,
  fontWeight: "700",
  marginBottom: 12,
},

modalRow: {
  marginBottom: 8,
},

label: {
  fontSize: 12,
  color: "#6b7280",
},

value: {
  fontSize: 14,
  fontWeight: "600",
},

modalDivider: {
  height: 1,
  backgroundColor: "#e5e7eb",
  marginVertical: 10,
},

modalActions: {
  flexDirection: "row",
  justifyContent: "flex-end",
  marginTop: 16,
  gap: 12,
},

cancelBtn: {
  paddingHorizontal: 14,
  paddingVertical: 8,
},

sendBtn: {
  backgroundColor: "#6366f1",
  paddingHorizontal: 16,
  paddingVertical: 8,
  borderRadius: 8,
},

});
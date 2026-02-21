import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TextInput,
  Pressable,
  Dimensions,
  ActivityIndicator,
} from "react-native";
import Ionicons from "react-native-vector-icons/Ionicons";
import FontAwesome from "react-native-vector-icons/FontAwesome";
import api from "../api";

const { width } = Dimensions.get("window");

const BASE_URL = api.defaults.baseURL.replace("/api", "");

const CityHostels = ({ navigation }) => {
  const [pgList, setPgList] = useState([]);
  const [filteredList, setFilteredList] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [loading, setLoading] = useState(true);
  const [likedMap, setLikedMap] = useState({});
const [likeLoading, setLikeLoading] = useState({});


  /* ---------------- FETCH ALL HOSTELS ---------------- */
  useEffect(() => {
    fetchAllPGs();
  }, []);

  const fetchAllPGs = async () => {
    try {
      const res = await api.get("/hostel/gethostels");
      const hostels = res.data?.hostels || [];
      setPgList(hostels);
      setFilteredList(hostels);
       fetchLikeStatus(hostels);
    } catch (e) {
      console.log("FETCH ERROR", e);
    } finally {
      setLoading(false);
    }
  };


  const fetchLikeStatus = async (hostels) => {
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
};


const toggleLike = async (hostelId) => {
  if (likeLoading[hostelId]) return;

  // optimistic UI
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

    setLikedMap((prev) => ({
      ...prev,
      [hostelId]: res.data.liked === true,
    }));
  } catch (e) {
    // rollback
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


  /* ---------------- SEARCH LOGIC ---------------- */
  const handleSearch = (text) => {
    setSearchText(text);

    if (!text.trim()) {
      setFilteredList(pgList);
      return;
    }

    const q = text.toLowerCase();

    const filtered = pgList.filter((pg) =>
      pg.hostel_name?.toLowerCase().includes(q) ||
      pg.area?.toLowerCase().includes(q) ||
      pg.city?.toLowerCase().includes(q)
    );

    setFilteredList(filtered);
  };

  /* ---------------- CARD ---------------- */
  const renderCard = (item) => {
    const imageSource = item.img
      ? { uri: `${BASE_URL}${item.img}` }
      : require("../assets/Universal.jpeg");

    return (
      <Pressable
        key={item.hostel_id}
        style={styles.card}
        onPress={() =>
          navigation.navigate("HostelDetails", {
            hostelId: item.hostel_id,
          })
        }
      >
        <Image source={imageSource} style={styles.cardImage} />

        {/* LIKE ICON (optional) */}
        <Pressable
  style={styles.likeIcon}
  onPress={() => toggleLike(item.hostel_id)}
>
  <Ionicons
    name={likedMap[item.hostel_id] ? "heart" : "heart-outline"}
    size={16}
    color={likedMap[item.hostel_id] ? "#ef4444" : "#fff"}
  />
</Pressable>


        <View style={styles.cardBody}>
          <Text style={styles.pgName}>
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
              <FontAwesome name="star" size={14} color="#facc15" />
              <Text style={styles.ratingText}>
                {item.rating || "4.5"}/5
              </Text>
            </View>
          </View>

          <View style={styles.iconRow}>
            <Ionicons name="wifi-outline" size={14} color="#7c5cff" />
            <Ionicons name="bed-outline" size={14} color="#7c5cff" />
            <Ionicons name="restaurant-outline" size={14} color="#7c5cff" />
            <View style={styles.moreBubble}>
              <Text style={styles.moreText}>+8</Text>
            </View>
          </View>

          <Text style={styles.price}>₹{item.price} / Month</Text>
        </View>
      </Pressable>
    );
  };

  /* ---------------- RENDER ---------------- */
  return (
    <View style={styles.screen}>
      {/* TITLE */}
      {/* <Text style={styles.pageTitle}>Search</Text> */}

      {/* WHITE CONTAINER */}
      <View style={styles.container}>
        {/* SEARCH BAR */}
        <View style={styles.searchRow}>
          <View style={styles.searchBox}>
            <Ionicons name="search-outline" size={18} color="#888" />
            <TextInput
              placeholder="Search PG / Hostels"
              value={searchText}
              onChangeText={handleSearch}
              style={styles.searchInput}
            />
          </View>

          <Pressable style={styles.filterBtn}>
            <Ionicons name="options-outline" size={20} color="#444" />
          </Pressable>
        </View>

        {/* LIST */}
        {loading ? (
          <ActivityIndicator size="large" style={{ marginTop: 40 }} />
        ) : (
          <ScrollView showsVerticalScrollIndicator={false}>
            <View style={styles.grid}>
              {filteredList.map(renderCard)}
            </View>

            {filteredList.length === 0 && (
              <Text style={styles.emptyText}>No PGs found</Text>
            )}
          </ScrollView>
        )}
      </View>
    </View>
  );
};

export default CityHostels;

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    backgroundColor: "#fff",
  },

 

  container: {
    flex: 1,
    backgroundColor: "#fff",
    marginTop: 15,
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
    padding: 16,
  },

  searchRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 16,
  },

  searchBox: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#f2f2f2",
    borderRadius: 25,
    paddingHorizontal: 14,
    height: 46,
  },

  searchInput: {
    flex: 1,
    marginLeft: 10,
    fontSize: 14,
  },

  filterBtn: {
    width: 46,
    height: 46,
    borderRadius: 23,
    backgroundColor: "#f2f2f2",
    justifyContent: "center",
    alignItems: "center",
    marginLeft: 10,
  },

  grid: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
  },

  card: {
    width: "48%",
    backgroundColor: "#fff",
    borderRadius: 10,
    marginBottom: 16,
    elevation: 4,
    overflow: "hidden",
    borderWidth:1.5,
    borderColor:"#0e0d0d24",
    padding:8
  },

  cardImage: {
    width: "100%",
    height: 130,
     borderRadius: 10,
  },

  likeIcon: {
    position: "absolute",
    top: 13,
    right: 13,
    backgroundColor: "rgba(0,0,0,0.4)",
    padding: 5,
    borderRadius: 20,
  },

  cardBody: {
    padding: 10,
  },

  pgName: {
    fontSize: 13,
    fontWeight: "700",
  },

  rowBetween: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 6,
    alignItems: "center",
  },

  tag: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 12,
  },

  tagText: {
    color: "#fff",
    fontSize: 10,
    fontWeight: "600",
  },

  ratingRow: {
    flexDirection: "row",
    alignItems: "center",
  },

  ratingText: {
    marginLeft: 4,
    fontSize: 12,
    fontWeight: "600",
  },

  iconRow: {
    flexDirection: "row",
    gap: 8,
    marginTop: 8,
  },

  moreBubble: {
    backgroundColor: "#eef2ff",
    paddingHorizontal: 6,
    borderRadius: 10,
  },

  moreText: {
    fontSize: 11,
    fontWeight: "700",
    color: "#7c5cff",
  },

  price: {
    marginTop: 8,
    fontSize: 13,
    fontWeight: "700",
  },

  emptyText: {
    textAlign: "center",
    marginTop: 40,
    color: "#6b7280",
  },
});
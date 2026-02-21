import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Image,
  Pressable,
  ActivityIndicator,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";
import FontAwesome from "react-native-vector-icons/FontAwesome";
import api from "../api";

const BASE_URL = api.defaults.baseURL.replace("/api", "");

const Favourites = () => {
  const navigation = useNavigation();
  const [likedPGs, setLikedPGs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLikedPGs();
  }, []);

  const fetchLikedPGs = async () => {
  try {
    const res = await api.get("/hostel/liked-hostels");
    const data = res.data?.data || []; // ✅ FIXED
    setLikedPGs(data);
  } catch (e) {
    console.log("FETCH LIKED ERROR", e);
  } finally {
    setLoading(false);
  }
};


  const renderCard = ({ item }) => {
    const imageSource = item.img
      ? { uri: `${BASE_URL}${item.img}` }
      : require("../assets/Universal.jpeg");

    return (
      <Pressable
        style={styles.card}
        onPress={() =>
          navigation.navigate("HostelDetails", {
            hostelId: item.hostel_id,
          })
        }
      >
        <Image source={imageSource} style={styles.cardImage} />

        {/* ❤️ liked */}
        <View style={styles.likeIcon}>
          <Ionicons name="heart" size={16} color="#ef4444" />
        </View>

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

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <Pressable onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} />
        </Pressable>
        <Text style={styles.headerTitle}>Favourite PG's List</Text>
        <View style={{ width: 24 }} />
      </View>

      {loading ? (
        <ActivityIndicator size="large" style={{ marginTop: 40 }} />
      ) : likedPGs.length === 0 ? (
        <Text style={styles.emptyText}>No liked PGs yet</Text>
      ) : (
        <FlatList
          data={likedPGs}
          keyExtractor={(item) => item.hostel_id.toString()}
          renderItem={renderCard}
          numColumns={2}
          columnWrapperStyle={{ justifyContent: "space-between" }}
          showsVerticalScrollIndicator={false}
        />
      )}
    </View>
  );
};

export default Favourites;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 16,
  },

  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop: 10,
    marginBottom: 20,
  },

  headerTitle: {
    fontSize: 18,
    fontWeight: "700",
  },

  card: {
    width: "48%",
    backgroundColor: "#fff",
    borderRadius: 10,
    marginBottom: 16,
    elevation: 4,
    borderWidth: 1.5,
    borderColor: "#0e0d0d24",
    padding: 8,
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
    marginTop: 60,
    color: "#6b7280",
  },
});
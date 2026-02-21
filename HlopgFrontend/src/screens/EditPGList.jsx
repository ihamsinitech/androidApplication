import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import Ionicons from "react-native-vector-icons/Ionicons";
import api from "../api";
import { useNavigation } from "@react-navigation/native";

const BASE_URL = api.defaults.baseURL.replace("/api", "");

export default function EditPGList() {
  const navigation = useNavigation();
  const [pgs, setPgs] = useState([]);

  useEffect(() => {
    loadOwnerPGs();
  }, []);

  const loadOwnerPGs = async () => {
    try {
      const token = await AsyncStorage.getItem("hlopgToken");
      const res = await api.get("/hostel/owner/pgs", {
        headers: { Authorization: `Bearer ${token}` },
      });
      // setPgs(res.data.data || []);
      const allPGs = res.data.data || [];

    // ✅ FILTER ONLY EDITED PGs
    const editedPGs = allPGs.filter(pg => pg.isEdited === true);

    setPgs(editedPGs);
    } catch (e) {
      console.log("Failed to load PG list", e);
    }
  };

  return (
    <ScrollView style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} />
        </TouchableOpacity>
        <Text style={styles.title}>Edit PGs List</Text>
      </View>

      {/* READ-ONLY PG LIST */}
      {pgs.map(pg => (
        <View key={pg.hostel_id} style={styles.card}>
          {/* <TouchableOpacity
  key={pg.hostel_id}
  style={styles.card}
  activeOpacity={0.8}
  onPress={() =>
    navigation.navigate("HostelDetails", {
      hostelId: pg.hostel_id,
    })
  }
> */}

          <Image
            source={{
              uri: pg.img
                ? `${BASE_URL}${pg.img}`
                : "https://via.placeholder.com/150",
            }}
            style={styles.image}
          />

          <View style={styles.cardBody}>
            <Text style={styles.pgName}>{pg.hostel_name}</Text>

            <Text style={styles.meta}>
              {pg.city}, {pg.area}
            </Text>

            <Text style={styles.meta}>
              Floors: {pg.numFloors} • Rooms/Floor: {pg.roomsPerFloor}
            </Text>

            <Text style={styles.statusText}>
              Status: {pg.status || "Active"}
            </Text>
          </View>
        </View>
      ))}

      {pgs.length === 0 && (
        <Text style={styles.emptyText}>
          No PGs available
        </Text>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 16,
    
  },

  header: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginBottom: 25,
    
  },

  title: {
    fontSize: 22,
    fontWeight: "700",
    marginLeft:90,
  },

  card: {
    flexDirection: "row",
    borderWidth: 1,
    borderColor: "#e5e7eb",
    borderRadius: 12,
    marginBottom: 14,
    overflow: "hidden",
  },

  image: {
    width: 90,
    height: 90,
    backgroundColor: "#f3f4f6",
  },

  cardBody: {
    flex: 1,
    padding: 10,
  },

  pgName: {
    fontSize: 16,
    fontWeight: "600",
  },

  meta: {
    fontSize: 12,
    color: "#6b7280",
    marginTop: 2,
  },

  statusText: {
    fontSize: 12,
    marginTop: 6,
    color: "#2563eb",
    fontWeight: "500",
  },

  emptyText: {
    textAlign: "center",
    color: "#9ca3af",
    marginTop: 40,
  },
});
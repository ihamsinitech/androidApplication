

import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Pressable,
} from "react-native";
import Ionicons from "react-native-vector-icons/Ionicons";
import AsyncStorage from "@react-native-async-storage/async-storage";
import api from "../api";
import { useNavigation } from "@react-navigation/native";

const ROOMS_STORAGE_KEY = "hlopg_rooms_state";





export default function MyRooms() {
  const navigation = useNavigation();

  const [hostels, setHostels] = useState([]);
  const [selectedHostel, setSelectedHostel] = useState(null);
  const [roomsByHostel, setRoomsByHostel] = useState({});
  const [floors, setFloors] = useState([]);



  useEffect(() => {
    navigation.setOptions({ headerShown: false });
    loadOwnerPGs();
    loadSavedRooms();
  }, []);



const loadSavedRooms = async () => {
  try {
    const saved = await AsyncStorage.getItem(ROOMS_STORAGE_KEY);
    if (saved) {
      setRoomsByHostel(JSON.parse(saved));
    }
  } catch (e) {
    console.log("Failed to load saved rooms", e);
  }
};

  /** 1️⃣ Load Owner Hostels */
  const loadOwnerPGs = async () => {
    try {
      const token = await AsyncStorage.getItem("hlopgToken");
      const res = await api.get("/hostel/owner/pgs", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setHostels(res.data.data || []);
    } catch (e) {
      console.log("❌ Owner PG fetch error", e);
    }
  };

  /** 2️⃣ Load Rooms for selected hostel */
  const loadRooms = async hostel => {
    try {
      setSelectedHostel(hostel);

      if (roomsByHostel[hostel.hostel_id]) {
      setFloors(roomsByHostel[hostel.hostel_id]);
      return;
    }

      const token = await AsyncStorage.getItem("hlopgToken");
      const res = await api.get(`/hostel/${hostel.hostel_id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const {
        numFloors,
        roomsPerFloor,
        startingRoom,
        sharing_data,
      } = res.data.data;

      const maxSharing = Math.max(
        ...Object.keys(sharing_data).map(s => parseInt(s))
      );

      const generated = Array.from(
        { length: numFloors },
        (_, f) => ({
          title: `${f + 1} Floor`,
          rooms: Array.from(
            { length: roomsPerFloor },
            (_, r) => ({
              roomNo: (f + 1) * 100 + (r + 1),
              beds: Array(maxSharing).fill(false),
            })
          ),
        })
      );

          const updatedMap = {
      ...roomsByHostel,
      [hostel.hostel_id]: generated,
    };

    setRoomsByHostel(updatedMap);
    setFloors(generated);

    // 🔒 persist
    await AsyncStorage.setItem(
      ROOMS_STORAGE_KEY,
      JSON.stringify(updatedMap)
    );
  } catch (e) {
    console.log("Room load error", e);
  }
};

 

  const toggleBed = async (f, r, b) => {
  const updated = floors.map((floor, fi) =>
    fi !== f
      ? floor
      : {
          ...floor,
          rooms: floor.rooms.map((room, ri) =>
            ri !== r
              ? room
              : {
                  ...room,
                  beds: room.beds.map((bed, bi) =>
                    bi === b ? !bed : bed
                  ),
                }
          ),
        }
  );

  setFloors(updated);

  const updatedMap = {
    ...roomsByHostel,
    [selectedHostel.hostel_id]: updated,
  };

  setRoomsByHostel(updatedMap);

  // ✅ async is now valid
  await AsyncStorage.setItem(
    ROOMS_STORAGE_KEY,
    JSON.stringify(updatedMap)
  );
};






  return (
    <ScrollView style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <Pressable onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} />
        </Pressable>
        <Text style={styles.title}>My Rooms</Text>
      </View>

      {/* HOSTEL SELECTOR */}
      <Text style={styles.sectionTitle}>Select Hostel</Text>

      <View style={styles.hostelRow}>
        {hostels.map(h => (
          <Pressable
            key={h.hostel_id}
            onPress={() => loadRooms(h)}
            style={[
              styles.hostelCard,
              selectedHostel?.hostel_id === h.hostel_id &&
                styles.activeHostel,
            ]}
          >
            <Text style={styles.hostelName}>{h.hostel_name}</Text>
            <Text style={styles.subText}>
              {h.numFloors} Floors • {h.roomsPerFloor} Rooms
            </Text>
          </Pressable>
        ))}
      </View>

      {/* ROOMS */}
      {floors.map((floor, fIndex) => (
        <View key={fIndex} style={styles.floorBox}>
          <Text style={styles.floorTitle}>{floor.title}</Text>

          <View style={styles.roomRow}>
            {floor.rooms.map((room, rIndex) => (
              <View key={rIndex} style={styles.roomCard}>
                <Text style={styles.roomNo}>{room.roomNo}</Text>

                <View style={styles.beds}>
                  {room.beds.map((filled, bIndex) => (
                    <Pressable
                      key={bIndex}
                      onPress={() =>
                        toggleBed(fIndex, rIndex, bIndex)
                      }
                      style={[
                        styles.bed,
                        filled
                          ? styles.filled
                          : styles.available,
                      ]}
                    />
                  ))}
                </View>
              </View>
            ))}
          </View>
        </View>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 16 ,},

  header: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    marginBottom: 20,
  },

  title: { fontSize: 22, fontWeight: "700" ,marginLeft:100},

  sectionTitle: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 10,
  },

  hostelRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
    marginBottom: 20,
  },

  hostelCard: {
    padding: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#e5e7eb",
    width: "48%",
  },

  activeHostel: {
    borderColor: "#2563eb",
    backgroundColor: "#eef4ff",
  },

  hostelName: { fontWeight: "600" },
  subText: { fontSize: 11, color: "#6b7280" },

  floorBox: { marginBottom: 24 },

  floorTitle: {
    backgroundColor: "#2563eb",
    color: "#fff",
    padding: 6,
    borderRadius: 6,
    marginBottom: 10,
    width: "fit-content",
  },

  roomRow: { flexDirection: "row", flexWrap: "wrap", gap: 12 },

  roomCard: {
    width: 90,
    borderWidth: 1,
    borderColor: "#e5e7eb",
    borderRadius: 10,
    padding: 8,
    alignItems: "center",
  },

  roomNo: { fontWeight: "600", marginBottom: 6 },

  beds: { flexDirection: "row", flexWrap: "wrap", gap: 4 },

  bed: { width: 16, height: 26, borderRadius: 4 },

  available: { backgroundColor: "#22c55e" },
  filled: { backgroundColor: "#ef4444" },
});
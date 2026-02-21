import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";
import api from "../api";
import AsyncStorage from "@react-native-async-storage/async-storage";

const OwnerNotifications = () => {
  const navigation = useNavigation();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedId, setExpandedId] = useState(null);

  useEffect(() => {
    fetchRequests();
  }, []);

//   const fetchRequests = async () => {
//   try {
//     const token = await AsyncStorage.getItem("hlopgToken"); // OWNER token

//     const res = await api.get("/booking/owner-requests", {
//       headers: {
//         Authorization: `Bearer ${token}`,
//       },
//     });

//     console.log("OWNER REQUESTS:", res.data.requests); // 🔍 DEBUG
//     setRequests(res.data.requests || []);
//   } catch (e) {
//     console.log("NOTIFICATION FETCH ERROR", e.response?.data || e.message);
//   } finally {
//     setLoading(false);
//   }
// };

const fetchRequests = async () => {
  try {
    const ownerId = 1; // 🔥 TEMP: replace with real owner id later

    const res = await api.get("/booking/owner-requests", {
      params: { ownerId }
    });

    console.log("OWNER REQUESTS:", res.data.requests);
    setRequests(res.data.requests || []);
  } catch (e) {
    console.log(
      "NOTIFICATION FETCH ERROR",
      e.response?.data || e.message
    );
  } finally {
    setLoading(false);
  }
};


  const toggleExpand = (id) => {
    setExpandedId(prev => (prev === id ? null : id));
  };

  const renderItem = ({ item }) => {
    const expanded = expandedId === item.id;

    return (
      <View style={styles.card}>
        {/* MAIN CARD */}
        <TouchableOpacity onPress={() => toggleExpand(item.id)}>
          <View style={styles.cardHeader}>
            <View>
              <Text style={styles.title}>New Booking Request</Text>
              <Text style={styles.msg}>
                {item.userName} requested for {item.hostelName}
              </Text>
            </View>

            <Ionicons
              name={expanded ? "chevron-up" : "chevron-down"}
              size={18}
              color="#6b7280"
            />
          </View>

          <Text style={styles.time}>{item.createdAt}</Text>
        </TouchableOpacity>

        {/* EXPANDED DETAILS */}
        {expanded && (
          <View style={styles.detailsBox}>
            <Text style={styles.sectionTitle}>User Details</Text>
            <Detail label="Name" value={item.userName} />
            <Detail label="Email" value={item.userEmail} />
            <Detail label="Mobile" value={item.userMobile} />

            <View style={styles.divider} />

            <Text style={styles.sectionTitle}>Hostel Details</Text>
            <Detail label="Hostel" value={item.hostelName} />
            <Detail label="Area" value={item.area} />
            <Detail label="City" value={item.city} />

            <View style={styles.messageBox}>
              <Text style={styles.message}>
                User is interested in this PG. Please contact them for further discussion.
              </Text>
            </View>
          </View>
        )}
      </View>
    );
  };

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Notifications</Text>
        <View style={{ width: 24 }} />
      </View>

      {loading ? (
        <ActivityIndicator style={{ marginTop: 40 }} />
      ) : (
        <FlatList
          data={requests}
          keyExtractor={(item) => item.id.toString()}
          renderItem={renderItem}
          ListEmptyComponent={
            <Text style={{ textAlign: "center", marginTop: 40 }}>
              No booking requests
            </Text>
          }
        />
      )}
    </View>
  );
};

const Detail = ({ label, value }) => (
  <View style={styles.row}>
    <Text style={styles.label}>{label}</Text>
    <Text style={styles.value}>{value || "-"}</Text>
  </View>
);

export default OwnerNotifications;


const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 16 },

  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 40,
    marginTop: 20,
  },

  headerTitle: { fontSize: 18, fontWeight: "600" },

  card: {
    borderWidth: 1,
    borderColor: "#6e91d7",
    borderRadius: 12,
    padding: 14,
    marginBottom: 12,
    backgroundColor: "#fff",
  },

  cardHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },

  title: {
    fontWeight: "700",
    fontSize: 14,
    marginBottom: 2,
  },

  msg: {
    color: "#374151",
    fontSize: 13,
  },

  time: {
    fontSize: 11,
    color: "#6b7280",
    marginTop: 6,
  },

  detailsBox: {
    marginTop: 14,
    backgroundColor: "#6da4da5a",
    borderRadius: 10,
    padding: 12,
  },

  sectionTitle: {
    fontSize: 14,
    fontWeight: "700",
    marginBottom: 8,
    marginTop: 6,
  },

  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 8,
  },

  label: {
    fontSize: 13,
    color: "#6b7280",
  },

  value: {
    fontSize: 13,
    fontWeight: "600",
    color: "#111827",
    maxWidth: "60%",
    textAlign: "right",
  },

  divider: {
    height: 1,
    backgroundColor: "#e5e7eb",
    marginVertical: 10,
  },

  messageBox: {
    marginTop: 8,
  },

  message: {
    fontSize: 13,
    color: "#374151",
    lineHeight: 18,
  },
});
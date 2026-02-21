import React from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";

const notifications = [
  { id: "1", title: "Booking Confirmed", msg: "Your booking in Madhapur is confirmed." },
  { id: "2", title: "New PG Listed", msg: "A new PG is available near KPHB." },
  { id: "3", title: "Price Drop", msg: "Price reduced for a PG you liked." },
];

const Notifications = () => {
  const navigation = useNavigation();

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

      {/* LIST */}
      <FlatList
        data={notifications}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.title}>{item.title}</Text>
            <Text style={styles.msg}>{item.msg}</Text>
          </View>
        )}
      />
    </View>
  );
};

export default Notifications;

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff", padding: 16 },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 50,
    marginTop:10,
  },
  headerTitle: { fontSize: 18, fontWeight: "600" },
  card: {
    padding: 14,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#e5e7eb",
    marginBottom: 12,
  },
  title: { fontWeight: "600", marginBottom: 4 },
  msg: { color: "#6b7280" },
});
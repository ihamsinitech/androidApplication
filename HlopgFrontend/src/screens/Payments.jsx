import React from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";

const Payments = () => {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Payments</Text>
        <View style={{ width: 24 }} />
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* INFO CARD */}
        <View style={styles.card}>
          <Ionicons name="information-circle" size={22} color="#4f46e5" />
          <Text style={styles.cardTitle}>Payment Process</Text>
          <Text style={styles.cardText}>
            Payments are handled directly with PG or Hostel owners.
            PG Finder does not collect money inside the app.
          </Text>
        </View>

        {/* DETAILS CARD */}
        <View style={styles.card}>
          <Ionicons name="wallet" size={22} color="#16a34a" />
          <Text style={styles.cardTitle}>Before You Pay</Text>

          <Text style={styles.list}>• Monthly Rent Amount</Text>
          <Text style={styles.list}>• Security Deposit</Text>
          <Text style={styles.list}>• Payment Mode (Cash / UPI / Bank)</Text>
          <Text style={styles.list}>• Refund & Exit Policy</Text>
        </View>

        {/* WARNING CARD */}
        <View style={[styles.card, styles.warningCard]}>
          <Ionicons name="alert-circle" size={22} color="#dc2626" />
          <Text style={styles.cardTitle}>Important Notice</Text>
          <Text style={styles.cardText}>
            Always verify owner details before making any payment.
            PG Finder is not responsible for financial disputes.
          </Text>
        </View>
      </ScrollView>
    </View>
  );
};

export default Payments;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f9fafb",
    padding: 16,
  },

  /* HEADER */
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop: 10,
    marginBottom: 20,
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "600",
  },

  /* CARDS */
  card: {
    backgroundColor: "#fff",
    borderRadius: 14,
    padding: 16,
    marginBottom: 16,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 8,
    elevation: 3,
  },

  warningCard: {
    borderLeftWidth: 4,
    borderLeftColor: "#dc2626",
  },

  cardTitle: {
    fontSize: 16,
    fontWeight: "600",
    marginTop: 6,
    marginBottom: 6,
  },

  cardText: {
    fontSize: 14,
    color: "#374151",
    lineHeight: 22,
  },

  list: {
    fontSize: 14,
    color: "#374151",
    marginTop: 6,
  },
});
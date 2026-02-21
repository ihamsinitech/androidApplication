import React from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";

const Help = () => {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} color="#111827" />
        </TouchableOpacity>

        <Text style={styles.headerTitle}>Help & Support</Text>
        <View style={{ width: 24 }} />
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* INTRO */}
        <Text style={styles.intro}>
          Welcome to PG Finder! We’re here to help you have a smooth and
          stress-free experience while finding your ideal PG or rental room.
        </Text>

        <View style={styles.divider} />

        {/* SEARCH */}
        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="search" size={18} color="#4f46e5" />
            <Text style={styles.sectionTitle}>
              How to Search for a PG or Rental Room
            </Text>
          </View>

          <Text style={styles.list}>• Go to the Home screen</Text>
          <Text style={styles.list}>• Enter your preferred location</Text>
          <Text style={styles.list}>• Apply filters (budget, amenities)</Text>
          <Text style={styles.list}>• Tap a property to view details</Text>
        </View>

        <View style={styles.divider} />

        {/* BOOKING */}
        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="calendar" size={18} color="#4f46e5" />
            <Text style={styles.sectionTitle}>How to Book a Room</Text>
          </View>

          <Text style={styles.list}>• Open the listing you like</Text>
          <Text style={styles.list}>• Check photos and amenities</Text>
          <Text style={styles.list}>• Tap “Book Now” or “Contact Owner”</Text>
          <Text style={styles.list}>• Wait for confirmation</Text>
        </View>

        <View style={styles.divider} />

        {/* PAYMENTS */}
        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="card" size={18} color="#4f46e5" />
            <Text style={styles.sectionTitle}>Payments</Text>
          </View>

          <Text style={styles.paragraph}>
            Payments are handled directly with the property owner. PG Finder
            does not collect payments inside the app. Always verify details
            before paying.
          </Text>
        </View>

        <View style={styles.divider} />

        {/* CONTACT */}
        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Ionicons name="help-circle" size={18} color="#4f46e5" />
            <Text style={styles.sectionTitle}>Need More Help?</Text>
          </View>

          <Text style={styles.list}>Email: support@pgfinder.com</Text>
          <Text style={styles.list}>Phone: +91-XXXXXXXXXX</Text>
          <Text style={styles.paragraph}>
            Our support team is available 24/7 to assist you.
          </Text>
        </View>

        <View style={{ height: 40 }} />
      </ScrollView>
    </View>
  );
};

export default Help;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 16,
  },

  /* HEADER */
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop:5,
    marginBottom: 20,
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "600",
    color: "#111827",
  },

  intro: {
    fontSize: 15,
    color: "#374151",
    lineHeight: 22,
    marginBottom: 16,
  },

  divider: {
    height: 1,
    backgroundColor: "#e5e7eb",
    marginVertical: 16,
  },

  section: {
    marginBottom: 6,
  },

  sectionHeader: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginBottom: 8,
  },

  sectionTitle: {
    fontSize: 16,
    fontWeight: "600",
    color: "#111827",
  },

  list: {
    fontSize: 14,
    color: "#374151",
    marginBottom: 6,
    lineHeight: 20,
  },

  paragraph: {
    fontSize: 14,
    color: "#374151",
    lineHeight: 22,
  },
});
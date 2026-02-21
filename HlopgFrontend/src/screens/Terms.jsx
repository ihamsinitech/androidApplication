import React from "react";
import {
  Text,
  View,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import Ionicons from "react-native-vector-icons/Ionicons";

const Terms = () => {
  const navigation = useNavigation();

  return (
    <View style={styles.wrapper}>
      {/* 🔹 HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} color="#111827" />
        </TouchableOpacity>

        <Text style={styles.headerTitle}>Terms and Conditions</Text>

        {/* spacer to keep title centered */}
        <View style={{ width: 24 }} />
      </View>

      {/* 🔹 CONTENT */}
      <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
        <Text style={styles.date}>Last Updated: 23/07/2025</Text>

        <Text style={styles.text}>
          Welcome to PG Finder! By using our mobile application or website, you
          agree to the following Terms and Conditions. Please read them carefully.
        </Text>

        <Text style={styles.heading}>1. Acceptance of Terms</Text>
        <Text style={styles.text}>
          By downloading, accessing, or using the PG Finder app (“App”), you
          agree to be bound by these Terms and Conditions and our Privacy Policy.
          If you do not agree, please do not use the app.
        </Text>

        <Text style={styles.heading}>2. Services Offered</Text>
        <Text style={styles.text}>
          PG Finder provides a platform for users to:
          {"\n"}• Search and book Paying Guest (PG) accommodations
          {"\n"}• Search and book Rental Rooms
          {"\n"}• View property details, amenities, availability, and pricing
          {"\n"}• Contact property owners or managers
        </Text>

        <Text style={styles.heading}>3. User Eligibility</Text>
        <Text style={styles.text}>
          You must be at least 18 years of age to use the app. By using PG Finder,
          you confirm that you meet this requirement.
        </Text>

        <Text style={styles.heading}>4. Booking and Payments</Text>
        <Text style={styles.text}>
          PG Finder may allow bookings through the platform or redirect you to
          third-party payment portals. All payments, deposits, and cancellation
          policies are governed by property owners. PG Finder is not responsible
          for disputes or losses.
        </Text>

        <Text style={styles.heading}>5. User Responsibilities</Text>
        <Text style={styles.text}>
          • Provide accurate information during registration and booking
          {"\n"}• Respect property rules
          {"\n"}• Do not use the app for illegal activities
        </Text>

        <Text style={styles.heading}>6. Content Accuracy</Text>
        <Text style={styles.text}>
          Listings are provided by property owners. PG Finder does not guarantee
          accuracy or availability and is not liable for discrepancies.
        </Text>

        <Text style={styles.heading}>7. Third-Party Services</Text>
        <Text style={styles.text}>
          Some services may be provided by third parties. PG Finder is not
          responsible for their conduct or performance.
        </Text>

        <Text style={styles.heading}>8. Account Termination</Text>
        <Text style={styles.text}>
          We reserve the right to suspend or terminate accounts for violations
          or suspicious activity.
        </Text>

        <Text style={styles.heading}>9. Limitation of Liability</Text>
        <Text style={styles.text}>
          PG Finder is not liable for losses related to bookings, payments,
          accommodation quality, or disputes.
        </Text>

        <Text style={styles.heading}>10. Modifications</Text>
        <Text style={styles.text}>
          Terms may be updated at any time. Continued use implies acceptance.
        </Text>

        <Text style={styles.heading}>11. Contact Us</Text>
        <Text style={styles.text}>
          For questions or support:
          {"\n"}📧 abc@gmail.com
        </Text>

        <View style={{ height: 40 }} />
      </ScrollView>
    </View>
  );
};

export default Terms;

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    backgroundColor: "#fff",
  },

  /* HEADER */
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 14,
    borderBottomWidth: 1,
    borderBottomColor: "#e5e7eb",
    marginBottom:20
    
  },

  headerTitle: {
    fontSize: 18,
    fontWeight: "600",
    color: "#111827",
  },

  /* CONTENT */
  container: {
    padding: 16,
  },

  date: {
    fontSize: 14,
    color: "#6b7280",
    marginBottom: 16,
  },

  heading: {
    fontSize: 16,
    fontWeight: "700",
    marginTop: 14,
    marginBottom: 6,
    color: "#111827",
  },

  text: {
    fontSize: 14,
    lineHeight: 22,
    color: "#374151",
  },
});
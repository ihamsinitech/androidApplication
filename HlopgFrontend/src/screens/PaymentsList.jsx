import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from "react-native";
import { Picker } from "@react-native-picker/picker";

const PaymentsList = () => {
  const [payments, setPayments] = useState([
    {
      id: 1,
      name: "Thota Chaitanya",
      date: "2025-10-10",
      type: "3 Sharing",
      amount: "9000",
      status: "Paid",
    },
    {
      id: 2,
      name: "Vijay Kumar",
      date: "2025-10-12",
      type: "2 Sharing",
      amount: "8500",
      status: "Pending",
    },
    {
      id: 3,
      name: "Rohit Reddy",
      date: "2025-10-15",
      type: "Single Room",
      amount: "12000",
      status: "Paid",
    },
  ]);

  const [newPayment, setNewPayment] = useState({
    name: "",
    date: "",
    type: "",
    amount: "",
    status: "Pending",
  });

  // Handle input change
  const handleChange = (field, value) => {
    setNewPayment((prev) => ({ ...prev, [field]: value }));
  };

  const handleAddPayment = () => {
    if (!newPayment.name || !newPayment.date || !newPayment.type || !newPayment.amount) {
      Alert.alert("Error", "Please fill all fields before adding a payment!");
      return;
    }

    const newEntry = { ...newPayment, id: payments.length + 1 };
    setPayments([...payments, newEntry]);
    setNewPayment({ name: "", date: "", type: "", amount: "", status: "Pending" });
  };

  const handleToggleStatus = (id) => {
    setPayments((prev) =>
      prev.map((p) =>
        p.id === id ? { ...p, status: p.status === "Paid" ? "Pending" : "Paid" } : p
      )
    );
  };

  const renderPayment = ({ item }) => (
    <View style={styles.paymentCard}>
      <View style={styles.paymentRow}>
        <Text style={styles.name}>{item.name}</Text>
        <Text style={[styles.statusBadge, item.status === "Paid" ? styles.paid : styles.pending]}>
          {item.status}
        </Text>
      </View>

      <View style={styles.paymentRow}>
        <Text style={styles.label}>Date:</Text>
        <Text>{item.date}</Text>
      </View>

      <View style={styles.paymentRow}>
        <Text style={styles.label}>Type:</Text>
        <Text>{item.type}</Text>
      </View>

      <View style={styles.paymentRow}>
        <Text style={styles.label}>Amount:</Text>
        <Text>₹ {item.amount}</Text>
      </View>

      <TouchableOpacity
        style={[styles.toggleBtn, item.status === "Paid" ? styles.btnPaid : styles.btnPending]}
        onPress={() => handleToggleStatus(item.id)}
      >
        <Text style={styles.btnText}>
          {item.status === "Paid" ? "Mark Pending" : "Mark Paid"}
        </Text>
      </TouchableOpacity>
    </View>
  );

  const totalAmount = payments.reduce((sum, p) => sum + parseInt(p.amount || 0), 0);

  return (
    <View style={styles.container}>
      <Text style={styles.header}>💳 Payments List</Text>

      {/* Add Payment Form */}
      <View style={styles.form}>
        <TextInput
          placeholder="Full Name"
          style={styles.input}
          value={newPayment.name}
          onChangeText={(val) => handleChange("name", val)}
        />
        <TextInput
          placeholder="Date (YYYY-MM-DD)"
          style={styles.input}
          value={newPayment.date}
          onChangeText={(val) => handleChange("date", val)}
        />
        <View style={styles.pickerContainer}>
          <Picker
            selectedValue={newPayment.type}
            onValueChange={(val) => handleChange("type", val)}
          >
            <Picker.Item label="Select Sharing Type" value="" />
            <Picker.Item label="Single Room" value="Single Room" />
            <Picker.Item label="2 Sharing" value="2 Sharing" />
            <Picker.Item label="3 Sharing" value="3 Sharing" />
            <Picker.Item label="4 Sharing" value="4 Sharing" />
          </Picker>
        </View>
        <TextInput
          placeholder="Amount (₹)"
          style={styles.input}
          keyboardType="numeric"
          value={newPayment.amount}
          onChangeText={(val) => handleChange("amount", val)}
        />
        <TouchableOpacity style={styles.addBtn} onPress={handleAddPayment}>
          <Text style={styles.addBtnText}>+ Add Payment</Text>
        </TouchableOpacity>
      </View>

      {/* Payments List */}
      <FlatList
        data={payments}
        keyExtractor={(item) => item.id.toString()}
        renderItem={renderPayment}
        contentContainerStyle={{ paddingBottom: 40 }}
      />

      {/* Total */}
      <View style={styles.totalBox}>
        <Text style={styles.totalText}>Total: ₹ {totalAmount.toLocaleString()}</Text>
      </View>
    </View>
  );
};

export default PaymentsList;



const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 16,
  },
  header: {
    fontSize: 24,
    fontWeight: "600",
    color: "#222",
    marginBottom: 20,
  },
  form: {
    marginBottom: 20,
    padding: 16,
    backgroundColor: "#f9fafc",
    borderRadius: 12,
  },
  input: {
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 10,
    padding: 10,
    marginBottom: 12,
    backgroundColor: "#fff",
  },
  pickerContainer: {
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 10,
    overflow: "hidden",
    marginBottom: 12,
    backgroundColor: "#fff",
  },
  addBtn: {
    backgroundColor: "#007bff",
    padding: 12,
    borderRadius: 10,
    alignItems: "center",
  },
  addBtnText: {
    color: "#fff",
    fontWeight: "600",
  },
  paymentCard: {
    padding: 12,
    backgroundColor: "#f5f5f5",
    borderRadius: 12,
    marginBottom: 12,
  },
  paymentRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 4,
  },
  name: {
    fontWeight: "600",
    fontSize: 16,
  },
  label: {
    fontWeight: "500",
    color: "#555",
  },
  statusBadge: {
    paddingVertical: 4,
    paddingHorizontal: 10,
    borderRadius: 20,
    fontWeight: "500",
    fontSize: 13,
    textTransform: "capitalize",
  },
  paid: {
    backgroundColor: "#e6f9ee",
    color: "#17803d",
  },
  pending: {
    backgroundColor: "#fff6e6",
    color: "#b56b00",
  },
  toggleBtn: {
    marginTop: 8,
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
  },
  btnPaid: {
    backgroundColor: "#28a745",
  },
  btnPending: {
    backgroundColor: "#ffc107",
  },
  btnText: {
    color: "#fff",
    fontWeight: "600",
  },
  totalBox: {
    backgroundColor: "#6c63ff",
    padding: 12,
    borderRadius: 10,
    alignItems: "center",
    marginTop: 10,
  },
  totalText: {
    color: "#fff",
    fontWeight: "600",
    fontSize: 16,
  },
});
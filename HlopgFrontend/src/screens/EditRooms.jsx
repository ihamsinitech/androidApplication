import React, { useEffect, useRef, useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  Modal,
  TextInput,
  Image,
  Alert,
} from "react-native";

const STORAGE_KEY = "hlopg_myrooms_v3";

export default function EditRooms() {
  const hasLoaded = useRef(false);

  const [floors, setFloors] = useState([]);
  const [setupPopup, setSetupPopup] = useState(false);

  const [setupData, setSetupData] = useState({
    floors: "",
    roomsPerFloor: "",
    sharing: "",
  });

  const [popupVisible, setPopupVisible] = useState(false);
  const [popupMode, setPopupMode] = useState("add");

  const [popupData, setPopupData] = useState({
    roomNo: "",
    sharing: "",
  });

  const [activeFloorIndex, setActiveFloorIndex] = useState(null);
  const [activeRoomIndex, setActiveRoomIndex] = useState(null);

  /* ---------- LOAD LOCAL STORAGE ---------- */
  useEffect(() => {
    if (hasLoaded.current) return;
    hasLoaded.current = true;

    try {
      const saved = global.localStorage?.getItem(STORAGE_KEY);
      if (saved) {
        setFloors(JSON.parse(saved));
      } else {
        setSetupPopup(true);
      }
    } catch {
      setSetupPopup(true);
    }
  }, []);

  const saveLayout = (data) => {
    global.localStorage?.setItem(STORAGE_KEY, JSON.stringify(data));
  };

  /* ---------- GENERATE LAYOUT ---------- */
  const generateLayout = () => {
    const { floors, roomsPerFloor, sharing } = setupData;
    if (!floors || !roomsPerFloor || !sharing) {
      Alert.alert("Error", "Please fill all fields");
      return;
    }

    const newFloors = Array.from({ length: floors }, (_, f) => ({
      floor: `${f + 1} Floor`,
      rooms: Array.from({ length: roomsPerFloor }, (_, r) => ({
        roomNo: `${f + 1}${String(r + 1).padStart(2, "0")}`,
        beds: Array(sharing).fill(false),
      })),
    }));

    setFloors(newFloors);
    saveLayout(newFloors);
    setSetupPopup(false);
  };

  /* ---------- BED TOGGLE ---------- */
  const toggleBed = (f, r, b) => {
    const updated = [...floors];
    updated[f].rooms[r].beds[b] = !updated[f].rooms[r].beds[b];
    setFloors(updated);
    saveLayout(updated);
  };

  /* ---------- ADD FLOOR ---------- */
  const addFloor = () => {
    const updated = [
      ...floors,
      { floor: `${floors.length + 1} Floor`, rooms: [] },
    ];
    setFloors(updated);
    saveLayout(updated);
  };

  /* ---------- ROOM POPUP ---------- */
  const openPopup = (mode, floorIndex, roomIndex = null) => {
    setPopupMode(mode);
    setActiveFloorIndex(floorIndex);
    setActiveRoomIndex(roomIndex);

    if (mode === "edit") {
      const room = floors[floorIndex].rooms[roomIndex];
      setPopupData({
        roomNo: room.roomNo,
        sharing: room.beds.length.toString(),
      });
    } else {
      setPopupData({ roomNo: "", sharing: "" });
    }

    setPopupVisible(true);
  };

  const saveRoom = () => {
    const { roomNo, sharing } = popupData;
    if (!roomNo || !sharing) {
      Alert.alert("Error", "Fill all fields");
      return;
    }

    const beds = Array(Math.min(6, Math.max(1, sharing))).fill(false);
    const updated = [...floors];

    if (popupMode === "add") {
      updated[activeFloorIndex].rooms.push({ roomNo, beds });
    } else {
      updated[activeFloorIndex].rooms[activeRoomIndex] = { roomNo, beds };
    }

    setFloors(updated);
    saveLayout(updated);
    setPopupVisible(false);
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Edit Rooms</Text>

        {floors.length > 0 && (
          <TouchableOpacity style={styles.addBtn} onPress={addFloor}>
            <Text style={styles.btnText}>+ Add Floor</Text>
          </TouchableOpacity>
        )}
      </View>

      {floors.map((floor, fi) => (
        <View key={fi} style={styles.floorBox}>
          <View style={styles.floorHeader}>
            <Text style={styles.floorText}>{floor.floor}</Text>
            <TouchableOpacity onPress={() => openPopup("add", fi)}>
              <Text style={styles.addRoom}>+ Add Room</Text>
            </TouchableOpacity>
          </View>

          <View style={styles.roomsWrap}>
            {floor.rooms.map((room, ri) => (
              <View key={ri} style={styles.roomCard}>
                <View style={styles.roomHeader}>
                  <Text>{room.roomNo}</Text>
                  <TouchableOpacity
                    onPress={() => openPopup("edit", fi, ri)}
                  >
                    <Text style={styles.edit}>Edit</Text>
                  </TouchableOpacity>
                </View>

                <Image
                  source={{
                    uri: "https://images.unsplash.com/photo-1600585154340-be6161a56a0c",
                  }}
                  style={styles.image}
                />

                <View style={styles.beds}>
                  {room.beds.map((filled, bi) => (
                    <TouchableOpacity
                      key={bi}
                      style={[
                        styles.bed,
                        { backgroundColor: filled ? "#00ff4c" : "#ff0000" },
                      ]}
                      onPress={() => toggleBed(fi, ri, bi)}
                    />
                  ))}
                </View>
              </View>
            ))}
          </View>
        </View>
      ))}

      {/* SETUP MODAL */}
      <Modal visible={setupPopup} transparent>
        <View style={styles.modal}>
          <TextInput
            placeholder="Floors"
            keyboardType="numeric"
            onChangeText={(v) => setSetupData({ ...setupData, floors: v })}
          />
          <TextInput
            placeholder="Rooms / Floor"
            keyboardType="numeric"
            onChangeText={(v) =>
              setSetupData({ ...setupData, roomsPerFloor: v })
            }
          />
          <TextInput
            placeholder="Sharing"
            keyboardType="numeric"
            onChangeText={(v) => setSetupData({ ...setupData, sharing: v })}
          />

          <TouchableOpacity onPress={generateLayout}>
            <Text style={styles.save}>Generate</Text>
          </TouchableOpacity>
        </View>
      </Modal>

      {/* ADD / EDIT ROOM MODAL */}
      <Modal visible={popupVisible} transparent>
        <View style={styles.modal}>
          <TextInput
            placeholder="Room No"
            value={popupData.roomNo}
            onChangeText={(v) => setPopupData({ ...popupData, roomNo: v })}
          />
          <TextInput
            placeholder="Sharing"
            keyboardType="numeric"
            value={popupData.sharing}
            onChangeText={(v) => setPopupData({ ...popupData, sharing: v })}
          />

          <TouchableOpacity onPress={saveRoom}>
            <Text style={styles.save}>
              {popupMode === "add" ? "Add" : "Update"}
            </Text>
          </TouchableOpacity>
        </View>
      </Modal>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { padding: 16 },
  header: { flexDirection: "row", justifyContent: "space-between" },
  title: { fontSize: 20, fontWeight: "600" },
  addBtn: { backgroundColor: "#007bff", padding: 8, borderRadius: 6 },
  btnText: { color: "#fff" },
  floorBox: { marginTop: 20 },
  floorHeader: { flexDirection: "row", justifyContent: "space-between" },
  roomsWrap: { flexDirection: "row", flexWrap: "wrap" },
  roomCard: { width: 150, margin: 8, backgroundColor: "#eee" },
  roomHeader: { flexDirection: "row", justifyContent: "space-between" },
  image: { width: "100%", height: 80 },
  beds: { flexDirection: "row", flexWrap: "wrap", padding: 6 },
  bed: { width: 24, height: 24, margin: 4 },
  modal: {
    margin: 40,
    padding: 20,
    backgroundColor: "#fff",
    borderRadius: 10,
  },
  save: { marginTop: 10, color: "#007bff", textAlign: "center" },
  edit: { color: "#007bff" },
});
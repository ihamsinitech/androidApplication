import React from "react";
import { View, Text, FlatList, Image, StyleSheet } from "react-native";
import FontAwesome from "react-native-vector-icons/FontAwesome";
import FontAwesome5 from "react-native-vector-icons/FontAwesome5";


const reviewsData = [
  {
    id: 1,
    name: "Chaitanya Thota",
    location: "Hyderabad",
    avatar: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
    review:
      "Hlo PG made finding my perfect PG Hostel so easy! The verified listings gave me peace of mind, and the whole process was smooth from start to finish.",
    rating: 4.5,
  },
  {
    id: 2,
    name: "Chaitanya Thota",
    location: "Hyderabad",
    avatar: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
    review:
      "Hlo PG made finding my perfect PG Hostel so easy! The verified listings gave me peace of mind, and the whole process was smooth from start to finish.",
    rating: 4.0,
  },
  {
    id: 3,
    name: "Chaitanya Thota",
    location: "Hyderabad",
    avatar: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
    review:
      "Hlo PG made finding my perfect PG Hostel so easy! The verified listings gave me peace of mind, and the whole process was smooth from start to finish.",
    rating: 4.0,
  },
  {
    id: 4,
    name: "Chaitanya Thota",
    location: "Hyderabad",
    avatar: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
    review:
      "Hlo PG made finding my perfect PG Hostel so easy! The verified listings gave me peace of mind, and the whole process was smooth from start to finish.",
    rating: 4.0,
  },
  {
    id: 5,
    name: "Chaitanya Thota",
    location: "Hyderabad",
    avatar: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
    review:
      "Hlo PG made finding my perfect PG Hostel so easy! The verified listings gave me peace of mind, and the whole process was smooth from start to finish.",
    rating: 4.0,
  },
  {
    id: 6,
    name: "Chaitanya Thota",
    location: "Hyderabad",
    avatar: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
    review:
      "Hlo PG made finding my perfect PG Hostel so easy! The verified listings gave me peace of mind, and the whole process was smooth from start to finish.",
    rating: 4.0,
  },
];

// ⭐ Render Star Ratings
const renderStars = (rating) => {
  const stars = [];
  const fullStars = Math.floor(rating);
  const hasHalfStar = rating % 1 !== 0;

  for (let i = 0; i < fullStars; i++) {
    stars.push(<FontAwesome key={`full-${i}`} name="star" size={16} color="#FFD700" />);
  }
  if (hasHalfStar) {
    stars.push(<FontAwesome key="half" name="star-half-full" size={16} color="#FFD700" />);
  }
  while (stars.length < 5) {
    stars.push(<FontAwesome key={`empty-${stars.length}`} name="star-o" size={16} color="#FFD700" />);
  }

  return <View style={styles.starsRow}>{stars}</View>;
};

const Reviews = () => {
  const renderItem = ({ item }) => (
    <View style={styles.reviewCard}>
      <View style={styles.reviewHeader}>
        <Image source={{ uri: item.avatar }} style={styles.avatar} />
        <View style={styles.reviewerInfo}>
          <Text style={styles.name}>{item.name}</Text>
          <Text style={styles.location}>{item.location}</Text>
        </View>
      </View>
      <Text style={styles.reviewText}>{item.review}</Text>
      {renderStars(item.rating)}
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Customer Reviews</Text>
      <FlatList
        data={reviewsData}
        keyExtractor={(item) => item.id.toString()}
        renderItem={renderItem}
        numColumns={2} // grid layout
        columnWrapperStyle={{ justifyContent: "space-between" }}
        contentContainerStyle={{ paddingBottom: 20 }}
      />
    </View>
  );
};

export default Reviews;

export const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: "600",
    color: "#222",
    marginBottom: 20,
    borderLeftWidth: 4,
    borderLeftColor: "#7b61ff",
    paddingLeft: 12,
  },
  reviewCard: {
    flex: 1,
    backgroundColor: "#fff",
    borderWidth: 1,
    borderColor: "#eee",
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    marginHorizontal: 4,
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    elevation: 2,
  },
  reviewHeader: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },
  avatar: {
    width: 48,
    height: 48,
    borderRadius: 24,
    marginRight: 12,
  },
  reviewerInfo: {
    flex: 1,
  },
  name: {
    fontSize: 16,
    fontWeight: "600",
    color: "#111",
  },
  location: {
    fontSize: 13,
    color: "#777",
  },
  reviewText: {
    fontSize: 14,
    color: "#333",
    marginBottom: 8,
    lineHeight: 20,
  },
  starsRow: {
    flexDirection: "row",
    gap: 4,
  },
});
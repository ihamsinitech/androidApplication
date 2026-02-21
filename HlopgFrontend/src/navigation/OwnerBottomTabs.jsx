import React from "react";
import { View, StyleSheet } from "react-native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import Ionicons from "react-native-vector-icons/Ionicons";

import Dashboard from "../screens/Dashboard";
import UploadPG from "../screens/UploadPG";
import MyPGs from "../screens/MyPGs";
import OwnerProfile from "../screens/OwnerProfile";

const Tab = createBottomTabNavigator();

const OwnerBottomTabs = ({ route }) => {
  const user = route?.params?.user;

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarShowLabel: false,

        // 🔥 PILL BACKGROUND
        tabBarStyle: {
          position: "absolute",
          bottom: 10,
          left: 20,
          right: 20,
          height: 64,
          borderRadius: 32,
          backgroundColor: "#000",
          borderTopWidth: 0,
          elevation: 10,
          marginLeft:10,
          marginRight:10,
        },

        tabBarIcon: ({ focused }) => {
          let iconName;

          switch (route.name) {
            case "Home":
              iconName = "home";
              break;
            case "UploadPG":
              iconName = "add";
              break;
            case "MyPGs":
              iconName = "business";
              break;
            case "Profile":
              iconName = "person";
              break;
          }

          return (
            <View
              style={[
                styles.iconWrapper,
                focused && styles.activeIcon,
              ]}
            >
              <Ionicons
  name={iconName}
  size={26}
  color={focused ? "#4f46e5" : "#9ca3af"}
/>

            </View>
          );
        },
      })}
    >
      <Tab.Screen name="Home">
        {() => <Dashboard user={user} />}
      </Tab.Screen>

      <Tab.Screen name="UploadPG" component={UploadPG} />

      <Tab.Screen name="MyPGs" component={MyPGs} />

      <Tab.Screen name="Profile">
        {() => <OwnerProfile user={user} />}
      </Tab.Screen>
    </Tab.Navigator>
  );
};

export default OwnerBottomTabs;

const styles = StyleSheet.create({
  iconWrapper: {
    width: 40,
    height: 40,
    borderRadius: 20,
    alignItems: "center",
    justifyContent: "center",
    marginTop:19,
    
  },

  activeIcon: {
    backgroundColor: "#fff", // ⚪ white circle
  },
});
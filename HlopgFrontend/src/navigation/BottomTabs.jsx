

import React from "react";
import { View, StyleSheet } from "react-native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import Ionicons from "react-native-vector-icons/Ionicons";

import Home from "../screens/Home";
import CityHostels from "../screens/CityHostels";
import ProfileEntry from "../screens/ProfileEntry";
import Favourites from "../screens/Favourites";

const Tab = createBottomTabNavigator();

export default function BottomTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarShowLabel: false,

        // 🔥 PILL TAB BAR
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
          marginRight:10
        },

        tabBarIcon: ({ focused }) => {
          let iconName = "home";
          // if (route.name === "SearchTab") iconName = "search";
          if (route.name === "SearchTab") {
    return (
      <View style={[
        styles.iconWrapper,
        focused && styles.activeIcon, // ✅ now works
      ]}>
        {/* Map icon */}
        <Ionicons
          name="map-outline"
          size={26}
          color={focused ? "#4f46e5" : "#9ca3af"}
          style={{ position: "absolute",top:10,right:9}}
        />

        {/* Small search icon overlay */}
        <Ionicons
          name="search"
          size={19}
          color={focused ? "#4f46e5" : "#9ca3af"} 
          style={{
            position: "absolute",
            right: 7,
            bottom: -2,
            top:20
          }}
        />
      </View>
    );
  }
          if (route.name === "FavouriteTab") iconName = "heart";
          if (route.name === "ProfileTab") iconName = "person";

          return (
            <View
              style={[
                styles.iconWrapper,
                focused && styles.activeIcon,
              ]}
            >
              <Ionicons
                name={focused ? iconName : `${iconName}-outline`}
                size={26}
                color={focused ? "#4f46e5" : "#9ca3af"}
              />
            </View>
          );
        },
      })}
    >
      <Tab.Screen name="HomeTab" component={Home} />
      <Tab.Screen name="SearchTab" component={CityHostels} />
      <Tab.Screen name="FavouriteTab" component={Favourites} />
      <Tab.Screen name="ProfileTab" component={ProfileEntry} />
    </Tab.Navigator>
  );
}

const styles = StyleSheet.create({
  iconWrapper: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: "center",
    justifyContent: "center",
    marginTop:24,
  },

  activeIcon: {
    backgroundColor: "#fff",
  },
});
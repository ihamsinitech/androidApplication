


import React, { useEffect, useState } from "react";
import { View, ActivityIndicator } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation } from "@react-navigation/native";

import RoleSelection from "../components/RoleSelection";
import UserPanel from "./UserPanel";

export default function ProfileEntry() {
  const navigation = useNavigation();
  const [screen, setScreen] = useState(null);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const token = await AsyncStorage.getItem("hlopgToken");
        const role = await AsyncStorage.getItem("hlopgRole");

         console.log("ProfileEntry - Token:", token, "Role:", role);

        if (!token) {
          setScreen("ROLE");
          return;
        }

        if (role === "OWNER") {
          // Owner should never see user profile - redirect to OwnerStack
          navigation.reset({
            index: 0,
            routes: [{ name: "OwnerStack" }],
          });
          return;
        }

        if (role === "USER") {
          setScreen("USER");
          return;
        }

        // Fallback
        setScreen("ROLE");
      } catch (error) {
        console.log("PROFILE ENTRY ERROR:", error);
        setScreen("ROLE");
      }
    };

    checkAuth();
  }, []);


//   useEffect(() => {
//   const checkAuth = async () => {
//     const token = await AsyncStorage.getItem("hlopgToken");
//     const role = await AsyncStorage.getItem("hlopgRole");

//   //   if (!token) {
//   //     setScreen("ROLE");
//   //   } else if (role === "USER") {
//   //     setScreen("USER");
//   //   } else {
//   //     // OWNER NEVER COMES HERE
//   //     setScreen("ROLE");
//   //   }
//   // };

// //   if (!token) {
// //       setScreen("ROLE");
// //     } else if (role === "USER") {
// //       setScreen("USER");
// //     } else if (role === "OWNER") {
// //       // 🔥 FORCE OWNER TO DASHBOARD
// //       navigation.reset({
// //         index: 0,
// //         routes: [{ name: "OwnerStack" }],
// //       });
// //     }
// //   };

// //   checkAuth();
// // }, []);

// if (!token) {
//       setScreen("ROLE");
//       return;
//     }

// //     if (role === "OWNER") {
// //       // 🔥 HARD EXIT from USER flow
// //       navigation.reset({
// //         index: 0,
// //         routes: [{ name: "OwnerStack" }],
// //       });
// //       return;
// //     }

// //     // USER
// //     setScreen("USER");
// //   };

// //   checkAuth();
// // }, []);
// // ONLY USER belongs here
//       if (role === "USER") {
//         setScreen("USER");
//         return;
//       }

//       // OWNER should never reach BottomTabs
//       setScreen("ROLE");
//     };

//     checkAuth();
//   }, []);




  if (!screen) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <ActivityIndicator />
      </View>
    );
  }

  if (screen === "USER") return <UserPanel />;
  return <RoleSelection />;
}
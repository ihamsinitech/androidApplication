


import React, { useState ,useEffect} from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";


import { NavigationContainer } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";

import BottomTabs from "./src/navigation/BottomTabs";
// import OwnerBottomTabs from "./src/navigation/OwnerBottomTabs";
import OwnerStackNavigator from "./src/navigation/OwnerStackNavigator";

import HostelDetails from "./src/screens/HostelDetails";

import IntroVideo from "./src/components/IntroVideo";
import LoadingVideo from "./src/components/LoadingVideo";


import StudentLogin from './src/screens/StudentLogin';
import StudentSignup from './src/screens/StudentSignup';
import ForgotPassword from "./src/screens/ForgotPassword";
import RoleSelection from './src/components/RoleSelection';
import OwnerSignup from "./src/screens/OwnerSignup";



// import UploadPG from './src/screens/UploadPG';
import UserPanel from './src/screens/UserPanel';
// import AdminPanel from "./src/screens/AdminPanel";

import EditProfile from "./src/screens/EditProfile";
import Notifications from "./src/screens/Notifications";
import Favourites from "./src/screens/Favourites";
import Payments from "./src/screens/Payments";
import Terms from "./src/screens/Terms";
import Help from "./src/screens/Help";

import OwnerEditProfile from "./src/screens/OwnerEditProfile";
import OwnerNotifications from "./src/screens/OwnerNotifications";
import MyRooms from "./src/screens/MyRooms";
import EditPGList from "./src/screens/EditPGList";

const Stack = createStackNavigator();

export default function App() {
  const [showIntro, setShowIntro] = useState(true);
  const [loading, setLoading] = useState(true);
  const [initialRoute, setInitialRoute] = useState("Main");
  const [isOwner, setIsOwner] = useState(false);

    const [isLoggedIn, setIsLoggedIn] = useState(false);

  
      useEffect(() => {
    const checkLogin = async () => {
      try {
        const token = await AsyncStorage.getItem("hlopgToken");
        const role = await AsyncStorage.getItem("hlopgRole");

        if (token) {
          setIsLoggedIn(true);
          setUserRole(role);
          
          if (role === "OWNER") {
            setInitialRoute("OwnerStack");
          } else {
            setInitialRoute("Main");
          }
        } else {
          setIsLoggedIn(false);
          setUserRole(null);
          setInitialRoute("Main");
        }
      } catch (error) {
        console.log("AUTH CHECK ERROR:", error);
        setIsLoggedIn(false);
        setUserRole(null);
        setInitialRoute("Main");
      } finally {
        setLoading(false);
      }
    };

    checkLogin();
  }, []);

  if (loading) {
    return <LoadingVideo />;
  }

  // Show intro video ONLY for non-logged-in users
  if (!isLoggedIn && showIntro) {
    return <IntroVideo onFinish={() => setShowIntro(false)} />;
  }
  // useEffect(() => {
  //   const checkLogin = async () => {
  //     try {
  //       const token = await AsyncStorage.getItem("hlopgToken");
  //       const role = await AsyncStorage.getItem("hlopgRole");

  //       if (token) {
  //         setIsLoggedIn(true);
  //         if (role === "OWNER") {
  //           setInitialRoute("OwnerStack");
  //           setIsOwner(true);
  //         } else {
  //           setInitialRoute("Main");
  //           setIsOwner(false);
  //         }
  //       } else {
  //         setIsLoggedIn(false);
  //         setInitialRoute("Main");
  //         setIsOwner(false);
  //       }
  //     } catch (error) {
  //       console.log("AUTH CHECK ERROR:", error);
  //       setInitialRoute("Main");
  //       setIsOwner(false);
  //     } finally {
  //       setLoading(false);
  //     }
  //   };

  //   checkLogin();
  // }, []);

  // if (loading) {
  //   return <LoadingVideo />;
  // }

  // if (!isOwner && !isLoggedIn && showIntro) {
  //   return <IntroVideo onFinish={() => setShowIntro(false)} />;
  // }


  return (
    // <NavigationContainer>
    <NavigationContainer key={initialRoute}>

      <Stack.Navigator
        initialRouteName={initialRoute}
        screenOptions={{ headerShown: false }}
      >
        <Stack.Screen name="Main" component={BottomTabs} />
        <Stack.Screen name="OwnerStack" component={OwnerStackNavigator} />


      

        {/* 🔹 Stack screens above tabs */}
        <Stack.Screen
          name="HostelDetails"
          component={HostelDetails}
        />

        <Stack.Screen
          name="StudentLogin"
          component={StudentLogin}
          options={{ title: 'Login' }}
        />
        <Stack.Screen
          name="StudentSignup"
          component={StudentSignup}
          options={{ title: 'Signup' }}
        />

         <Stack.Screen
  name="ForgotPassword"
  component={ForgotPassword}
  options={{ title: " ForgetPassword" }}
/>

  <Stack.Screen name="OwnerSignup" component={OwnerSignup} />      

<Stack.Screen
  name="RoleSelection"
  component={RoleSelection}
/>






  <Stack.Screen
    name="UserPanel"
    component={UserPanel}
    options={{ title: "UserPanel" }}
  />

  {/* <Stack.Screen
          name="AdminPanel"
          component={AdminPanel}
          options={{ headerShown: false }}
        /> */}

  <Stack.Screen name="EditProfile" component={EditProfile} />
<Stack.Screen name="Notifications" component={Notifications} />
<Stack.Screen name="Favourites" component={Favourites} />
<Stack.Screen name="Payments" component={Payments} />
<Stack.Screen name="Terms" component={Terms} />
<Stack.Screen name="Help" component={Help} />


<Stack.Screen name="OwnerEditProfile" component={OwnerEditProfile} />
<Stack.Screen name="OwnerNotifications" component={OwnerNotifications} />
<Stack.Screen name="MyRooms" component={MyRooms} />
<Stack.Screen name="EditPGList" component={EditPGList} />

      </Stack.Navigator>
    </NavigationContainer>
  );
}
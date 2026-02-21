import { createStackNavigator } from "@react-navigation/stack";

import OwnerBottomTabs from "../navigation/OwnerBottomTabs";
import UploadPG from "../screens/UploadPG";

const Stack = createStackNavigator();

export default function OwnerStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="OwnerTabs" component={OwnerBottomTabs} />
      <Stack.Screen name="EditPG" component={UploadPG} />
    </Stack.Navigator>
  );
}
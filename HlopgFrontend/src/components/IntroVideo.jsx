import React, { useEffect } from "react";
import { View, StyleSheet } from "react-native";
import Video from "react-native-video";

const IntroVideo = ({ onFinish }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onFinish && onFinish();
    }, 5000); // match video duration

    return () => clearTimeout(timer);
  }, []);

  return (
    <View style={styles.container}>
      <Video
        source={require("../assets/videos/loadingVideo.mp4")}
        style={styles.video}
        resizeMode="cover"
        muted
        repeat={false}
        paused={false}
      />
    </View>
  );
};

export default IntroVideo;

const styles = StyleSheet.create({
  container: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "#000",
    zIndex: 5000,
  },
  video: {
    width: "100%",
    height: "100%",
  },
});
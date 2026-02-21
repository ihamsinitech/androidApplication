package com.hlopg_backend.service;






public class GoogleAuthService {

    // @Value("${google.client.id}")
    // private String googleClientId;

    // public GoogleIdToken.Payload verify(String idTokenString) throws Exception {

    //     GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
    //             new NetHttpTransport(),
    //             JacksonFactory.getDefaultInstance()
    //     )
    //     .setAudience(Collections.singletonList(googleClientId))
    //     .build();

    //     GoogleIdToken idToken = verifier.verify(idTokenString);

    //     if (idToken == null) {
    //         throw new RuntimeException("Invalid Google token");
    //     }

    //     return idToken.getPayload();
    // }
}

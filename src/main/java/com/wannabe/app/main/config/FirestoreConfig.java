package com.wannabe.app.main.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class FirestoreConfig {

    /*@Value("${firebase.credential.path}")
    private String keyPath;*/

    private final ResourceLoader resourceLoader;

    public FirestoreConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    private void initialFirestore() {
        try {
//            FileInputStream serviceAccount = new FileInputStream("src/main/resources/wannabe-chat-test-firebase-adminsdk-4hx3v-4fbd7ef73b.json");
            Resource resource = resourceLoader.getResource("classpath:wannabe-dev-8a07e-firebase-adminsdk-zleup-83b2695ebf.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /*@Bean
    public Firestore getFireStore() throws IOException {
            var serviceAccount = new FileInputStream("src/resources/firebase/wannabe-chat-test-firebase-adminsdk-jy.json");
            var googleCredentials = GoogleCredentials.fromStream(serviceAccount);
            var build = FirestoreOptions.newBuilder().setCredentials(googleCredentials).build();
            return build.getService();
    }*/
}

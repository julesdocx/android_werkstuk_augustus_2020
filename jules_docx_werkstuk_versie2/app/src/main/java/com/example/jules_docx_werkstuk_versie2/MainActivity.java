package com.example.jules_docx_werkstuk_versie2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;


import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static androidx.navigation.ui.NavigationUI.setupWithNavController;
import static com.google.android.gms.common.util.CollectionUtils.setOf;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;
    private NavController navController;
    private View fragment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Date date;
    private Long highFives;
    public static MyAppDataBase dbRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        setUpNavigation();
        dbRoom = Room.databaseBuilder(getApplicationContext(),
                MyAppDataBase.class, "database-name").allowMainThreadQueries().build();



        User user = new User();
        user.setUid(1);
        user.setFirstName("Jules");
        user.setLastName("Docx");

        dbRoom.myDao().insertUser(user);
    }

    public void addHighFives(final View view) {
        try {
            DocumentReference docRef = db.collection("highFive").document("globalHFCounter");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            Log.d("", "DocumentSnapshot data: " + document.getData().get("highFiveCounter"));
                            highFives = (Long) document.getData().get("highFive");
                            highFives = highFives + 1;
                            date = new Date();
                            HighFive highFive = new HighFive(highFives, date);
                            setHighFive(highFive);
                            performSnackbar(view);
                        } else {
                            Log.d("", "No such document");
                        }
                    } else {
                        Log.d("", "get failed with ", task.getException());
                    }
                }
            });
        }catch (Error e) {
            Log.d("", " tja..." + e);
        }
    }

    private void performSnackbar(View view) {
        Snackbar mySnackbar = Snackbar.make(view, getResources().getString(R.string.snackbar_highfives) + highFives, 3000);
        mySnackbar.show();
    }

    private void setHighFive(HighFive highFive) {
        db.collection("highFive").document("globalHFCounter")
                .set(highFive)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error writing document", e);
                    }
                });
    }

    public void setUpNavigation() {
        this.bottomNavView = this.findViewById(R.id.bottomNavigationView);
        this.navController = Navigation.findNavController(this, R.id.fragment);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        assert navHostFragment != null;
        NavigationUI.setupWithNavController(this.bottomNavView,
                navHostFragment.getNavController());

    }
}

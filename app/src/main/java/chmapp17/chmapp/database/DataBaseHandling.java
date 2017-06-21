package chmapp17.chmapp.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chmapp17.chmapp.MainActivity;

public class DataBaseHandling {

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("crimes");

    public void readData() {
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Retrieve lists of items or listen for additions to a list of items.
                // This callback is triggered once for each existing child and then again
                // every time a new child is added to the specified path.
                MainActivity.crimeList.add(dataSnapshot.getValue(CrimeInfo.class));
                MainActivity.mapCrimesKeys.put(MainActivity.crimeList.size() - 1, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DataBaseError: " + databaseError.getCode());
            }
        });
    }

    public void addCrime(CrimeInfo crime) {
        dbRef.push().setValue(crime);
    }

    public void updateCrime(CrimeInfo crime) {
        dbRef.child(MainActivity.mapCrimesKeys.get(MainActivity.crimeList.indexOf(crime))).setValue(crime);
    }
}

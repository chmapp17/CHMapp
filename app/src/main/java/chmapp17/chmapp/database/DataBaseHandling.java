package chmapp17.chmapp.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chmapp17.chmapp.MainActivity;

public class DataBaseHandling {

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("crimes");

    public void readData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot crimeSnapshot : dataSnapshot.getChildren()) {
                    MainActivity.crimes.add(crimeSnapshot.getValue(CrimeInfo.class));
                }
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
}

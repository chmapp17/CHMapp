package chmapp17.chmapp.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DataBaseHandling {

    private DatabaseReference dbRef;
    private ArrayList<CrimeInfo> crimes;

    public DataBaseHandling() {
        dbRef = FirebaseDatabase.getInstance().getReference("crimes");
        crimes = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot crimeSnapshot : dataSnapshot.getChildren()) {
                    crimes.add(crimeSnapshot.getValue(CrimeInfo.class));
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

    public ArrayList<CrimeInfo> getCrimes() {
        return crimes;
    }
}

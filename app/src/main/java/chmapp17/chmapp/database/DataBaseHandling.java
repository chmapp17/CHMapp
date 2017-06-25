package chmapp17.chmapp.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chmapp17.chmapp.MainActivity;

public class DataBaseHandling {

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("crimes");
    private DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference dbReviews = FirebaseDatabase.getInstance().getReference("reviews");

    public void readData() {
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Retrieve lists of items or listen for additions to a list of items.
                // This callback is triggered once for each existing child and then again
                // every time a new child is added to the specified path.
                MainActivity.crimeList.add(dataSnapshot.getValue(CrimeInfo.class));
                MainActivity.mapCrimesKeys.put(MainActivity.crimeList.size() - 1, dataSnapshot.getKey());
                crimeRatingCalc();
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
        dbUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Retrieve lists of items or listen for additions to a list of items.
                // This callback is triggered once for each existing child and then again
                // every time a new child is added to the specified path.
                MainActivity.userList.add(dataSnapshot.getValue(UsersInfo.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DataBaseError: " + databaseError.getCode());
            }
        });
        dbReviews.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Retrieve lists of items or listen for additions to a list of items.
                // This callback is triggered once for each existing child and then again
                // every time a new child is added to the specified path.
                MainActivity.reviewList.add(dataSnapshot.getValue(CrimeReview.class));
                crimeRatingCalc();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DataBaseError: " + databaseError.getCode());
            }
        });

    }

    public void addCrime(CrimeInfo crime) {
        DatabaseReference dbRef_local = dbRef.push();
        crime.cId = dbRef_local.getKey();
        dbRef_local.setValue(crime);
    }

    public void addUser(UsersInfo user_info) {
        dbUsers.push().setValue(user_info);
    }
    public void addReview(CrimeReview cReview) {
        DatabaseReference dbRef_local = dbReviews.push();
        cReview.rId = dbRef_local.getKey();
        dbRef_local.setValue(cReview);
    }
    public void updateReview(CrimeReview cReview) {
        dbReviews.child(cReview.rId).setValue(cReview);
    }
    public void crimeRatingCalc()
    {
        for (CrimeInfo crime : MainActivity.crimeList) {
            if(crime.toCalcRating == true )
            {
                String cId = crime.cId;
                float cRating = 0;
                int nr = 0;
                for( CrimeReview cReview : MainActivity.reviewList)
                {
                    if(cId.equals(cReview.cId)){
                        nr++;
                        cRating += cReview.cStars;
                    }
                }
                crime.cRating = cRating/nr;
                crime.toCalcRating = false;
                updateCrime(crime);

            }
        }
    }

    public void updateCrime(CrimeInfo crime) {
        dbRef.child(MainActivity.mapCrimesKeys.get(MainActivity.crimeList.indexOf(crime))).setValue(crime);
    }
}

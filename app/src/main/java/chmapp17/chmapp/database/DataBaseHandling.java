package chmapp17.chmapp.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chmapp17.chmapp.MainActivity;

public class DataBaseHandling {

    private DatabaseReference dbCrimes = FirebaseDatabase.getInstance().getReference("crimes");
    private DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference dbReviews = FirebaseDatabase.getInstance().getReference("reviews");

    public void readData() {
        dbCrimes.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Retrieve lists of items or listen for additions to a list of items.
                // This callback is triggered once for each existing child and then again
                // every time a new child is added to the specified path.
                MainActivity.crimeList.add(dataSnapshot.getValue(CrimeInfo.class));
                MainActivity.mapCrimesKeys.put(MainActivity.crimeList.size() - 1, dataSnapshot.getKey());
                MainActivity.mapKeysCrimes.put(dataSnapshot.getKey(), MainActivity.crimeList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                MainActivity.crimeList.set(MainActivity.crimeList.indexOf(getCrimeByKey(dataSnapshot.getKey())),
                        dataSnapshot.getValue(CrimeInfo.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MainActivity.crimeList.remove(dataSnapshot.getValue(CrimeInfo.class));
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
        dbCrimes.push().setValue(crime);
    }

    public void updateCrime(CrimeInfo crime) {
        dbCrimes.child(getCrimeKey(crime)).setValue(crime);
    }

    public String getCrimeKey(CrimeInfo crime) {
        return MainActivity.mapCrimesKeys.get(MainActivity.crimeList.indexOf(crime));
    }

    public CrimeInfo getCrimeByKey(String key) {
        return MainActivity.crimeList.get(MainActivity.mapKeysCrimes.get(key));
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

    public void crimeRatingCalc() {
        for (CrimeInfo crime : MainActivity.crimeList) {
            if (crime.toCalcRating) {
                String cId = getCrimeKey(crime);
                float cRating = 0;
                int nr = 0;
                for (CrimeReview cReview : MainActivity.reviewList) {
                    if (cId.equals(cReview.cId)) {
                        nr++;
                        cRating += cReview.cStars;
                    }
                }
                crime.cRating = cRating / nr;
                crime.toCalcRating = false;
                updateCrime(crime);
            }
        }
    }
}

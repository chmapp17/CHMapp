package chmapp17.chmapp.database;

public class CrimeReview {
    public String uId, email;
    public boolean hasUpVoted = false;

    public CrimeReview() {
    }

    public CrimeReview(String uId, String email,boolean hasUpVoted) {
        this.uId = uId;
        this.email = email;
        this.hasUpVoted = hasUpVoted;
    }
}

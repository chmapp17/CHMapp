package chmapp17.chmapp.database;

public class CrimeReview {
    public String uId;
    public boolean hasUpVoted = false;

    public CrimeReview() {
    }

    public CrimeReview(String uId, boolean hasUpVoted) {
        this.uId = uId;
        this.hasUpVoted = hasUpVoted;
    }
}

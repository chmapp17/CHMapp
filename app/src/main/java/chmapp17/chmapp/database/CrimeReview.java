package chmapp17.chmapp.database;

public class CrimeReview {
    public String uId;
    public boolean HasUpVoted = false;

    public CrimeReview() {
    }

    public CrimeReview(String uId, boolean HasUpVoted) {
        this.uId = uId;
        this.HasUpVoted = HasUpVoted;
    }
}

package chmapp17.chmapp.database;

public class CrimeReview {
    public String cId, uId, rId;
    public int cStars;

    public CrimeReview() {
    }

    public CrimeReview(String cId, String uId, int cStars) {
        this.cId = cId;
        this.uId = uId;
        this.cStars = cStars;

    }
}

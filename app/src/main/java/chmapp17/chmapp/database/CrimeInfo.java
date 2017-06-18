package chmapp17.chmapp.database;

public class CrimeInfo {

    public String cType, cDate, cDescr, lDescr, cLocation;

    public CrimeInfo() {
    }

    public CrimeInfo(String cType, String cDate, String cDescr, String lDescr, String cLocation) {
        this.cType = cType;
        this.cDate = cDate;
        this.cDescr = cDescr;
        this.lDescr = lDescr;
        this.cLocation = cLocation;
    }

    @Override
    public String toString() {
        return cType + " " + cDate + " " + cDescr + " " + lDescr + " " + cLocation;
    }
}

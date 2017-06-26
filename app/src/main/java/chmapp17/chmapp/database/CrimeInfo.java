package chmapp17.chmapp.database;

import android.content.Context;

import chmapp17.chmapp.R;

public class CrimeInfo {

    public String cType, cDate, cDescr, lDescr, cLocation, uId;
    public float cRating = 0;
    public boolean toCalcRating = false;

    public CrimeInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(CrimeInfo.class)
    }

    public CrimeInfo(String cType, String cDate, String cDescr, String lDescr, String cLocation, String uId) {
        this.cType = cType;
        this.cDate = cDate;
        this.cDescr = cDescr;
        this.lDescr = lDescr;
        this.cLocation = cLocation;
        this.uId = uId;
    }

    public int getCrimeDrawableID(Context context, String cType, String dType) {
        String[] crimes_array = context.getResources().getStringArray(R.array.crimes_array);
        int i = 0, drawable_id = 0;
        for (String cStr : crimes_array) {
            i += 1;
            if (cStr.equals(cType))
                drawable_id = context.getResources()
                        .getIdentifier("crime" + i + "_" + dType, "drawable", context.getPackageName());
        }
        return drawable_id;
    }
}

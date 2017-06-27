package chmapp17.chmapp.database;

import java.util.ArrayList;
import java.util.List;

interface DataBooleanChangedListener {
    public void OnMyBooleanChanged();
}

public class DataListener {
    private static boolean myBoolean;
    private static List<DataBooleanChangedListener> listeners = new ArrayList<DataBooleanChangedListener>();

    public static boolean getMyBoolean() {
        return myBoolean;
    }

    public static void setMyBoolean(boolean value) {
        myBoolean = value;

        for (DataBooleanChangedListener l : listeners) {
            l.OnMyBooleanChanged();
        }
    }

    public static void addMyBooleanListener(DataBooleanChangedListener l) {
        listeners.add(l);
    }
}

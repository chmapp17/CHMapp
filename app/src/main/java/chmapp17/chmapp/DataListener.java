package chmapp17.chmapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioan.mihailiuc on 6/21/2017.
 */
interface DataBooleanChangedListener {
    public void OnMyBooleanChanged();
}

public class DataListener {
    private static boolean myBoolean;
    private static List<DataBooleanChangedListener> listeners = new ArrayList<DataBooleanChangedListener>();

    public static boolean getMyBoolean() { return myBoolean; }

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

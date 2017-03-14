package chmapp17.chmapp;

/**
 * Created by Edward on 3/11/2017.
 */

import android.content.Context;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GetCellTowerInfo {

    public static JSONArray getCellTowerObjects(Context ctx) {

        TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        JSONArray cellTowers = new JSONArray();

        // Type of the network
        int phoneTypeInt = tel.getPhoneType();
        String phoneType = null;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;

        //from Android M up must use getAllCellInfo
        List<CellInfo> infos = tel.getAllCellInfo();
        for (int i = 0; i < infos.size(); ++i) {
            try {
                JSONObject cellObj = new JSONObject();
                CellInfo info = infos.get(i);
                if (info instanceof CellInfoGsm) {
                    CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                    CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                    cellObj.put("cellId", identityGsm.getCid());
                    cellObj.put("locationAreaCode", identityGsm.getLac());
                    cellObj.put("mobileCountryCode", identityGsm.getMcc());
                    cellObj.put("mobileNetworkCode", identityGsm.getMnc());
                    cellObj.put("signalStrength", gsm.getDbm());
                    cellTowers.put(cellObj);
                } else if (info instanceof CellInfoLte) {
                    CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                    cellObj.put("cellId", identityLte.getCi());
                    cellObj.put("locationAreaCode", identityLte.getTac());
                    cellObj.put("mobileCountryCode", identityLte.getMcc());
                    cellObj.put("mobileNetworkCode", identityLte.getMnc());
                    cellObj.put("signalStrength", lte.getDbm());
                    cellTowers.put(cellObj);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return cellTowers;
    }
}

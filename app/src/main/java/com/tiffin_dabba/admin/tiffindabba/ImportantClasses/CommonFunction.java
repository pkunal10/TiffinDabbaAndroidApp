package com.tiffin_dabba.admin.tiffindabba.ImportantClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ADMIN on 17-09-2017.
 */

public class CommonFunction {

    public boolean IsInternetAvailable(Context context)
    {
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkinfo=connectivityManager.getActiveNetworkInfo();
            return activeNetworkinfo!=null&&activeNetworkinfo.isConnected();
    }

}

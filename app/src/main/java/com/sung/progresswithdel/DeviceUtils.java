package com.sung.progresswithdel;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by sung on 2017/7/4.
 */

public class DeviceUtils {

    public static int dipToPX(final Context ctx, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }
}

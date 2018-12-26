package com.td.yassine.zekri.melomeet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.td.yassine.zekri.melomeet.HomeActivity;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.map.MapActivity;
import com.td.yassine.zekri.melomeet.match.MatchActivity;
import com.td.yassine.zekri.melomeet.messages.MessagesActivity;
import com.td.yassine.zekri.melomeet.model.User;
import com.td.yassine.zekri.melomeet.profile.ProfileActivity;

import java.lang.reflect.Field;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationView) {
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view, final User user) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent i0 = new Intent(context, HomeActivity.class);
                        i0.putExtra(context.getString(R.string.bundle_object_user), user);
                        context.startActivity(i0);
                        break;
                    case R.id.nav_profil:
                        Intent i1 = new Intent(context, ProfileActivity.class);
                        i1.putExtra(context.getString(R.string.bundle_object_user), user);
                        context.startActivity(i1);
                        break;
                    case R.id.nav_map:
                        Intent i2 = new Intent(context, MapActivity.class);
                        i2.putExtra(context.getString(R.string.bundle_object_user), user);
                        context.startActivity(i2);
                        break;
                    case R.id.nav_match:
                        Intent i3 = new Intent(context, MatchActivity.class);
                        i3.putExtra(context.getString(R.string.bundle_object_user), user);
                        context.startActivity(i3);
                        break;
                    case R.id.nav_messages:
                        Intent i4 = new Intent(context, MessagesActivity.class);
                        i4.putExtra(context.getString(R.string.bundle_object_user), user);
                        context.startActivity(i4);
                        break;
                }
                return false;
            }
        });
    }

//    public static void disableShiftMode(BottomNavigationView view) {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
//        try {
//            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//            shiftingMode.setAccessible(true);
//            shiftingMode.setBoolean(menuView, false);
//            shiftingMode.setAccessible(false);
//            for (int i = 0; i < menuView.getChildCount(); i++) {
//                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                //noinspection RestrictedApi
//                item.setShiftingMode(false);
//                // set once again checked value, so view will be updated
//                //noinspection RestrictedApi
//                item.setChecked(item.getItemData().isChecked());
//            }
//        } catch (NoSuchFieldException e) {
//            Log.e("BNVHelper", "Unable to get shift mode field", e);
//        } catch (IllegalAccessException e) {
//            Log.e("BNVHelper", "Unable to change value of shift mode", e);
//        }
//    }
}

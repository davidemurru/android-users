package me.nootify.users;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.concurrent.Callable;

/**
 * Utilities class with static methods.
 */
public class Utilities {

    public interface showWarningCommand {
        void execute();
    }

    /*
     * Check network availability.
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * Simple method for show and hide two views.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void showFadeView(final Context context, final View showView, final View hideView) {

        if (context == null)
            return;

        // These operation can be execute only by the main Thread.
        hideView.post(new Runnable() {

            @Override
            public void run() {
                if (!Utilities.hasHoneycombMR1()) {

                    if (hideView != null)
                        hideView.setVisibility(View.GONE);

                    if (showView != null)
                        showView.setVisibility(View.VISIBLE);

                } else {

                    int mShortAnimationDuration;

                    mShortAnimationDuration = context.getResources().getInteger(
                            android.R.integer.config_longAnimTime);

                    // Set the "show" view to 0% opacity but visible, so that it is
                    // visible
                    // (but fully transparent) during the animation.

                    if (showView != null) {
                        showView.setAlpha(0f);
                        showView.setVisibility(View.VISIBLE);

                        // Animate the "show" view to 100% opacity, and clear any
                        // animation
                        // listener set on
                        // the view. Remember that listeners are not limited to the
                        // specific
                        // animation
                        // describes in the chained method calls. Listeners are set
                        // on the
                        // ViewPropertyAnimator object for the view, which persists
                        // across
                        // several
                        // animations.
                        showView.animate().alpha(1f)
                                .setDuration(mShortAnimationDuration)
                                .setListener(null);
                    }

                    if (hideView != null) {
                        // Animate the "hide" view to 0% opacity. After the
                        // animation ends,
                        // set
                        // its visibility
                        // to GONE as an optimization step (it won't participate in
                        // layout
                        // passes, etc.)
                        hideView.animate().alpha(0f)
                                .setDuration(mShortAnimationDuration)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        hideView.setVisibility(View.GONE);
                                        hideView.setAlpha(1f);
                                    }
                                });
                    }

                }
            }

        });

    }

    /*
     * Return whether the current version of Android is equal or superior of HoneyComb.
     */
    final public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /*
     * Return whether the current version of Android is equal or superior of HoneyCombMR1.
     */
    final public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static String getUrlRandomPictures(int position) {
        return "http://lorempixel.com/512/512/people/" + Integer.valueOf((position % 10) + 1);
    }

    public static void showWarning(final View view, final String message, final Callable<Void> func) {

        Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            func.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();

    }
}

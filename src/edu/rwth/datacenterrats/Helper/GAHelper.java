package edu.rwth.datacenterrats.Helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
/**
 * GoogleAnalytics Helper Class
 * Helps to track all the Events. Collects them and posts them.
 * 
 * @author
 *
 */
public class GAHelper {
    String activity;
    static GoogleAnalyticsTracker tracker;
    static int instanceCount = 0;
    long start;

    // Limit the number of events due to outofmemory exceptions of analytics sdk
    final static int MAX_EVENTS_BEFORE_DISPATCH = 10;
    final static String TAG = "GAHelper";
    static int eventCount = 0;

    static final ExecutorService tpe = Executors.newSingleThreadExecutor();

    public GAHelper(final Context c, final String activity) {
        this.activity = activity;
        instanceCount++;
        if (tracker == null) {
            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    tracker = GoogleAnalyticsTracker.getInstance();
                    tracker.start("UA-24833798-1",60, c.getApplicationContext());
                }
            });
        }
    }

    public void onResume() {
        this.trackPageView("/"+this.activity);
    }

    public synchronized void destroy () {
        instanceCount--;
        if (instanceCount <= 0) {
            tpe.submit(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "destroying GA");
                    if (tracker != null)
                        tracker.stop();
                    instanceCount = 0;
                }
            });
        }
    }

    protected void tick() throws InterruptedException {
        Thread.sleep(3000);
        this.start = System.currentTimeMillis();
    }

    public void log (final String l) {

    }

    public void trackClick(final String button) {
        checkDispatch();
        tpe.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    tick();
                    tracker.trackEvent(
                            "clicks",  // Category
                            activity+"-button",  // Action
                            button, // Label
                            1);
                    log("trackClick:"+button);
                } catch (final Exception e) {
                    Log.e(TAG, "Error tracking", e);
                }
            }
        });
    }

    public void trackEvent (final String category, final String action, final String label, final int count) {
        checkDispatch();
        tpe.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    tick();
                    tracker.trackEvent(
                            category,  // Category
                            action,  // Action
                            activity+"-"+label, // Label
                            1);
                    log("trackEvent:"+category + "#"+action+"#"+label+"#"+count);
                } catch (final Exception e) {
                    Log.e(TAG, "Error tracking", e);
                }
            }
        });
    }

    public void trackPopupView (final String popup) {
        checkDispatch();
        tpe.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    tick();
                    final String page = "/"+activity+"/"+popup;
                    tracker.trackPageView(page);
                    log("trackPageView:"+page);
                } catch (final Exception e) {
                    Log.e(TAG, "Error tracking", e);
                }
            }
        });
    }

    public void trackPageView (final String page) {
        checkDispatch();
        tpe.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    tick();
                    tracker.trackPageView(page);
                    log("trackPageView:"+page);
                } catch (final Exception e) {
                    Log.e(TAG, "Error tracking", e);
                }
            }
        });
    }

    public void checkDispatch() {
        eventCount++;
        if (eventCount >= MAX_EVENTS_BEFORE_DISPATCH)
            dispatch();
    }

    public void dispatch(){
        eventCount = 0;
        tpe.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    tick();
                    tracker.dispatch();
                    log("dispatched");
                } catch (final Exception e) {
                    Log.e(TAG, "Error dispatching", e);
                }
            }
        });
    }
}

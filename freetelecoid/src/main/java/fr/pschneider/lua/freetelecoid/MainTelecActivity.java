package fr.pschneider.lua.freetelecoid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainTelecActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_telec);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Resources res = getResources();
        // connexion_timeout = res.getInteger(R.integer.connexion_timeout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_telec, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void ExecuteClick(final KeyHitParams key_prms) {
        final AsyncTask<KeyHitParams, Integer, Integer> keyHitParamsIntegerIntegerAsyncTask = new AsyncTask<KeyHitParams, Integer, Integer>() {
            // TODO remove "get" calls
            private int connection_timeout = 1000;
            private String uri = getString(R.string.uri_hd1);

            @Override
            protected Integer doInBackground(KeyHitParams... params) {
                int count = params.length;
                int pi = 0;
                for (KeyHitParams param : params) {
                    String stringUrl = String.format(uri,
                            PlaceholderFragment.code_hd1,
                            param.key,
                            param.longclick ? "true" : "false",
                            param.repeat);
                    try {
                        URL url = new URL(stringUrl);
                        HttpURLConnection cx = (HttpURLConnection) url.openConnection();
                        cx.setConnectTimeout(connection_timeout);
                        try {
                            InputStream in = new BufferedInputStream(cx.getInputStream());
                            // TODO use in.read();
                            in.close();
                        } catch (FileNotFoundException fex) {
                            onError(pi, getString(R.string.e_code_hd));
                        } catch (Exception ex) {
                            onError(pi, ex.getMessage());
                        } finally {
                            cx.disconnect();
                            pi++;
                        }

                    } catch (MalformedURLException uex) {
                        Logger.getAnonymousLogger().log(Level.ALL, "Mal formed url: " + stringUrl);
                    } catch (IOException IOEx) {
                        Logger.getAnonymousLogger().log(Level.ALL,
                                String.format("IO Exception connecting to %s : %S", stringUrl, IOEx.getMessage()));
                    }
                }
                return null;
            }

            public void onError(int index, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            keyHitParamsIntegerIntegerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, key_prms);
        else
            keyHitParamsIntegerIntegerAsyncTask.execute(key_prms);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static String code_hd1 = null;
        public static Map<Integer, String> keyMap = new HashMap<>();
        Animation lgClickAnim;
        Animation shClickAnim;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_telec, container, false);

            lgClickAnim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.longclick);
            shClickAnim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.abc_fade_out);
            keyMap.put(R.id.fbt_0, "0");
            keyMap.put(R.id.fbt_1, "1");
            keyMap.put(R.id.fbt_2, "2");
            keyMap.put(R.id.fbt_3, "3");
            keyMap.put(R.id.fbt_4, "4");
            keyMap.put(R.id.fbt_5, "5");
            keyMap.put(R.id.fbt_6, "6");
            keyMap.put(R.id.fbt_7, "7");
            keyMap.put(R.id.fbt_8, "8");
            keyMap.put(R.id.fbt_9, "9");
            keyMap.put(R.id.fbt_blue, "blue");
            keyMap.put(R.id.fbt_red, "red");
            keyMap.put(R.id.fbt_green, "green");
            keyMap.put(R.id.fbt_yellow, "yellow");
            keyMap.put(R.id.fbt_home, "home");
            keyMap.put(R.id.fbt_up, "up");
            keyMap.put(R.id.fbt_down, "down");
            keyMap.put(R.id.fbt_left, "left");
            keyMap.put(R.id.fbt_right, "right");
            keyMap.put(R.id.fbt_play, "play");
            keyMap.put(R.id.fbt_bwd, "bwd");
            keyMap.put(R.id.fbt_fwd, "fwd");
            keyMap.put(R.id.fbt_help, "help");
            keyMap.put(R.id.fbt_info, "info");
            keyMap.put(R.id.fbt_mail, "mail");
            keyMap.put(R.id.fbt_epg, "epg");
            keyMap.put(R.id.fbt_media, "media");
            keyMap.put(R.id.fbt_mute, "mute");
            keyMap.put(R.id.fbt_ok, "ok");
            keyMap.put(R.id.fbt_options, "options");
            keyMap.put(R.id.fbt_pip, "pip");
            keyMap.put(R.id.fbt_vol_inc, "vol_inc");
            keyMap.put(R.id.fbt_vol_dec, "vol_dec");
            keyMap.put(R.id.fbt_prgm_dec, "prgm_dec");
            keyMap.put(R.id.fbt_prgm_inc, "prgm_inc");
            keyMap.put(R.id.fbt_prev, "prev");
            keyMap.put(R.id.fbt_next, "next");
            keyMap.put(R.id.fbt_power, "power");
            keyMap.put(R.id.fbt_tv, "tv");
            Iterator<Integer> keys = keyMap.keySet().iterator();

            Resources res = getResources();
            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            code_hd1 = defaultSharedPreferences.getString(res.getString(R.string.pref_code_hd1), "");
            int autoRepeatTimeout = res.getInteger(R.integer.autoRepeatDelay);
            int longClickTimeout = res.getInteger(R.integer.longClickTimeout);
            int autoRepeatDelay = res.getInteger(R.integer.autoRepeatDelay);
            // TODO if (defaultSharedPreferences.getBoolean(res.getString(R.string.pref_V5),false))

            while (keys.hasNext()) {
                Integer id = keys.next();
                View v = rootView.findViewById(id);
                View.OnTouchListener l;

                switch (id) {
                    case R.id.fbt_vol_dec:
                    case R.id.fbt_vol_inc:
                    case R.id.fbt_prgm_dec:
                    case R.id.fbt_prgm_inc:
                    case R.id.fbt_left:
                    case R.id.fbt_right:
                    case R.id.fbt_up:
                    case R.id.fbt_down:
                        l = new FreeTelecAutoRepeatTouchListener((MainTelecActivity) this.getActivity(), autoRepeatTimeout, autoRepeatDelay);
                        break;
                    default:
                        l = new FreeTelecLongClickTouchListner((MainTelecActivity) this.getActivity(), longClickTimeout);
                }
                v.setOnTouchListener(l);
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.FreeTelecoidHd1).toUpperCase(l);
                case 1:
                    return getString(R.string.FreeTelecoidFavorites).toUpperCase(l);
                case 2:
                    return getString(R.string.FreeTelecoidHd2).toUpperCase(l);
            }
            return null;
        }
    }

}

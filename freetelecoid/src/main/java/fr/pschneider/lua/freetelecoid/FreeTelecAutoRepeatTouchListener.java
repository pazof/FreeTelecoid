package fr.pschneider.lua.freetelecoid;

import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

import static android.os.SystemClock.sleep;

/**
 * Created by Paul Schneider <paul@pschneider.fr> on 22/06/15.
 * This file is part of fr.pschneider.lua.freetelecoid.
 * <p/>
 * fr.pschneider.lua.freetelecoid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with fr.pschneider.lua.freetelecoid.  If not, see <http://www.gnu.org/licenses/>.
 */
public class FreeTelecAutoRepeatTouchListener implements View.OnTouchListener {


    private MainTelecActivity telecActivity = null;

    public FreeTelecAutoRepeatTouchListener(MainTelecActivity a, long timeout, long delay) {
        telecActivity = a;
        specialClickTimeout = timeout;
        autoRepeatDelay = delay;
    }

    private  long specialClickTimeout = 1000;
    private  long autoRepeatDelay = 200;

    class LongTouchArgs {
        public KeyHitParams params;
        public View view;

        public LongTouchArgs(View v, KeyHitParams prms) {
            params = prms;
            view = v;
        }
    }
    AsyncTask<LongTouchArgs,Void,Void> hitTask=null;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action =  event.getAction();
        long dtime =  event.getEventTime() - event.getDownTime();
        KeyHitParams prms = new KeyHitParams();
        prms.key = MainTelecActivity.PlaceholderFragment.keyMap.get(v.getId());
        prms.repeat = 1;
        if (action == MotionEvent.ACTION_DOWN ) {
                if (hitTask!=null)
                    if (hitTask.cancel(true));
                hitTask = new AsyncTask<LongTouchArgs,Void,Void>() {
                    @Override
                    protected Void doInBackground(LongTouchArgs... params) {
                        sleep(specialClickTimeout);
                        params[0].params.longclick = false;
                        params[0].params.repeat = 1;
                            while (params[0].view.isPressed()) {
                                // repeating
                                telecActivity.ExecuteClick(params[0].params);
                                sleep(autoRepeatDelay);
                            }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        hitTask = null;
                    }
                };
                hitTask.execute(new LongTouchArgs(v, prms));
            }
        else
         if (action == MotionEvent.ACTION_UP) {
                if (hitTask!=null)
                    hitTask.cancel(true);
                telecActivity.ExecuteClick(prms);
            }
        return false;
    }
}

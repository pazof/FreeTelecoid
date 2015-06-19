package fr.pschneider.lua.freetelecoid;

import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

import static android.os.SystemClock.sleep;

/**
 * Created by Paul Schneider <paul@pschneider.fr> on 18/06/15.
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
public class FreeTelecTouchListner implements View.OnTouchListener {

    private MainTelecActivity telecActivity = null;

    public FreeTelecTouchListner(MainTelecActivity a, String code_hd1) {
        telecActivity = a;
    }

    private final long longClickTimeout = 500;
    private final long delayBetweenLongAndRepeat = 1000;
    private final long autoRepeatDelay = 200;

    class LongTouchArgs {
        public KeyHitParams params;
        public View view;

        public LongTouchArgs(View v, KeyHitParams prms) {
            params = prms;
            view = v;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action =  event.getAction();
        long dtime =  event.getEventTime() - event.getDownTime();
        if (action == MotionEvent.ACTION_DOWN || action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_MOVE) {
            KeyHitParams prms = new KeyHitParams();
            prms.key = MainTelecActivity.PlaceholderFragment.keyMap.get(v.getId());
            prms.repeat = 1;
            if (action == MotionEvent.ACTION_DOWN) {
                new AsyncTask<LongTouchArgs,Void,Void>() {
                    @Override
                    protected Void doInBackground(LongTouchArgs... params) {
                        sleep(longClickTimeout);
                        if (params[0].view.isPressed()) {
                            params[0].params.longclick = true;
                            telecActivity.ExecuteClick(params[0].params);
                            sleep(delayBetweenLongAndRepeat);
                            while (params[0].view.isPressed()) {
                                sleep(autoRepeatDelay);
                                params[0].params.longclick = false;
                                params[0].params.repeat = 1;
                                telecActivity.ExecuteClick(params[0].params);
                            }
                        }
                        return null;
                    }
                }.execute(new LongTouchArgs(v,prms));
            }
            if (action == MotionEvent.ACTION_UP) {
                telecActivity.ExecuteClick(prms);
            }
        }
        return false;
    }
}

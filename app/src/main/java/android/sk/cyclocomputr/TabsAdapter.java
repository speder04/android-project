package android.sk.cyclocomputr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Fero on 31. 5. 2015.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {

    private int TABS_COUNT = 2;

    private TachoFragment tachoFragment;
    private SpeedoFragment speedoFragment;

    public TabsAdapter(FragmentManager fm) {
        super(fm);
        tachoFragment = new TachoFragment();
        speedoFragment = new SpeedoFragment();
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return tachoFragment;
            case 1:
                return speedoFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return TABS_COUNT;
    }

    public void setSpeedUnitText(String s) {
        tachoFragment.setSpeedUnitTextView(s);
    }

    public void setSpeedText(String s) {
        tachoFragment.setSpeed(s);
    }

    public void setAccuracyText(String s) {
        tachoFragment.setAccuracy(s);
    }

    public void setMaxSpeedText(String s) {
        speedoFragment.setMaxSpeed(s);
    }

    public void setDistanceText(String s) {
        tachoFragment.setDistance(s);
    }

    public void setTimeText(String s) {
        tachoFragment.setDayTimeTextView(s);
    }

    public void setAvgSpeedText(String s) {
        speedoFragment.setAvgSpeed(s);
    }

    public void setDistanceUnitText(String s) {
        tachoFragment.setDistanceUnitTextView(s);
        speedoFragment.setTotalDistanceUnit(s);
    }

    public boolean getIsCreate() {
        boolean isCreate = tachoFragment.getIsCreate() && speedoFragment.getIsCreate();
        return isCreate;
    }

    public void setMaxAvgSpeedUnitText(String s) {
        speedoFragment.setMaxAvgSpeedUnit(s);
    }

    public void setTotalTimeText(String s) {
        speedoFragment.setTotalTime(s);
    }

    public void setTotalDistanceText(String s) {
        speedoFragment.setTotalDistance(s);
    }

    public void setCompasText(String s) {
        tachoFragment.setCompasTextView(s);
    }
}

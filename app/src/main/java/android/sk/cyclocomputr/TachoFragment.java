package android.sk.cyclocomputr;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TachoFragment extends Fragment {

    private TextView speedTextView;
    private TextView distanceTextView;
    private TextView accTextView;
    private TextView dayTimeTextView;
    private TextView speedUnitTextView;
    private TextView compasTextView;
    private TextView distanceUnitTextView;


    private boolean isCrate;

    public TachoFragment() {
        // Required empty public constructor
        isCrate = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflaterView = inflater.inflate(R.layout.fragment_tacho, container, false);

        speedTextView = (TextView) inflaterView.findViewById(R.id.speed_text_view);
        distanceTextView = (TextView) inflaterView.findViewById(R.id.distance_text_view);
        accTextView = (TextView) inflaterView.findViewById(R.id.accuracy_text_view);
        dayTimeTextView = (TextView) inflaterView.findViewById(R.id.day_time_text_view);
        speedUnitTextView = (TextView) inflaterView.findViewById(R.id.speed_unit_text_view);
        compasTextView = (TextView) inflaterView.findViewById(R.id.compas_text_view);
        distanceUnitTextView = (TextView) inflaterView.findViewById(R.id.distanceUnitTextView);
        isCrate = true;

        return inflaterView;
    }

    public void setSpeed(String speed) {
        if (isCrate) {
            speedTextView.setText(speed);
        }
    }

    public void setAccuracy(String acc) {
        if (isCrate)
            accTextView.setText(acc);
    }

    public void setDistance(String distance) {
        if (isCrate)
            distanceTextView.setText(distance);
    }

    public void setSpeedUnitTextView(String speedUnit) {
        if (isCrate)
            speedUnitTextView.setText(speedUnit);
    }

    public void setDayTimeTextView(String time) {
        if (isCrate)
            dayTimeTextView.setText(time);
    }

    public void setDistanceUnitTextView(String unit) {
        if (isCrate)
            distanceUnitTextView.setText(unit);
    }

    public void setCompasTextView(String s) {
        if (isCrate) {
            compasTextView.setText(s);
        }
    }

    public boolean getIsCreate() {
        return isCrate;
    }
}

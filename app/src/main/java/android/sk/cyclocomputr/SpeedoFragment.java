package android.sk.cyclocomputr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpeedoFragment extends Fragment {

    private TextView maxSpeedTextView;
    private TextView avgSpeedTextView;
    private TextView maxSpeedUnitTextView;
    private TextView avgSpeedUnitTextView;
    private TextView totalDistanceUnitTextView;
    private TextView totalTimeTextView;
    private TextView totalDistanceTextView;

    private boolean isCrate;

    public SpeedoFragment() {
        // Required empty public constructor
        isCrate = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflaterView = inflater.inflate(R.layout.fragment_speedo, container, false);

        maxSpeedTextView = (TextView) inflaterView.findViewById(R.id.max_speed_text_view);
        avgSpeedTextView = (TextView) inflaterView.findViewById(R.id.avg_speed_text_view);
        maxSpeedUnitTextView = (TextView) inflaterView.findViewById(R.id.maxSpeedUnitTextView);
        avgSpeedUnitTextView = (TextView) inflaterView.findViewById(R.id.avgSpeedUnitTextView);
        totalDistanceUnitTextView = (TextView) inflaterView.findViewById(R.id.totalDistanceUnitTextView);
        totalTimeTextView = (TextView) inflaterView.findViewById(R.id.totalTimeTextView);
        totalDistanceTextView = (TextView) inflaterView.findViewById(R.id.totalDistanceTextView);
        isCrate = true;

        return inflaterView;
    }

    public void setMaxSpeed(String speed) {
        if (isCrate) {
            maxSpeedTextView.setText(speed);
        }
    }

    public void setAvgSpeed(String avgSpeed) {
        if (isCrate) {
            avgSpeedTextView.setText(avgSpeed);
        }
    }

    public void setMaxAvgSpeedUnit(String unit) {
        if (isCrate) {
            avgSpeedUnitTextView.setText(unit);
            maxSpeedUnitTextView.setText(unit);
        }
    }

    public void setTotalDistanceUnit(String unit) {
        if (isCrate) {
            totalDistanceUnitTextView.setText(unit);
        }
    }

    public void setTotalTime(String time) {
        if (isCrate) {
            totalTimeTextView.setText(time);
        }
    }

    public void setTotalDistance(String distance) {
        if (isCrate) {
            totalDistanceTextView.setText(distance);
        }
    }

    public boolean getIsCreate() {
        return isCrate;
    }
}

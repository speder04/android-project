package android.sk.cyclocomputr;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.sk.cyclocomputr.contentprovider.MyCycloComputrContentProvider;
import android.sk.cyclocomputr.database.PointsTable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GraphActivity extends ActionBarActivity {

    // http://www.achartengine.org/index.html

    private GraphicalView view;

    private Uri uri;

    private List<Double> x;
    private List<Double> alt;
    private List<Double> lat;
    private List<Double> lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);

        x = new ArrayList<Double>();
        alt = new ArrayList<Double>();
        lon = new ArrayList<Double>();
        lat = new ArrayList<Double>();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            uri = extras.getParcelable(MyCycloComputrContentProvider.CONTENT_ITEM_TYPE2);

            fillData(uri);
        }
    }

    public void fillData(Uri uri) {
        String[] projection = {
                PointsTable.COLUMN_LATITUDE,
                PointsTable.COLUMN_LONGITUDE,
                PointsTable.COLUMN_ALTITUDE
        };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int i = 1;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                x.add((double) i);
                i++;
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PointsTable.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PointsTable.COLUMN_LONGITUDE));
                double altitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PointsTable.COLUMN_ALTITUDE));
                lat.add(latitude);
                lon.add(longitude);
                alt.add(altitude);
            }
        }
        cursor.close();

        paintGraph();

        //Toast.makeText(this, String.valueOf(lat.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, String.valueOf(lon.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, String.valueOf(alt.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, String.valueOf(x.size()), Toast.LENGTH_SHORT).show();
    }

    public void paintGraph() {
        XYSeries series = setSeries();

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[] { 20, 30, 15, 0 });
        renderer.setZoomButtonsVisible(true);
        renderer.setPointSize(5);

        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();

        renderer.addSeriesRenderer(seriesRenderer);

        seriesRenderer.setPointStyle(PointStyle.CIRCLE);
        seriesRenderer.setFillPoints(true);
        seriesRenderer.setDisplayChartValues(true);
        seriesRenderer.setDisplayChartValuesDistance(10);

        view = ChartFactory.getLineChartView(this, dataset, renderer);

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        layout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        view.repaint();
    }

    public XYSeries setSeries() {
        XYSeries series = new XYSeries("XYSeries");

        for (int i = 0; i < x.size(); i++) {
            series.add(x.get(i), alt.get(i));
        }

        return series;
    }
}

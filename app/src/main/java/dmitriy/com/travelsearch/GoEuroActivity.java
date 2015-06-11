package dmitriy.com.travelsearch;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.larvalabs.svgandroid.SVGParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import dmitriy.com.travelsearch.datepicker.DateFragment;

public class GoEuroActivity extends Activity implements DateFragment.DateChangeListener {
    private LocationProvider mLocationProvider;
    private RetainedFragment mDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_euro);
        setRetainFragment();
        mLocationProvider = new LocationProvider(this);
        setTextViews();
        setIcons();
        setCurrDate();
        setAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableSearchButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setRetainFragment() {
        FragmentManager fm = getFragmentManager();
        mDataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        if (mDataFragment == null) {
            mDataFragment = new RetainedFragment();
            AutoCompleteTextView placeFrom = (AutoCompleteTextView) findViewById(R.id.placeFrom);
            AutoCompleteTextView placeTo = (AutoCompleteTextView) findViewById(R.id.placeTo);

            mDataFragment.getUiControlsReady().put(placeFrom.getId(),
                    new StringBooleanPair(new String(""),Boolean.FALSE));
            mDataFragment.getUiControlsReady().put(placeTo.getId(),
                    new StringBooleanPair(new String(""),Boolean.FALSE));
            fm.beginTransaction().add(mDataFragment, "data").commit();
        }
    }

    private void setTextViews() {
        AutoCompleteTextView placeFrom = (AutoCompleteTextView) findViewById(R.id.placeFrom);
        AutoCompleteTextView placeTo = (AutoCompleteTextView) findViewById(R.id.placeTo);
        placeFrom.setAdapter(new HintAdapter(this, mLocationProvider));
        placeTo.setAdapter(new HintAdapter(this, mLocationProvider));
        placeFrom.setOnClickListener(mAutoCompleteClickListener);
        placeTo.setOnClickListener(mAutoCompleteClickListener);
        placeFrom.setOnItemClickListener(new OnAdapterClickListener(placeFrom));
        placeTo.setOnItemClickListener(new OnAdapterClickListener(placeTo));
        placeFrom.addTextChangedListener(new OnTextChangedListener(placeFrom));
        placeTo.addTextChangedListener(new OnTextChangedListener(placeTo));
    }

    private void setIcons() {
        BitmapDrawable drawableDate = new BitmapDrawable(getResources(),
                Utils.Svg2Bitmap(SVGParser.getSVGFromResource(getResources(), R.raw.date),
                        Utils.getButtonIconSize(this)));
        ImageView imageView = (ImageView)findViewById(R.id.buttonDate);
        imageView.setImageDrawable(drawableDate);
    }

    public void onShowDatePickerDialog(View v) {
        DateFragment fragment = new DateFragment();
        fragment.setListener(this);
        fragment.show(getFragmentManager(), "datePicker");
    }

    public void onSearchClick(View v) {
        Toast toast = Toast.makeText(this, getResources().getString(R.string.search), Toast.LENGTH_LONG);
        toast.show();
    }

    private void setCurrDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        OnDateChanged(y, m, d);
    }

    @Override
    public void OnDateChanged(int y, int m, int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        String dateString = (sdf.format(cal.getTime()));

        TextView dateTextView = (TextView) findViewById(R.id.textviewDate);
        dateTextView.setText(dateString);
    }

    private void setAnimation() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100);
        set.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(1000);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.dateLayout);
        viewGroup.setLayoutAnimation(controller);
    }

    private void enableSearchButton() {
        Button searchButton = (Button) findViewById(R.id.buttonSearch);
        boolean enable = true;

        HashMap<Integer, StringBooleanPair> uiControlsReady = mDataFragment.getUiControlsReady();

        for (Map.Entry<Integer, StringBooleanPair> entry : uiControlsReady.entrySet()) {
            if (entry.getValue().mBoolean == Boolean.FALSE) {
                enable = false;
                break;
            }
        }
        searchButton.setEnabled(enable);
    }

    class OnAdapterClickListener implements AdapterView.OnItemClickListener {
        private TextView mOwner;

        OnAdapterClickListener(TextView owner) {
            mOwner = owner;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            mDataFragment.getUiControlsReady().put(mOwner.getId(),
                    new StringBooleanPair(mOwner.getText().toString(), Boolean.TRUE));

            enableSearchButton();
        }
    };

    class OnTextChangedListener implements TextWatcher {
        private View mOwner;

        OnTextChangedListener(View owner) {
            mOwner = owner;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            StringBooleanPair sbp = mDataFragment.getUiControlsReady().get(mOwner.getId());
            if (sbp != null) {
                if (sbp.mBoolean == Boolean.TRUE && sbp.mString.contentEquals(charSequence))
                    return;
            }

            mDataFragment.getUiControlsReady().put(mOwner.getId(), new StringBooleanPair(new String(""), Boolean.FALSE));
            enableSearchButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    View.OnClickListener mAutoCompleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((AutoCompleteTextView)view).showDropDown();
        }
    };


}


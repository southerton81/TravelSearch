package dmitriy.com.travelsearch;

import android.content.Context;
import android.location.Location;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HintAdapter extends ArrayAdapter<String> implements Filterable {
    static private Location mLastLocation = new Location("");
    static private ModelsCache mModelsCache = new ModelsCache();
    private LayoutInflater mInflater;
    private ArrayList<String> mData = null;
    private ModelsFetcher mModelsFetcher = new ModelsFetcher();
    private LocationProvider mLocationProvider;
    private volatile Pattern mPattern;

    public HintAdapter(Context context, LocationProvider locationProvider) {
        super(context, android.R.layout.simple_list_item_1);
        mLocationProvider = locationProvider;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        String string = mData.get(position);
        Matcher matcher = mPattern.matcher(string);
        Spannable spannableString = new SpannableString(string);

        // Make constraint-matching string span bold
        while (matcher.find())
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = ((TextView) convertView.findViewById(android.R.id.text1));
        textView.setText(spannableString);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                    ModelsFetcher.FetchResult sortedModels = sortModels(acquireModels(constraint.toString()));
                    mData = toStrings(sortedModels.mModels);
                    filterResults.values = mData;
                    filterResults.count = mData.size();
                    mPattern = Utils.createPatternByMasksSeparatedBySpaces(constraint.toString());
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private ModelsFetcher.FetchResult acquireModels(String constraint) {
        ModelsFetcher.FetchResult result = mModelsCache.get(constraint);
        if (result == null) {
            result = mModelsFetcher.Fetch(constraint);
            mModelsCache.put(result);
        }
        return result;
    }

    private ModelsFetcher.FetchResult sortModels(ModelsFetcher.FetchResult models) {
        Location newLocation = mLocationProvider.getLocation();
        if (!newLocation.equals(mLastLocation)) {
            mLastLocation.set(newLocation);
            Collections.sort(models.mModels, mByDistanceComparator);
        }
        return models;
    }

    private ArrayList<String> toStrings(ArrayList<GoEuroPlaceModel> models) {
        ArrayList<String> result = new ArrayList<String>();
        for (GoEuroPlaceModel goEuroPlaceModel : models) {
            StringBuilder stringBuilder = new StringBuilder(goEuroPlaceModel.name);
            stringBuilder.append(" (").append(goEuroPlaceModel.country).append(")");
            result.add(stringBuilder.toString());
        }
        return result;
    }

    public static Comparator<GoEuroPlaceModel> mByDistanceComparator = new Comparator<GoEuroPlaceModel>() {
        private Location mLocation1 = new Location("");
        private Location mLocation2 = new Location("");

        public int compare(GoEuroPlaceModel model1, GoEuroPlaceModel model2) {
            mLocation1.setLongitude(model1.geo_position.longitude);
            mLocation1.setLatitude(model1.geo_position.latitude);
            mLocation2.setLongitude(model2.geo_position.longitude);
            mLocation2.setLatitude(model2.geo_position.latitude);

            float distanceToFirst = mLastLocation.distanceTo(mLocation1);
            float distanceToSecond = mLastLocation.distanceTo(mLocation2);
            int compareResult = Math.round(distanceToFirst - distanceToSecond);
            return compareResult;
        }
    };
}


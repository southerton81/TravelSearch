package dmitriy.com.travelsearch;

import android.app.Fragment;
import android.os.Bundle;
import java.util.HashMap;

public class RetainedFragment extends Fragment {
    private HashMap<Integer, StringBooleanPair> mUiControlsReady = new HashMap<Integer, StringBooleanPair>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public HashMap<Integer, StringBooleanPair> getUiControlsReady() {
        return mUiControlsReady;
    }
};

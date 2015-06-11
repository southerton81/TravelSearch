package dmitriy.com.travelsearch;

import java.util.LinkedHashMap;

public class ModelsCache {
    private static final int MAX_CACHE_ENTRIES = 20;

    LinkedHashMap<String, ModelsFetcher.FetchResult> mCache =
            new LinkedHashMap<String, ModelsFetcher.FetchResult>(MAX_CACHE_ENTRIES) {
        @Override
        protected boolean removeEldestEntry(Entry<String, ModelsFetcher.FetchResult> eldest) {
            return (size() > MAX_CACHE_ENTRIES);
        }
    };

    void put(ModelsFetcher.FetchResult model) {
        mCache.put(model.mConstraint, model);
    }

    ModelsFetcher.FetchResult get(String constraint) {
        return mCache.get(constraint);
    }
}

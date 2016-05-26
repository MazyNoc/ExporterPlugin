package com.nordicusability.exporterplugin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AsyncTaskLoaderEx<T> extends AsyncTaskLoader<T> {

    protected final Bundle args;
    private T mData;

    public AsyncTaskLoaderEx(Context context) {
        super(context);
        this.args = null;
    }

    public AsyncTaskLoaderEx(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(T data) {
        mData = data;
        super.deliverResult(data);
    }

    @Override
    public abstract T loadInBackground();
}

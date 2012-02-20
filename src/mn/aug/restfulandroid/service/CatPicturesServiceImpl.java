package mn.aug.restfulandroid.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class CatPicturesServiceImpl extends IntentService implements CatPicturesService {

	private static final int REQUEST_INVALID = -1;

	private ResultReceiver mCallback;

	private Intent mOriginalRequestIntent;

	public CatPicturesServiceImpl() {
		super("CatPicturesService");
	}

	@Override
	protected void onHandleIntent(Intent requestIntent) {

		mOriginalRequestIntent = requestIntent;

		// Get request data from Intent
		String method = requestIntent.getStringExtra(CatPicturesService.METHOD_EXTRA);
		int resourceType = requestIntent.getIntExtra(CatPicturesService.RESOURCE_TYPE_EXTRA, -1);
		mCallback = requestIntent.getParcelableExtra(CatPicturesService.SERVICE_CALLBACK);

		switch (resourceType) {
		case RESOURCE_TYPE_CAT_PICTURES:

			if (method.equalsIgnoreCase(METHOD_GET)) {
				CatPicturesProcessor processor = new CatPicturesProcessor(getApplicationContext());
				processor.getCatPictures(makeCatPicturesProcessorCallback());
			} else {
				mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			}
			break;

		default:
			mCallback.send(REQUEST_INVALID, getOriginalIntentBundle());
			break;
		}

	}

	@Override
	public CatPicturesProcessorCallback makeCatPicturesProcessorCallback() {
		CatPicturesProcessorCallback callback = new CatPicturesProcessorCallback() {

			@Override
			public void send(int resultCode) {
				if (mCallback != null) {
					mCallback.send(resultCode, getOriginalIntentBundle());
				}
			}
		};
		return callback;
	}

	protected Bundle getOriginalIntentBundle() {
		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
		return originalRequest;
	}
}

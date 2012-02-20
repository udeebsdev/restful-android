package mn.aug.restfulandroid.mock;

import java.util.concurrent.Semaphore;

import mn.aug.restfulandroid.service.CatPicturesProcessorCallback;
import mn.aug.restfulandroid.service.CatPicturesService;

import org.apache.http.HttpStatus;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class MockCatPicturesService extends IntentService implements CatPicturesService {
	
	private static final int REQUEST_INVALID = -1;

	private ResultReceiver mCallback;

	private Intent mOriginalRequestIntent;
	
	private static Semaphore mOnHandleIntentSemaphone;

	private static int mResultCode = HttpStatus.SC_OK;

	public MockCatPicturesService() {
		super("CatPicturesService");
		mOnHandleIntentSemaphone = new Semaphore(0);
	}
	
	public static void setResultCode(int resultCode) {
		mResultCode  = resultCode;
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
				// fake request
				try {
					mOnHandleIntentSemaphone.acquire();
				} catch (InterruptedException e) {}
				makeCatPicturesProcessorCallback().send(mResultCode);
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
	
	public static void releaseOnHandleIntent() {
		mOnHandleIntentSemaphone.release();
	}

}

package ch.ubique.notifyme.app.network;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.crowdnotifier.android.sdk.model.ProblematicEventInfo;

import ch.ubique.notifyme.app.model.Proto;
import ch.ubique.notifyme.base.BuildConfig;
import ch.ubique.notifyme.base.utils.Storage;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TraceKeysServiceController {

	private static final String KEY_BUNDLE_TAG_HEADER = "x-key-bundle-tag";

	private TraceKeysService traceKeysService;
	private Storage storage;

	public TraceKeysServiceController(Context context) {

		storage = Storage.getInstance(context);
		String baseUrl = BuildConfig.PUBLISHED_KEYS_BASE_URL;

		String userAgent = BuildConfig.APPLICATION_ID + ";" + BuildConfig.VERSION_NAME + ";Android;" + Build.VERSION.RELEASE;
		OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
		okHttpBuilder.networkInterceptors().add(new UserAgentInterceptor(userAgent));

		Retrofit bucketRetrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(okHttpBuilder.build())
				.build();

		traceKeysService = bucketRetrofit.create(TraceKeysService.class);
	}

	public void loadTraceKeysAsync(Callback callback) {
		traceKeysService.getTraceKeys(storage.getLastKeyBundleTag()).enqueue(new retrofit2.Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				if (response.isSuccessful()) {
					callback.onTraceKeysLoaded(handleSuccessfulResponse(response));
				} else {
					callback.onTraceKeysLoaded(null);
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				callback.onTraceKeysLoaded(null);
			}
		});
	}

	public List<ProblematicEventInfo> loadTraceKeys() {
		try {
			Response<ResponseBody> response = traceKeysService.getTraceKeys(storage.getLastKeyBundleTag()).execute();
			if (response.isSuccessful()) {
				return handleSuccessfulResponse(response);
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	private List<ProblematicEventInfo> handleSuccessfulResponse(Response<ResponseBody> response) {
		try {
			long keyBundleTag = Long.parseLong(response.headers().get(KEY_BUNDLE_TAG_HEADER));
			storage.setLastKeyBundleTag(keyBundleTag);
			Proto.ProblematicEventWrapper problematicEventWrapper =
					Proto.ProblematicEventWrapper.parseFrom(response.body().byteStream());
			ArrayList<ProblematicEventInfo> problematicEventInfos = new ArrayList<>();
			for (Proto.ProblematicEvent event : problematicEventWrapper.getEventsList()) {
				problematicEventInfos.add(new ProblematicEventInfo(event.getIdentity().toByteArray(),
						event.getSecretKeyForIdentity().toByteArray(), event.getStartTime(), event.getEndTime(),
						event.getEncryptedAssociatedData().toByteArray(), event.getCipherTextNonce().toByteArray()));
			}
			return problematicEventInfos;
		} catch (IOException e) {
			return null;
		}
	}

	public interface Callback {
		void onTraceKeysLoaded(List<ProblematicEventInfo> traceKeys);

	}

}

package ch.ubique.n2step.app.network;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.internal.$Gson$Preconditions;

import ch.ubique.n2step.app.BuildConfig;
import ch.ubique.n2step.app.model.ProblematicEventOuterClass;
import ch.ubique.n2step.app.utils.Storage;
import ch.ubique.n2step.sdk.model.ProblematicEventInfo;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WebServiceController {

	private TraceKeysService traceKeysService;
	private Storage storage;

	public WebServiceController(Context context) {

		storage = Storage.getInstance(context);
		String baseUrl = BuildConfig.PUBLISHED_KEYS_BASE_URL;

		OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

		Retrofit bucketRetrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(okHttpBuilder.build())
				.build();

		traceKeysService = bucketRetrofit.create(TraceKeysService.class);
	}

	public void loadTraceKeysAsync(Callback callback) {
		traceKeysService.getTraceKeys(storage.getLastSync()).enqueue(new retrofit2.Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				if (response.isSuccessful()) {
					callback.onTraceKeysLoaded(handleSuccessfulResponse(response));
				}
				callback.onTraceKeysLoaded(null);
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				callback.onTraceKeysLoaded(null);
			}
		});
	}

	public List<ProblematicEventInfo> loadTraceKeys() {
		try {
			Response<ResponseBody> response = traceKeysService.getTraceKeys(storage.getLastSync()).execute();
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

			//TODO: Uncomment these two lines
				/*
				Date d = response.headers().getDate("date");
				Storage.getInstance(getApplicationContext()).setLastSync(d.getTime());
				 */
			ProblematicEventOuterClass.ProblematicEventWrapper problematicEventWrapper =
					ProblematicEventOuterClass.ProblematicEventWrapper.parseFrom(response.body().byteStream());
			ArrayList<ProblematicEventInfo> problematicEventInfos = new ArrayList<>();
			for (ProblematicEventOuterClass.ProblematicEvent event : problematicEventWrapper.getEventsList()) {
				problematicEventInfos.add(new ProblematicEventInfo(event.getSecretKey().toByteArray(), event.getStartTime(),
						event.getEndTime()));
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

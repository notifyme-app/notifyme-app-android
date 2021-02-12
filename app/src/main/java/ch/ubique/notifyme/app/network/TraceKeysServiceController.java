package ch.ubique.notifyme.app.network;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.crowdnotifier.android.sdk.model.ProblematicEventInfo;

import ch.ubique.notifyme.app.BuildConfig;
import ch.ubique.notifyme.app.model.ProblematicEventOuterClass;
import ch.ubique.notifyme.app.utils.Storage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class TraceKeysServiceController extends BaseServiceController<TraceKeysService> {

	private static final String KEY_BUNDLE_TAG_HEADER = "x-key-bundle-tag";

	private Storage storage;

	public TraceKeysServiceController(Context context) {
		super(BuildConfig.PUBLISHED_KEYS_BASE_URL);
		storage = Storage.getInstance(context);
	}

	public void loadTraceKeysAsync(Callback callback) {
		service.getTraceKeys(storage.getLastKeyBundleTag()).enqueue(new retrofit2.Callback<ResponseBody>() {
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
			Response<ResponseBody> response = service.getTraceKeys(storage.getLastKeyBundleTag()).execute();
			if (response.isSuccessful()) {
				return handleSuccessfulResponse(response);
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	@Override
	protected Class<TraceKeysService> getServiceClass() {
		return TraceKeysService.class;
	}

	private List<ProblematicEventInfo> handleSuccessfulResponse(Response<ResponseBody> response) {
		try {
			long keyBundleTag = Long.parseLong(response.headers().get(KEY_BUNDLE_TAG_HEADER));
			storage.setLastKeyBundleTag(keyBundleTag);
			ProblematicEventOuterClass.ProblematicEventWrapper problematicEventWrapper =
					ProblematicEventOuterClass.ProblematicEventWrapper.parseFrom(response.body().byteStream());
			ArrayList<ProblematicEventInfo> problematicEventInfos = new ArrayList<>();
			for (ProblematicEventOuterClass.ProblematicEvent event : problematicEventWrapper.getEventsList()) {
				problematicEventInfos.add(new ProblematicEventInfo(event.getIdentity().toByteArray(),
						event.getSecretKeyForIdentity().toByteArray(), event.getStartTime(), event.getEndTime(),
						event.getMessage().toByteArray(), event.getNonce().toByteArray()));
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

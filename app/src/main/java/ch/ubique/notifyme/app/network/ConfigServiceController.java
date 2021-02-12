package ch.ubique.notifyme.app.network;

import ch.ubique.notifyme.app.BuildConfig;
import ch.ubique.notifyme.app.model.ConfigResponseModel;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigServiceController extends BaseServiceController<ConfigService> {

	public ConfigServiceController() {
		super(BuildConfig.CONFIG_BASE_URL, GsonConverterFactory.create());
	}

	public void loadConfigAsync(Callback callback) {
		service.getConfig().enqueue(new retrofit2.Callback<ConfigResponseModel>() {
			@Override
			public void onResponse(Call<ConfigResponseModel> call, Response<ConfigResponseModel> response) {
				if (response.isSuccessful()) {
					callback.onConfigLoaded(response.body());
				} else {
					callback.onConfigLoaded(null);
				}
			}

			@Override
			public void onFailure(Call<ConfigResponseModel> call, Throwable t) {
				callback.onConfigLoaded(null);
			}
		});
	}

	@Override
	protected Class<ConfigService> getServiceClass() {
		return ConfigService.class;
	}

	public interface Callback {
		void onConfigLoaded(ConfigResponseModel config);
	}

}

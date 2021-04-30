package ch.ubique.notifyme.app.network;

import android.content.Context;
import android.os.Build;

import ch.ubique.notifyme.app.model.ConfigResponseModel;
import ch.ubique.notifyme.base.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigServiceController {

	private ConfigService configService;

	public ConfigServiceController(Context context) {

		String baseUrl = BuildConfig.CONFIG_BASE_URL;

		String userAgent = BuildConfig.APPLICATION_ID + ";" + BuildConfig.VERSION_NAME + ";Android;" + Build.VERSION.RELEASE;
		OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
		okHttpBuilder.networkInterceptors().add(new UserAgentInterceptor(userAgent));

		Retrofit bucketRetrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(okHttpBuilder.build())
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		configService = bucketRetrofit.create(ConfigService.class);
	}

	public void loadConfigAsync(Callback callback) {
		configService.getConfig().enqueue(new retrofit2.Callback<ConfigResponseModel>() {
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

	public interface Callback {
		void onConfigLoaded(ConfigResponseModel config);

	}

}

package ch.ubique.notifyme.app.network;

import android.os.Build;

import ch.ubique.notifyme.app.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;

public abstract class BaseServiceController<S> {

	protected S service;

	public BaseServiceController(String baseUrl, Converter.Factory... converterFactories) {
		String userAgent = BuildConfig.APPLICATION_ID + ";" + BuildConfig.VERSION_NAME + ";Android;" + Build.VERSION.RELEASE;
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
				.addNetworkInterceptor(new UserAgentInterceptor(userAgent));

		if (BuildConfig.FLAVOR.equals("dev") && BuildConfig.DEBUG) {
			HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
			loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			okHttpClientBuilder.addNetworkInterceptor(loggingInterceptor);
		}

		Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(okHttpClientBuilder.build());

		for (Converter.Factory factory : converterFactories) {
			retrofitBuilder.addConverterFactory(factory);
		}

		service = retrofitBuilder.build().create(getServiceClass());
	}

	protected abstract Class<S> getServiceClass();

}

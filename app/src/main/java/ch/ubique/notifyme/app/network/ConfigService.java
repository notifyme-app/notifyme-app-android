package ch.ubique.notifyme.app.network;

import ch.ubique.notifyme.app.model.ConfigResponseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ConfigService {

	@Headers("Accept: application/json")
	@GET("v1/config")
	Call<ConfigResponseModel> getConfig();

}

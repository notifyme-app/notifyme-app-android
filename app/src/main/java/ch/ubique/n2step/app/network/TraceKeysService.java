package ch.ubique.n2step.app.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface TraceKeysService {

	@Headers("Accept: application/protobuf")
	@GET("v1/traceKeys")
	Call<ResponseBody> getTraceKeys(@Query("lastSync") long lastSync);

}

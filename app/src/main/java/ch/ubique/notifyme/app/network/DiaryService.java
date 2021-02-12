package ch.ubique.notifyme.app.network;

import ch.ubique.notifyme.app.model.DiaryEntryOuterClass;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DiaryService {

	@Headers("Content-Type: application/x-protobuf")
	@POST("v1/debug/diaryEntries")
	Call<ResponseBody> uploadDiaryEntries(@Body DiaryEntryOuterClass.DiaryEntryWrapper diary);

}

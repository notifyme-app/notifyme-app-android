package ch.ubique.notifyme.app.network;

import java.util.ArrayList;
import java.util.List;

import ch.ubique.notifyme.app.BuildConfig;
import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.model.DiaryEntryOuterClass;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.protobuf.ProtoConverterFactory;

public class DiaryServiceController extends BaseServiceController<DiaryService> {

	public DiaryServiceController() {
		super(BuildConfig.DIARY_BASE_URL, ProtoConverterFactory.create());
	}

	public void uploadDiaryEntries(List<DiaryEntry> diaryEntries, Callback callback) {
		DiaryEntryOuterClass.DiaryEntryWrapper diary = mapDiaryEntries(diaryEntries);
		service.uploadDiaryEntries(diary).enqueue(new retrofit2.Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				callback.onDiaryEntriesUploaded(response.isSuccessful());
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				callback.onDiaryEntriesUploaded(false);
			}
		});
	}

	@Override
	protected Class<DiaryService> getServiceClass() {
		return DiaryService.class;
	}

	private DiaryEntryOuterClass.DiaryEntryWrapper mapDiaryEntries(List<DiaryEntry> diaryEntries) {
		List<DiaryEntryOuterClass.DiaryEntry> protoDiaryEntries = new ArrayList<>();

		for (DiaryEntry diaryEntry : diaryEntries) {
			DiaryEntryOuterClass.DiaryEntry protoDiaryEntry = DiaryEntryOuterClass.DiaryEntry.newBuilder()
					.setName(diaryEntry.getVenueInfo().getName())
					.setLocation(diaryEntry.getVenueInfo().getLocation())
					.setRoom(diaryEntry.getVenueInfo().getRoom())
					.setVenueTypeValue(diaryEntry.getVenueInfo().getVenueType().getNumber())
					.setCheckinTime(diaryEntry.getArrivalTime())
					.setCheckOutTIme(diaryEntry.getDepartureTime())
					.build();
			protoDiaryEntries.add(protoDiaryEntry);
		}

		return DiaryEntryOuterClass.DiaryEntryWrapper.newBuilder()
				.addAllDiaryEntries(protoDiaryEntries)
				.build();
	}

	public interface Callback {
		void onDiaryEntriesUploaded(boolean success);
	}

}

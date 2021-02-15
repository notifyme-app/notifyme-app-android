package ch.ubique.notifyme.app.diary.share;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.network.DiaryServiceController;
import ch.ubique.notifyme.app.utils.LoadingState;
import ch.ubique.notifyme.app.utils.Storage;

public class ShareDiaryViewModel extends AndroidViewModel {

	private final MutableLiveData<LoadingState> diaryUploadState = new MutableLiveData<>();
	private final DiaryServiceController diaryServiceController = new DiaryServiceController();
	private final Storage storage = Storage.getInstance(getApplication());

	public ShareDiaryViewModel(@NonNull Application application) {
		super(application);
	}

	public LiveData<LoadingState> getDiaryUploadState() {
		return diaryUploadState;
	}

	public void uploadDiaryEntries(List<DiaryEntry> diaryEntries) {
		diaryUploadState.setValue(LoadingState.LOADING);
		diaryServiceController.uploadDiaryEntries(diaryEntries, success -> {
			if (success) {
				storage.setLastPositiveReportTimestamp(System.currentTimeMillis());
				diaryUploadState.setValue(LoadingState.SUCCESS);
			} else {
				diaryUploadState.setValue(LoadingState.FAILURE);
			}
		});
	}

}

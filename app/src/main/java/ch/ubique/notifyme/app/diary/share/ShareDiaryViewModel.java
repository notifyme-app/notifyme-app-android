package ch.ubique.notifyme.app.diary.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ch.ubique.notifyme.app.model.DiaryEntry;
import ch.ubique.notifyme.app.network.DiaryServiceController;
import ch.ubique.notifyme.app.utils.LoadingState;

public class ShareDiaryViewModel extends ViewModel {

	private final MutableLiveData<LoadingState> diaryUploadState = new MutableLiveData<>();
	private final DiaryServiceController diaryServiceController = new DiaryServiceController();

	public LiveData<LoadingState> getDiaryUploadState() {
		return diaryUploadState;
	}

	public void uploadDiaryEntries(List<DiaryEntry> diaryEntries) {
		diaryUploadState.setValue(LoadingState.LOADING);
		diaryServiceController.uploadDiaryEntries(diaryEntries, success -> {
			if (success) {
				// TODO Store timestamp of diary upload
				diaryUploadState.setValue(LoadingState.SUCCESS);
			} else {
				diaryUploadState.setValue(LoadingState.FAILURE);
			}
		});
	}

}

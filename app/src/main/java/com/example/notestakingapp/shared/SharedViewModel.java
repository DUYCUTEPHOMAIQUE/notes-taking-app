package com.example.notestakingapp.shared;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notestakingapp.utils.NoteDetailsComponent;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<NoteDetailsComponent>> notes = new MutableLiveData<>();

    public void setNotes(List<NoteDetailsComponent> noteList) {
        notes.setValue(noteList);
    }

    public LiveData<List<NoteDetailsComponent>> getNotes() {
        return notes;
    }

    private final MutableLiveData<Boolean> itemLongPressed = new MutableLiveData<>();

    public void setItemLongPressed(boolean isLongPressed) {
        itemLongPressed.setValue(isLongPressed);
    }

    public LiveData<Boolean> getItemLongPressed() {
        return itemLongPressed;
    }
    private final MutableLiveData<Boolean> clearUiEvent = new MutableLiveData<>();

    public void triggerClearUiEvent() {
        clearUiEvent.setValue(true);
    }

    public LiveData<Boolean> getClearUiEvent() {
        return clearUiEvent;
    }
    private final MutableLiveData<Boolean> isDelete = new MutableLiveData<>();

    public void triggerIsDelete(boolean t) {
        clearUiEvent.setValue(t);
    }

    public LiveData<Boolean> getIsDelete() {
        return clearUiEvent;
    }

    private final MutableLiveData<List<Integer>> listNoteId = new MutableLiveData<>();
    public MutableLiveData<List<Integer>> getListNoteId() {
        return listNoteId;
    }
    public void setListNoteId(List<Integer> listNoteId1) {
        listNoteId.setValue(listNoteId1);
    }

    private MutableLiveData<Integer> numberChecked = new MutableLiveData<>();

    public MutableLiveData<Integer> getNumberChecked() {
        return numberChecked;
    }

    public void setNumberChecked(MutableLiveData<Integer> numberChecked) {
        this.numberChecked = numberChecked;
    }
    private final MutableLiveData<Boolean> dataChanged = new MutableLiveData<>();

    public LiveData<Boolean> getDataChanged() {
        return dataChanged;
    }

    public void notifyDataChanged() {
        dataChanged.setValue(true);
    }

    private final MutableLiveData<Boolean> isTodoChange = new MutableLiveData<>();

    public MutableLiveData<Boolean> getNoteEditChangeInsertAudio() {
        return noteEditChangeInsertAudio;
    }
    public void setNoteEditChangeInsertAudio(boolean t) {
        noteEditChangeInsertAudio.setValue(t);
    }

    private final MutableLiveData<Boolean> noteEditChangeInsertAudio = new MutableLiveData<>();
    private final MutableLiveData<Boolean> noteEditChangeInsertDraw = new MutableLiveData<>();

    public MutableLiveData<Boolean> getNoteEditChangeInsertDraw() {
        return noteEditChangeInsertDraw;
    }
    public void setNoteEditChangeInsertDraw(boolean t) {
        noteEditChangeInsertDraw.setValue(t);
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        Log.d("testDDDD", "test setImaged");

        this.imageId = imageId;
    }

    private int imageId = -1;

    public LiveData<Boolean> getIsTodoChange() {
        return isTodoChange;
    }

    public void setIsTodoChange(boolean i) {
        isTodoChange.setValue(i);
    }

    private int AudioId = -1;
    private byte[] audioDate = null;
    private  byte[] imageData = null;

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public byte[] getAudioDate() {
        return audioDate;
    }

    public void setAudioDate(byte[] audioDate) {
        this.audioDate = audioDate;
    }

    public int getAudioId() {
        return AudioId;
    }

    public void setAudioId(int audioId) {
        AudioId = audioId;
    }


    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> tagChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> getTagChanged() {
        return tagChanged;
    }
    public void setTagChanged() {
        tagChanged.setValue(true);
    }

    public MutableLiveData<Boolean> isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        Log.d("audioDuyTest", "set lai playing" + playing);
        isPlaying.setValue(playing);
    }

    private MutableLiveData<Integer> playingPosition = new MutableLiveData<>();

    public LiveData<Integer> getPlayingPosition() {
        return playingPosition;
    }

    public void setPlayingPosition(int position) {
        playingPosition.setValue(position);
    }

    private MutableLiveData<Boolean> test = new MutableLiveData<>();

    public MutableLiveData<Boolean> getTest() {
        return test;
    }
    public void setTest(boolean t) {
        Log.d("testDDDD", "test");
        test.setValue(t);
    }

    public void setTest(MutableLiveData<Boolean> test) {
        this.test = test;
    }

    public MutableLiveData<Boolean> getIsInputFocus() {
        return isInputFocus;
    }

    private MutableLiveData<Boolean> isInputFocus = new MutableLiveData<>();



    public void setInputFocus(boolean inputFocus) {
        isInputFocus.setValue(inputFocus);
    }
    public MutableLiveData<Integer> color = new MutableLiveData<>();

    public MutableLiveData<Integer> getColor() {
        return color;
    }

    public void setColor(int t) {
        color.setValue(t);
    }
    public MutableLiveData<String> language = new MutableLiveData<>();

    public MutableLiveData<String> getLanguage() {
        return language;
    }

    public void setLanguage(String t) {
        language.setValue(t);
    }

    public MutableLiveData<String> stateNotification = new MutableLiveData<>();

    public MutableLiveData<String> getStateNotification() {
        return stateNotification;
    }

    public void setStateNotification(String t) {
        stateNotification.setValue(t);
    }
}

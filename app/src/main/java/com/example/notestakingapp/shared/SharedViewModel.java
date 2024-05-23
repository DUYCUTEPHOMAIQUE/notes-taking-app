package com.example.notestakingapp.shared;
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
}

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
}

package com.clinic.factory;

import com.clinic.diary.Diary;
import com.clinic.storage.CSVDiaryImplementation;

public class CSVDiaryFactory extends DiaryFactory {

    private final String filePath;

    public CSVDiaryFactory(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Diary createDiary() {
        return new Diary(new CSVDiaryImplementation(filePath));
    }

    @Override
    public String toString() { return "CSV Factory"; }
}

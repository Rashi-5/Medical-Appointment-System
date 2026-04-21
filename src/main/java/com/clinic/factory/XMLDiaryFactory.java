package com.clinic.factory;

import com.clinic.diary.Diary;
import com.clinic.storage.XMLDiaryImplementation;

public class XMLDiaryFactory extends DiaryFactory {

    private final String filePath;

    public XMLDiaryFactory(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Diary createDiary() {
        return new Diary(new XMLDiaryImplementation(filePath));
    }

    @Override
    public String toString() { return "XML Factory"; }
}

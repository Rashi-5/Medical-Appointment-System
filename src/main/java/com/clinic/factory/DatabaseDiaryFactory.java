package com.clinic.factory;

import com.clinic.diary.Diary;
import com.clinic.storage.DatabaseDiaryImplementation;

public class DatabaseDiaryFactory extends DiaryFactory {

    private final String connectionString;

    public DatabaseDiaryFactory(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public Diary createDiary() {
        return new Diary(new DatabaseDiaryImplementation(connectionString));
    }

    @Override
    public String toString() { return "Database Factory"; }
}

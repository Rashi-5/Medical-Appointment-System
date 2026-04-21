package com.clinic.factory;

import com.clinic.diary.Diary;

/**
 * Abstract Factory (Factory Method pattern).
 * Each concrete subclass decides which DiaryImplementation to create,
 * hiding the storage type from the client application.
 */
public abstract class DiaryFactory {
    public abstract Diary createDiary();
}

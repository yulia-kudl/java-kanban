package ru.yandex.kanban;

import com.google.gson.annotations.SerializedName;

public enum TaskStatus {
    @SerializedName("NEW")
    NEW,
    @SerializedName("IN_PROGRESS")
    IN_PROGRESS,
    @SerializedName("DONE")
    DONE
}

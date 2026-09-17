package com.edusphere.service;

import java.time.Duration;
import java.time.LocalDateTime;

public interface EditWindow {
     static final int EDIT_WINDOW_HOURS = 24;
    default boolean isWithinEditWindow(LocalDateTime createdAt) {
        return Duration.between(createdAt, LocalDateTime.now()).toHours() < EDIT_WINDOW_HOURS;
    }
}

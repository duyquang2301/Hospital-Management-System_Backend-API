package com.wannabe.app.main.utility;

public class ConverToLong {

    // Helper method to handle the conversion
    public static Long convertUserId(Long userId) {
        return (userId != null) ? userId : -1L;
    }
}

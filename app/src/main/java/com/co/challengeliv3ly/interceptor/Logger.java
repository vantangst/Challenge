package com.co.challengeliv3ly.interceptor;

import okhttp3.internal.platform.Platform;

public interface Logger {
    enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    Logger DEFAULT = new DefaultLogger();

    Logger PLATFORM = new Logger() {
        @Override
        public void log(int level, String tag, String message, boolean useLogHack) {
            Platform.get().log(level, message, null);
        }
    };

    void log(int level, String tag, String message, boolean useLogHack);

    class DefaultLogger implements Logger {
        private static String[] prefix = new String[]{". ", " ."};
        private static int index = 0;

        private static String getFinalTag(String tag, boolean isLogHackEnable) {
            if (isLogHackEnable) {
                index ^= 1;
                return prefix[index] + tag;
            } else {
                return tag;
            }
        }

        @Override
        public void log(int level, String tag, String message, boolean useLogHack) {
            String finalTag = getFinalTag(tag, useLogHack);

            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(useLogHack ? finalTag : tag);
            switch (level) {
                case 4:
                    logger.log(java.util.logging.Level.INFO, message);
                    break;
                default:
                    logger.log(java.util.logging.Level.WARNING, message);
            }
        }
    }
}

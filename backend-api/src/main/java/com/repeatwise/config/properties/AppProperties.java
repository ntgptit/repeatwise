package com.repeatwise.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private String version;
    private String description;
    private final Srs srs = new Srs();
    private final Security security = new Security();
    private final Limits limits = new Limits();
    private final Storage storage = new Storage();

    @Getter
    @Setter
    public static class Srs {
        private int defaultTotalBoxes;
        private String defaultReviewOrder;
        private boolean defaultNotificationEnabled;
        private String defaultNotificationTime;
        private String defaultForgottenCardAction;
        private int defaultMoveDownBoxes;
        private int defaultNewCardsPerDay;
        private int defaultMaxReviewsPerDay;
    }

    @Getter
    @Setter
    public static class Security {
        private int bcryptStrength;
        private int maxLoginAttempts;
        private int lockoutDurationMinutes;
    }

    @Getter
    @Setter
    public static class Limits {
        private int maxFolderDepth;
        private int maxFoldersPerUser;
        private int maxCardsPerDeck;
        private int maxImportRows;
        private int maxFileSizeMb;
        private int importSyncThreshold;
        private int importBatchSize;
        private int exportSyncThreshold;
        private int maxExportRows;
        private int exportBatchSize;
        private int asyncJobTimeoutMinutes;
    }

    @Getter
    @Setter
    public static class Storage {
        private String basePath;
        private String importUploadsDir;
        private String importErrorDir;
        private String exportDir;
    }
}

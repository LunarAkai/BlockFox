package de.lunarakai.blockfox.common;

public enum BlockFoxDisplayMode {
    RSS_FEED ("rss"),
    FIXED_RSS_FEED ("fixed_rss"),
    FEDIVERSE_CLIENT ("fediverse"),
    FIXED_FEDIVERSE_CLIENT ("fixed_fediverse");

    private final String name;

    BlockFoxDisplayMode(String mode) {
        name = mode;
    }

    public String getModeName() {
        return this.name;
    }
 }

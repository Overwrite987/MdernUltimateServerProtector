package ru.overwrite.protect.bukkit.configuration.data;

public record SystemMessages(
        String baselineWarn,
        String baselineDefault,
        String bungeecord1,
        String bungeecord2,
        String bungeecord3,
        String updateLatest,
        String updateSuccess1,
        String updateSuccess2,
        String updateOutdated1,
        String updateOutdated2,
        String updateOutdated3
) {
}

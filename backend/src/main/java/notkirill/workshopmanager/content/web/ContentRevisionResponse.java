package notkirill.workshopmanager.content.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record ContentRevisionResponse(
        @JsonProperty("revision") long revision,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("file_size") long fileSize,
        @JsonProperty("sha256") String sha256,
        @JsonProperty("source_version") String sourceVersion,
        @JsonProperty("uploaded") boolean uploaded,
        @JsonProperty("time_created") OffsetDateTime timeCreated
) {
}

package notkirill.workshopmanager.content.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentUploadResponse(
        @JsonProperty("upload_id") long uploadId,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("size") long size,
        @JsonProperty("received") long received,
        @JsonProperty("chunk_size") long chunkSize,
        @JsonProperty("revision") ContentRevisionResponse revision
) {
}

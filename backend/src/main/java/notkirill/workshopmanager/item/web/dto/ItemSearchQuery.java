package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemSearchQuery(
        @JsonProperty("search_query") String searchQuery,
        @JsonProperty("page") Integer page
) {
    public ItemSearchQuery {
        if (page == null || page < 0) {
            page = 0;
        }
    }
}

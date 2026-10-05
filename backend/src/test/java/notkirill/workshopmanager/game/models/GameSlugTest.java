package notkirill.workshopmanager.game.models;

import notkirill.workshopmanager.common.Texts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameSlugTest {
    @Test
    void buildsUrlFriendlySlugs() {
        assertThat(Texts.toSlug("Garry's Mod")).isEqualTo("garry-s-mod");
        assertThat(Texts.toSlug("  Pokémon: Legends  ")).isEqualTo("pokemon-legends");
        assertThat(Texts.toSlug("Counter-Strike 2")).isEqualTo("counter-strike-2");
        assertThat(Texts.toSlug("東方")).isEqualTo("game");
    }

    @Test
    void followsRenames() {
        GameMetadata game = new GameMetadata("Steam App 4000");
        game.setName("Garry's Mod");
        assertThat(game.getSlug()).isEqualTo("garry-s-mod");
    }
}

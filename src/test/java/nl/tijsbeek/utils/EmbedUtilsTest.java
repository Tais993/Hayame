package nl.tijsbeek.utils;

import net.dv8tion.jda.api.entities.Member;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmbedUtilsTest {
    private final String name = "name";
    private final long id = 1231L;
    private final String avatarUrl = "https://test";

    @Test
    void createBuilder() {
        Member member = mock(Member.class);

        when(member.getEffectiveName()).thenReturn(name);
        when(member.getIdLong()).thenReturn(id);
        when(member.getEffectiveAvatarUrl()).thenReturn(avatarUrl);

        EmbedUtils.createBuilder(member);
    }
}
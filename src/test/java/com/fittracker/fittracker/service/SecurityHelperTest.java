package com.fittracker.fittracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fittracker.fittracker.security.SecurityHelper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class SecurityHelperTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Test
    void getUserId_givenUserIdInSecurityContext_shouldReturnUserId() {
        when(authentication.getPrincipal()).thenReturn(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        var result = SecurityHelper.getUserId();

        assertThat(result).isEqualTo(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"));
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }
}

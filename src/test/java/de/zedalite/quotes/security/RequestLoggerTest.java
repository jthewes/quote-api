package de.zedalite.quotes.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.zedalite.quotes.TestEnvironmentProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
@WithMockUser(roles = "MEMBER")
@ExtendWith(OutputCaptureExtension.class)
class RequestLoggerTest extends TestEnvironmentProvider {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should log request")
  void shouldLogRequest(final CapturedOutput output) throws Exception {
    mockMvc.perform(get("/quotes/count")).andExpect(status().isOk());

    assertThat(output).contains(
      "RequestLogger",
      "request",
      "GET /quotes/count",
      "status=200",
      "client",
      "user",
      "duration"
    );
  }
}

package de.zedalite.quotes.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.CountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
@WithMockUser(roles = "MEMBER")
class QuoteControllerIT extends TestEnvironmentProvider {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should return success on valid input")
  void shouldReturnSuccessOnValidInput() throws Exception {
    final MvcResult result = mockMvc.perform(get("/quotes/count")).andExpect(status().isOk()).andReturn();

    final CountResponse count = objectMapper.readValue(result.getResponse().getContentAsString(), CountResponse.class);

    assertThat(count.count()).isNotNegative();
  }
}

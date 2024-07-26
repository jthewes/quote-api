package de.zedalite.quotes.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zedalite.quotes.TestEnvironmentProvider;
import de.zedalite.quotes.data.model.User;
import de.zedalite.quotes.data.model.UserRequest;
import de.zedalite.quotes.fixtures.UserGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
@WithMockUser(roles = "GUEST")
class UserControllerIT extends TestEnvironmentProvider {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should return OK")
  void shouldReturnOk() throws Exception {
    final UserRequest userRequest = UserGenerator.getUserRequest();

    final String responseJson = mockMvc
      .perform(post("/users").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    final String responseName = objectMapper.readValue(responseJson, User.class).name();

    assertThat(responseName).contains(userRequest.name());
  }

  @Test
  @DisplayName("Should return BAD_REQUEST")
  void shouldReturnBadRequest() throws Exception {
    final UserRequest userRequest = new UserRequest("a".repeat(128), "AAA");

    mockMvc
      .perform(post("/users").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)))
      .andExpect(status().isBadRequest())
      .andReturn();
  }
}

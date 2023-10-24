package com.example.demo;

import com.example.demo.controller.CsvController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoApplicationTests {
    private final String API_BASE_URL = "/";

    private final String API_UPLOAD_URL = API_BASE_URL + "upload";

    private final String TEST_FILENAME = "./exercise.csv";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CsvController csvController;

    @Test
    @Order(1)
    @DisplayName("Context loads")
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(csvController).isNotNull();
        log.info("Context loaded");
    }

    @Test
    @Order(2)
    @DisplayName("Get all")
    void getAll() throws Exception {
        // expect no data at this point

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("Upload from file")
    void upload() throws Exception {
        // expect data to be uploaded correctly

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_UPLOAD_URL)
                        .file(new MockMultipartFile("file", "file.csv", "", getClass().getClassLoader().getResourceAsStream(TEST_FILENAME))))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(4)
    @DisplayName("Upload from file, duplicated data")
    void uploadDuplicatedData() throws Exception {
        // expect error because of duplicated data

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_UPLOAD_URL)
                        .file(new MockMultipartFile("file", "file.csv", "", getClass().getClassLoader().getResourceAsStream(TEST_FILENAME))))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(5)
    @DisplayName("Get all with data present")
    void getAllWithData() throws Exception {
        // expect previously uploaded data to be returned

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isNotEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Get all with data present, filtered")
    void getAllWithDataFiltered() throws Exception {
        // expect previously uploaded data to be returned (filtered)

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE_URL)
                        .queryParam("code", "Type 1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*]", hasSize(1)));
    }

    @Test
    @Order(7)
    @DisplayName("Get all with data present, paginated")
    void getAllWithDataPaginated() throws Exception {
        // expect previously uploaded data to be returned (paginated)

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE_URL)
                        .queryParam("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*]", hasSize(1)));
    }

    @Test
    @Order(8)
    @DisplayName("Delete all data")
    void deleteAllWithData() throws Exception {
        // expect to delete all the data
        mockMvc.perform(MockMvcRequestBuilders.delete(API_BASE_URL))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(9)
    @DisplayName("Retrieve data after deletion")
    void getAllAfterDeletion() throws Exception {
        // expect to find no data because has been deleted

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }
}
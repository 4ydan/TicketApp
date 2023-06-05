package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.PictureFileManager;
import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MessageEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private PictureFileManager pictureFileManager;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    private Message message;

    private final Picture picture = Picture.builder().name("test1.jpg").build();

    @BeforeEach
    public void beforeEach() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
        message = Message.builder()
            .title(TEST_NEWS_TITLE)
            .shortDescription(TEST_NEWS_SHORT_DESCRIPTION)
            .description(TEST_NEWS_DESCRIPTION)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .picture(picture)
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?page=0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<MessageDto> simpleMessageDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            MessageDto[].class));

        assertEquals(0, simpleMessageDtos.size());
    }

    @Test
    public void givenOneMessage_whenFindAll_thenListWithSizeOneAndMessageWithAllProperties()
        throws Exception {
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?page=0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<MessageDto> simpleMessageDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            MessageDto[].class));

        assertEquals(1, simpleMessageDtos.size());
        MessageDto messageDto = simpleMessageDtos.get(0);
        assertAll(
            () -> assertEquals(message.getId(), messageDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, messageDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SHORT_DESCRIPTION, messageDto.getShortDescription()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, messageDto.getPublishedAt()),
            () -> assertArrayEquals(pictureFileManager.getPictureByName(TEST_NEWS_PICTURE_NAME), messageDto.getPicture())
        );
    }

    @Test
    public void givenOneMessage_whenFindById_thenMessageWithAllProperties() throws Exception {
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        DetailedMessageDto detailedMessageDto = objectMapper.readValue(response.getContentAsString(),
            DetailedMessageDto.class);

        assertAll(
            () -> assertEquals(message.getId(), detailedMessageDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, detailedMessageDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SHORT_DESCRIPTION, detailedMessageDto.getShortDescription()),
            () -> assertEquals(TEST_NEWS_DESCRIPTION, detailedMessageDto.getDescription()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, detailedMessageDto.getPublishedAt()),
            () -> assertArrayEquals(pictureFileManager.getPictureByName(TEST_NEWS_PICTURE_NAME), detailedMessageDto.getPicture())
        );
    }

    @Test
    public void givenOneMessage_whenFindByIdUserRole_then200AndReadSize1AndUnreadSize0() throws Exception {
        userRepository.save(ApplicationUser.builder()
            .firstName(TEST_APPLICATION_USER_FIRST_NAME)
            .lastName(TEST_APPLICATION_USER_LAST_NAME)
            .role(UserRole.USER)
            .email(TEST_APPLICATION_USER_EMAIL)
            .password(TEST_APPLICATION_USER_PASSWORD)
            .failedLogins(TEST_APPLICATION_USER_FAILED_LOGINS)
            .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
            .isNotLocked(TEST_APPLICATION_USER_IS_NOT_LOCKED)
            .build());
        messageRepository.save(message);

        this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", message.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)));

        MvcResult mvcResult1 = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?page=0&read=true")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response1 = mvcResult1.getResponse();
        assertEquals(HttpStatus.OK.value(), response1.getStatus());

        List<MessageDto> simpleMessageDtos1 = Arrays.asList(objectMapper.readValue(response1.getContentAsString(),
            MessageDto[].class));
        assertEquals(1, simpleMessageDtos1.size());

        MvcResult mvcResult2 = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?page=0&read=false")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response2 = mvcResult2.getResponse();
        assertEquals(HttpStatus.OK.value(), response2.getStatus());

        List<MessageDto> simpleMessageDtos2 = Arrays.asList(objectMapper.readValue(response2.getContentAsString(),
            MessageDto[].class));
        assertEquals(0, simpleMessageDtos2.size());

    }


    @Test
    public void givenOneMessage_whenFindByNonExistingId_then404() throws Exception {
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/{id}", -1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneMessage_whenDeleteById_than204() throws Exception {
        saveTestPictureInFileSystem();
        message.setPicture(Picture.builder().name("test2.jpg").build());
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(delete(MESSAGE_BASE_URI + "/{id}", message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals(0, messageRepository.findAll().size());
    }

    @Test
    public void nothingGiven_whenDeleteById_than404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(MESSAGE_BASE_URI + "/{id}", -1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    private void saveTestPictureInFileSystem() throws Exception {
        File file = new PathResource(FOLDER_PATH + TEST_NEWS_PICTURE_NAME).getFile();
        try (FileOutputStream fos = new FileOutputStream(FOLDER_PATH + "test2.jpg")) {
            fos.write(Files.readAllBytes(file.toPath()));
        }
    }

    @Test
    public void creatingNewMessage_returns200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.multipart(MESSAGE_BASE_URI)
                .file(TEST_MESSAGE)
                .param("title", "TestMessageTitle")
                .param("shortDescription", "TestMessageShortDescription")
                .param("description", "TestMessageDescription")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void creatingNewMessageWithEmptyStrings_returns422() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.multipart(MESSAGE_BASE_URI)
                .file(TEST_MESSAGE)
                .param("title", "")
                .param("shortDescription", "")
                .param("description", "")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenExistingMessage_thenEditingThatMessage_returns400AndEditedMessage() throws Exception {
        saveTestPictureInFileSystem();
        message.setPicture(Picture.builder().name("test2.jpg").build());
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(put(MESSAGE_BASE_URI + "/" + this.message.getId())
                .param("title", "TestUpdateMessageTitle")
                .param("shortDescription", "TestUpdateMessageShortDescription")
                .param("description", "TestUpdateMessageDescription")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertThat(response.getContentAsString().contains("TestUpdateMessageTitle")).isTrue();
        assertThat(response.getContentAsString().contains("TestUpdateMessageShortDescription")).isTrue();
        assertThat(response.getContentAsString().contains("TestUpdateMessageDescription")).isTrue();
    }

    @Test
    public void givenExistingMessage_thenEditingThatMessageWithEmptyStrings_returns422() throws Exception {
        saveTestPictureInFileSystem();
        message.setPicture(Picture.builder().name("test2.jpg").build());
        messageRepository.save(message);

        MvcResult mvcResult = this.mockMvc.perform(put(MESSAGE_BASE_URI + "/" + this.message.getId())
                .param("title", "")
                .param("shortDescription", "")
                .param("description", "")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void editingNonExistentMessage_returns404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(put(MESSAGE_BASE_URI + "/10")
                .param("title", "TestUpdateMessageTitle")
                .param("shortDescription", "TestUpdateMessageShortDescription")
                .param("description", "TestUpdateMessageDescription")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}

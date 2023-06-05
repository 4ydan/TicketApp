package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.PictureFileManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class MessageMappingTest implements TestData {
    private final Picture picture = Picture.builder().name("test1.jpg").build();

    private final Message message = Message.builder()
        .id(ID)
        .title(TEST_NEWS_TITLE)
        .shortDescription(TEST_NEWS_SHORT_DESCRIPTION)
        .description(TEST_NEWS_DESCRIPTION)
        .publishedAt(TEST_NEWS_PUBLISHED_AT)
        .picture(picture)
        .build();
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private PictureFileManager pictureFileManager;

    @Test
    public void givenNothing_whenMapDetailedMessageDtoToEntity_thenEntityHasAllProperties() {
        DetailedMessageDto detailedMessageDto = messageMapper.messageToDetailedMessageDto(message);
        assertAll(
            () -> assertEquals(ID, detailedMessageDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, detailedMessageDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SHORT_DESCRIPTION, detailedMessageDto.getShortDescription()),
            () -> assertEquals(TEST_NEWS_DESCRIPTION, detailedMessageDto.getDescription()),
            () -> assertArrayEquals(pictureFileManager.getPictureByName(TEST_NEWS_PICTURE_NAME), detailedMessageDto.getPicture()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, detailedMessageDto.getPublishedAt())
        );
    }

    @Test
    public void givenNothing_whenMapListWithTwoMessageEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);

        List<MessageDto> simpleMessageDtos = messages.stream().map(message -> messageMapper.messageToMessageDto(message)).toList();
        assertEquals(2, simpleMessageDtos.size());
        MessageDto messageDto = simpleMessageDtos.get(0);
        assertAll(
            () -> assertEquals(ID, messageDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, messageDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SHORT_DESCRIPTION, messageDto.getShortDescription()),
            () -> assertArrayEquals(pictureFileManager.getPictureByName(TEST_NEWS_PICTURE_NAME), messageDto.getPicture()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, messageDto.getPublishedAt())
        );
    }
}

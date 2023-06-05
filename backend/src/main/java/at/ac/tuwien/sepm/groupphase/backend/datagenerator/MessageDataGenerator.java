package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

import static at.ac.tuwien.sepm.groupphase.backend.datagenerator.ImageBytesConverter.getImageBytes;

@Profile("generateData")
@Component
public class MessageDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 50;
    private static final String[] IMAGE_URIS = {"https://i.ibb.co/GTwndrT/img-test1.jpg",
        "https://i.ibb.co/68qvzB6/img-test2.jpg", "https://i.ibb.co/QHZZKcY/img-test3.jpg", "https://i.ibb.co/yh7HLrH/img-test4.jpg",
        "https://i.ibb.co/kqpc0sj/img-test5.jpg", "https://i.ibb.co/7VmL5p9/img-test6.jpg", "https://i.ibb.co/G54b2wj/test-img7.jpg",
        "https://i.ibb.co/ns5VCxk/test-img8.jpg", "https://i.ibb.co/NFYzvLn/test-img9.jpg", "https://i.ibb.co/yBTpvbS/test-img10.jpg"};

    private final MessageRepository messageRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final Faker FAKER = new Faker();

    private static final String FOLDER_PATH = new File("src/main/resources/pictures/").getAbsolutePath() + "/";

    public MessageDataGenerator(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void generateMessage() throws IOException {
        if (messageRepository.findAll().size() > 0) {
            LOGGER.debug("message already generated");
        } else {
            LOGGER.debug("generating {} message entries", NUMBER_OF_MESSAGES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
                savePictureInFileSystem(IMAGE_URIS[i % 10], i);
                Picture picture = Picture.builder().name("test_message_img" + i + ".jpg").build();

                Message message = Message.builder()
                    .title(FAKER.lorem().maxLengthSentence(60))
                    .shortDescription(FAKER.lorem().maxLengthSentence(300))
                    .description(FAKER.lorem().maxLengthSentence(1000))
                    .publishedAt(LocalDateTime.now().minusMonths(i))
                    .picture(picture)
                    .build();

                entityManager.persist(message);
            }
        }
    }

    public void setManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private void savePictureInFileSystem(String uri, int i) throws IOException {
        byte[] imageBytes = getImageBytes(uri);
        File folder = new PathResource(FOLDER_PATH).getFile();
        try (FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath() + "/test_message_img" + i + ".jpg")) {
            fos.write(imageBytes);
        }
    }
}


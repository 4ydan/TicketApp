package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
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

import static at.ac.tuwien.sepm.groupphase.backend.datagenerator.ImageBytesConverter.getImageBytes;

@Profile("generateData")
@Component
public class MerchandiseDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int NUMBER_OF_MERCHANDISES_TO_GENERATE = 50;

    static final String[] IMAGE_URIS = {"https://i.ibb.co/88qctTx/test-m-img1.jpg",
        "https://i.ibb.co/64ZmyHB/test-m-img2.jpg", "https://i.ibb.co/q1NXgRM/test-m-img3.jpg", "https://i.ibb.co/4pbHdGG/test-m-img4.jpg",
        "https://i.ibb.co/fYzRy7C/test-m-img5.jpg", "https://i.ibb.co/Jrn1bNp/test-m-img6.jpg", "https://i.ibb.co/zfsPv9b/test-m-img7.jpg",
        "https://i.ibb.co/ww96L86/test-m-img8.jpg", "https://i.ibb.co/R3GL1cp/test-m-img9.jpg", "https://i.ibb.co/8DGh73d/test-m-img10.jpg"};

    private final MerchandiseRepository merchandiseRepository;

    @PersistenceContext
    EntityManager entityManager;

    private static final Faker FAKER = new Faker();

    private static final String FOLDER_PATH = new File("src/main/resources/pictures/").getAbsolutePath() + "/";

    public MerchandiseDataGenerator(MerchandiseRepository merchandiseRepository) {
        this.merchandiseRepository = merchandiseRepository;
    }

    public void setManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void generateMerchandises() throws IOException {
        if (merchandiseRepository.findAll().size() > 0) {
            LOGGER.debug("merchandises already generated");
        } else {
            LOGGER.debug("generating {} merchandise entries", NUMBER_OF_MERCHANDISES_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_MERCHANDISES_TO_GENERATE; i++) {
                savePictureInFileSystem(IMAGE_URIS[i % 10], i);
                Picture picture = Picture.builder().name("test_merchandise_img" + i + ".jpg").build();

                Merchandise merchandise = Merchandise.builder()
                    .name(FAKER.lorem().maxLengthSentence(50))
                    .inStock(FAKER.random().nextInt(1, 500))
                    .price(FAKER.random().nextInt(1, 100).doubleValue())
                    .picture(picture)
                    .costsPoints(FAKER.random().nextInt(0, 1) == 0 ? FAKER.random().nextInt(1, 100) : null)
                    .build();

                entityManager.persist(merchandise);
            }
        }
    }

    private void savePictureInFileSystem(String uri, int i) throws IOException {
        byte[] imageBytes = getImageBytes(uri);
        File folder = new PathResource(FOLDER_PATH).getFile();
        try (FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath() + "/test_merchandise_img" + i + ".jpg")) {
            fos.write(imageBytes);
        }
    }
}

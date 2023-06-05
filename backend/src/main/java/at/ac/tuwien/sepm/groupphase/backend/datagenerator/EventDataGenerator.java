package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Profile("generateData")
@Component
public class EventDataGenerator {
    private static final int NUMBER_OF_EVENTS_TO_GENERATE = 200;
    private static final Faker FAKER = new Faker();
    private static final EventCategory[] CATEGORIES = new EventCategory[]{EventCategory.CINEMA, EventCategory.OPERA, EventCategory.THEATRE, EventCategory.CONCERT};
    private final EventRepository eventRepository;

    private final List<String> testDescriptions = new ArrayList<>();

    private static final String[] IMAGE_URIS = {"https://i.ibb.co/GTwndrT/img-test1.jpg",
        "https://i.ibb.co/68qvzB6/img-test2.jpg", "https://i.ibb.co/QHZZKcY/img-test3.jpg", "https://i.ibb.co/yh7HLrH/img-test4.jpg",
        "https://i.ibb.co/kqpc0sj/img-test5.jpg", "https://i.ibb.co/7VmL5p9/img-test6.jpg", "https://i.ibb.co/G54b2wj/test-img7.jpg",
        "https://i.ibb.co/ns5VCxk/test-img8.jpg", "https://i.ibb.co/NFYzvLn/test-img9.jpg", "https://i.ibb.co/yBTpvbS/test-img10.jpg"};

    private static final String FOLDER_PATH = new File("src/main/resources/pictures/").getAbsolutePath() + "/";
    private final ArtistRepository artistRepository;

    public EventDataGenerator(EventRepository eventRepository, ArtistRepository artistRepository) {
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        testDescriptions.add("25 Years of Hits: Robbie Williams geht mit seinem Jubiläumsalbum \"XXV\" auf Tour.");
        testDescriptions.add("Sabaton gehen gemeinsam mit Babymetal und Lordi auf \"The Tour To End All Tours\"");
        testDescriptions.add("Set on a Greek island paradise, a story of love, friendship and identity.");
        testDescriptions.add("The smash-hit musical is back, and the party is gonna be BIGGER than ever!");
        testDescriptions.add("After an exciting few years making and touring shows all over the world.");
        testDescriptions.add("Once Upon a Time is a fairy tale like no other. It is the brand-new dance show.");
        testDescriptions.add("IT’S THE SHOW THAT YOU WANT! Danny & Sandy have a summer full of lovin’ on the beach but after.");
        testDescriptions.add("Following two smash hit West End seasons, a record-breaking run and winning the WhatsOnStage.");
        testDescriptions.add("tar of the UK’s most streamed Netflix comedy special of 2021, Jimmy is back on ");
        testDescriptions.add("Penn & Teller are celebrating their 45th Anniversary as ‘The Kings Of Magic’ with ‘The First Final UK Tour'.");
        testDescriptions.add("Russell Howard returns in 2023 with a brand-new live tour!");
        testDescriptions.add("The sexy baby from Taskmaster is all grown up. Back in 2019 she was a Babybel – round, sweet, pure.");
        testDescriptions.add("Portsmouth Comic Con – International Festival of Comics returns bringing you the best in Comics.");
        testDescriptions.add("2022 Leicester Comedy Festival best show nominee 2020 Leicester Comedy ");
        testDescriptions.add("Join record breaking legendary football manager Neil Warnock as he talks you.");
        testDescriptions.add("An Evening of Burlesque Presents Tassels.");
        testDescriptions.add("The new pantomime from Curtain Call Productions.");
        testDescriptions.add("Introduce children to storytelling, dance and shadow puppetry with Apple ‘N’ Spice, an original");
        testDescriptions.add("A showcase spotlighting selected 10 minute plays");
        testDescriptions.add("Whitney - Queen of the Night is a stunning celebration of the music.");
        testDescriptions.add("IT’S THE SHOW THAT YOU WANT! Danny & Sandy have a summer full of lovin’.");
        testDescriptions.add("SpongeBob and friends are coming to Blackpool in a brilliant, bright, hilarious and brand-new.");
        testDescriptions.add("Set in 1930s New York during The Great Depression, brave young Annie is forced to live a life of misery.");
    }

    @PostConstruct
    public void generateEvents() throws IOException {
        this.generateArtists();

        if (eventRepository.findAll().size() > 0) {
            log.debug("events already generated");
        } else {
            log.debug("generating {} event entries", NUMBER_OF_EVENTS_TO_GENERATE);
            List<Artist> artistList = artistRepository.findAll();
            log.debug("Unique number of artists: {}", artistList.size());
            ArrayList<Event> eventList = new ArrayList<>();
            for (int i = 0; i < NUMBER_OF_EVENTS_TO_GENERATE; i++) {
                savePictureInFileSystem(IMAGE_URIS[i % 10], i);
                Picture picture = Picture.builder().name("test_event_img" + i + ".jpg").build();
                int daysAwayFromToday = FAKER.random().nextInt(1, 180);
                Artist tempArtist = artistList.get(FAKER.random().nextInt(artistList.size() - 1));
                Event event = new Event();
                event.setTitle(tempArtist.getName());
                event.setImage(picture);
                event.setDescription(testDescriptions.get(FAKER.random().nextInt(testDescriptions.size() - 1)));
                event.setDurationInMinutes(FAKER.random().nextInt(120, 450));
                event.setStartDate(LocalDate.parse(
                    FAKER.date().future(daysAwayFromToday, TimeUnit.DAYS, "d-MMM-yyyy"),
                    DateTimeFormatter.ofPattern("d-MMM-yyyy"))
                );
                event.setEndDate(LocalDate.parse(
                    FAKER.date().future(daysAwayFromToday + 1, daysAwayFromToday, TimeUnit.DAYS, "d-MMM-yyyy"),
                    DateTimeFormatter.ofPattern("d-MMM-yyyy"))
                );
                event.setEventCategory(CATEGORIES[FAKER.random().nextInt(0, 3)]);
                event.setArtist(tempArtist);
                eventList.add(event);
            }
            log.debug("saving new events");
            eventRepository.saveAll(eventList);
        }
    }

    private void generateArtists() {
        if (artistRepository.findAll().size() > 0) {
            log.debug("artists already generated");
        } else {
            log.debug("generating {} artist entries", NUMBER_OF_EVENTS_TO_GENERATE);
            Set<Artist> artistList = new HashSet<>();
            for (int i = 0; i < NUMBER_OF_EVENTS_TO_GENERATE; i++) {
                Artist artist = new Artist();
                artist.setName(FAKER.rockBand().name());
                artistList.add(artist);
            }
            log.debug("saving new artists");
            artistRepository.saveAll(artistList);
        }
    }

    private void savePictureInFileSystem(String uri, int i) throws IOException {
        byte[] imageBytes = getImageBytes(uri);
        File folder = new PathResource(FOLDER_PATH).getFile();
        try (FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath() + "/test_event_img" + i + ".jpg")) {
            fos.write(imageBytes);
        }
    }

    private static byte[] getImageBytes(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream stream = url.openStream()) {
            byte[] buffer = new byte[4096];

            while (true) {
                int bytesRead = stream.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                output.write(buffer, 0, bytesRead);
            }
        }
        return output.toByteArray();
    }
}

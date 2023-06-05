package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.BackendApplication;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandisePurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Security is a cross-cutting concern, however for the sake of simplicity it is tested against the message endpoint
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityTest implements TestData {

    private static final List<Class<?>> mappingAnnotations = Lists.list(
        RequestMapping.class,
        GetMapping.class,
        PostMapping.class,
        PutMapping.class,
        PatchMapping.class,
        DeleteMapping.class
    );

    private static final List<Class<?>> securityAnnotations = Lists.list(
        Secured.class,
        PreAuthorize.class,
        RolesAllowed.class,
        PermitAll.class,
        DenyAll.class,
        DeclareRoles.class
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private List<Object> components;

    private Message message;

    private Merchandise merchandise;

    private final Picture picture = Picture.builder().name("test1.jpg").build();

    @BeforeEach
    public void beforeEach() {
        messageRepository.deleteAll();
        message = Message.builder()
            .title(TEST_NEWS_TITLE)
            .shortDescription(TEST_NEWS_SHORT_DESCRIPTION)
            .description(TEST_NEWS_DESCRIPTION)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .picture(picture)
            .build();
        merchandiseRepository.deleteAll();
        merchandise = Merchandise.builder()
            .name(TEST_MERCHANDISE_NAME)
            .price(TEST_MERCHANDISE_PRICE)
            .inStock(TEST_MERCHANDISE_IN_STOCK)
            .costsPoints(TEST_MERCHANDISE_COSTS_POINTS)
            .picture(picture)
            .build();
    }

    /**
     * This ensures every Rest Method is secured with Method Security.
     * It is very easy to forget securing one method causing a security vulnerability.
     * Feel free to remove / disable / adapt if you do not use Method Security (e.g. if you prefer Web Security to define who may perform which actions) or want to use Method Security on the service layer.
     */
    @Test
    public void ensureSecurityAnnotationPresentForEveryEndpoint() {
        List<ImmutablePair<Class<?>, Method>> notSecured = components.stream()
            .map(AopUtils::getTargetClass) // beans may be proxies, get the target class instead
            .filter(clazz -> clazz.getCanonicalName() != null &&
                clazz.getCanonicalName().startsWith(BackendApplication.class.getPackageName())) // limit to our package
            .filter(clazz -> clazz.getAnnotation(RestController.class) != null) // limit to classes annotated with @RestController
            .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods())
                .map(method -> new ImmutablePair<Class<?>, Method>(clazz, method))) // get all class -> method pairs
            .filter(pair -> Arrays.stream(pair.getRight().getAnnotations()).anyMatch(
                annotation -> mappingAnnotations.contains(annotation.annotationType()))) // keep only the pairs where the method has a "mapping annotation"
            .filter(pair -> Arrays.stream(pair.getRight().getAnnotations()).noneMatch(annotation -> securityAnnotations.contains(
                annotation.annotationType()))) // keep only the pairs where the method does not have a "security annotation"
            .toList();

        assertThat(notSecured.size())
            .as("Most rest methods should be secured. If one is really intended for public use, explicitly state that with @PermitAll. "
                + "The following are missing: \n" +
                notSecured.stream().map(pair -> "Class: " + pair.getLeft() + " Method: " + pair.getRight()).reduce("", (a, b) -> a + "\n" + b))
            .isZero();

    }


    @Test
    public void givenNoOneLoggedIn_whenFindAllMessages_then200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?page=0"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    public void givenNoOneLoggedIn_whenFindAllReadOrUnread_then401() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?page=0&read=true"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }

    @Test
    public void givenNoOneLoggedIn_whenFindMessageById_then200() throws Exception {
        messageRepository.save(message);
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "/" + message.getId()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    public void givenNoOneLoggedIn_whenFindAllMerchandises_then200() throws Exception {
        merchandiseRepository.save(merchandise);
        MvcResult mvcResult = this.mockMvc.perform(get(MERCHANDISE_BASE_URI + "?page=0"))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    public void givenNoOneLoggedIn_whenPurchase_then401() throws Exception {
        MerchandisePurchaseDto merchandisePurchaseDto = new MerchandisePurchaseDto(merchandise.getId(), TEST_MERCHANDISE_INVALID_AMOUNT);
        List<MerchandisePurchaseDto> merchandisePurchaseDtos = new ArrayList<>();
        merchandisePurchaseDtos.add(merchandisePurchaseDto);
        PurchaseDto purchaseDto = new PurchaseDto();
        purchaseDto.setMerchandises(merchandisePurchaseDtos);

        MvcResult mvcResult = this.mockMvc.perform(post(PURCHASE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDto)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

}

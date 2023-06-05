package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

    private final MessageService messageService;

    @Autowired
    public MessageEndpoint(MessageService messageService) {
        this.messageService = messageService;
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get list of messages without details concerning the page")
    public List<MessageDto> findAll(@RequestParam(name = "page") int page, @RequestParam(name = "read", required = false) Boolean read) {
        if (read == null) {
            log.info("GET /api/v1/messages?page={}", page);
            return messageService.findAll(page);
        } else {
            log.info("GET /api/v1/messages?read={}&page={}", read, page);
            return messageService.findReadOrUnread(read, page);
        }
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific message")
    public DetailedMessageDto find(@PathVariable Long id) {
        log.info("GET /api/v1/messages/{}", id);
        return messageService.findOne(id);
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a specific message", security = @SecurityRequirement(name = "apiKey"))
    public void deleteById(@PathVariable("id") long id) {
        log.info("DELETE /api/v1/messages/{}", id);
        messageService.deleteById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create a new message", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<DetailedMessageDto> create(@RequestParam("title") String title,
                             @RequestParam("shortDescription") String shortDescription,
                             @RequestParam("description") String description,
                             @RequestParam("picture") MultipartFile picture) {
        CreateMessageDto toCreate = new CreateMessageDto(
            title.trim(),
            shortDescription.trim(),
            description.trim(),
            picture);
        log.info("POST /api/v1/messages/create " + title + description);
        try {
            return new ResponseEntity<>(messageService.create(toCreate), HttpStatus.CREATED);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Edit an existing message", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<DetailedMessageDto> update(@PathVariable Long id,
                                 @RequestParam("title") String title,
                                 @RequestParam("shortDescription") String shortDescription,
                                 @RequestParam("description") String description,
                                 @RequestParam(value = "picture", required = false) MultipartFile picture) {
        UpdateMessageDto toUpdate = new UpdateMessageDto(
            id,
            title.trim(),
            shortDescription.trim(),
            description.trim(),
            picture);
        log.info("PUT /api/v1/messages/{}", toUpdate.getId());
        try {
            return new ResponseEntity<>(messageService.update(toUpdate), HttpStatus.OK);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}

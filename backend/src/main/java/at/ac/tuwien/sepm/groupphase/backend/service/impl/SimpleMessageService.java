package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileManagerException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;

import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SimpleMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    private final MessageValidator messageValidator;

    private final UserRepository userRepository;

    private final PictureFileManager pictureFileManager;

    private static final String anonymousUser = "anonymousUser";

    @PersistenceContext
    EntityManager entityManager;


    public SimpleMessageService(MessageRepository messageRepository,
                                MessageMapper messageMapper,
                                MessageValidator messageValidator,
                                UserRepository userRepository,
                                PictureFileManager pictureFileManager) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.messageValidator = messageValidator;
        this.userRepository = userRepository;
        this.pictureFileManager = pictureFileManager;
    }

    @Override
    public List<MessageDto> findAll(int page) {
        log.debug("Find all messages on page {}", page);
        return messageRepository.findAllByOrderByPublishedAtDesc(page).stream().map(messageMapper::messageToMessageDto).toList();
    }

    @Override
    @Secured("ROLE_USER")
    public List<MessageDto> findReadOrUnread(boolean read, int page) {
        log.debug(read ? "Find read messages on page " + page : "Find unread messages on page " + page);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findUserByEmail(userEmail);
        if (read) {
            return messageRepository.findAllReadMessages(user.getId(), page).stream().map(messageMapper::messageToMessageDto).toList();
        } else {
            return messageRepository.findAllUnreadMessages(user.getId(), page).stream().map(messageMapper::messageToMessageDto).toList();
        }
    }

    @Override
    @Transactional
    public DetailedMessageDto findOne(Long id) {
        log.debug("Find message with id {}", id);
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent()) {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!userEmail.equals(anonymousUser)) {
                ApplicationUser user = userRepository.findUserByEmail(userEmail);
                if (user != null && !(user.getRole() == UserRole.ADMIN)) {
                    List<Message> readMessages = user.getHasRead();
                    if (!readMessages.contains(message.get())) {
                        log.debug("Storing new has read relation");
                        readMessages.add(message.get());
                        user.setHasRead(readMessages);
                    }
                }
            }
            return messageMapper.messageToDetailedMessageDto(message.get());
        } else {
            throw new NotFoundException(String.format("Could not find message with id %s", id));
        }
    }

    @Override
    public void deleteById(long id) {
        log.debug("Delete message with id {}", id);
        Optional<Message> message = messageRepository.findById(id);
        if (message.isEmpty()) {
            throw new NotFoundException(String.format("Could not find message with id %s", id));
        }
        pictureFileManager.deletePictureByName(message.get().getPicture().getName());
        messageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DetailedMessageDto create(CreateMessageDto toCreate) throws ValidationException {
        log.debug("Create message {}", toCreate);

        messageValidator.validateForCreate(toCreate);

        Picture picture = null;
        Message message = Message.builder()
            .title(toCreate.getTitle())
            .shortDescription(toCreate.getShortDescription())
            .description(toCreate.getDescription())
            .publishedAt(LocalDateTime.now())
            .picture(picture)
            .build();

        log.debug("Saving message {}", message);
        entityManager.persist(message);
        entityManager.flush();

        String filename = "message" + message.getId() + ".jpg";
        picture = Picture.builder().name(filename).build();
        message.setPicture(picture);
        try {
            pictureFileManager.savePictureByName(toCreate.getPicture().getBytes(), filename);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileManagerException(e.getMessage(), e);
        }

        entityManager.merge(message);

        return messageMapper.messageToDetailedMessageDto(message);
    }

    @Override
    @Transactional
    public DetailedMessageDto update(UpdateMessageDto toUpdate) throws ValidationException {
        log.trace("Update message {}", toUpdate);

        messageValidator.validateForUpdate(toUpdate);

        Optional<Message> dbMessage = messageRepository.findById(toUpdate.getId());
        if (dbMessage.isEmpty()) {
            throw new NotFoundException(String.format("Could not find message with id %s", toUpdate.getId()));
        }
        Message message = dbMessage.get();

        message.setTitle(toUpdate.getTitle());
        message.setShortDescription(toUpdate.getShortDescription());
        message.setDescription(toUpdate.getDescription());

        if (toUpdate.getPicture() != null) {
            String filename = "message" + message.getId() + ".jpg";
            try {
                pictureFileManager.savePictureByName(toUpdate.getPicture().getBytes(), filename);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new FileManagerException(e.getMessage(), e);
            }
            Picture picture = Picture.builder().name(filename).build();
            message.setPicture(picture);
        }

        entityManager.merge(message);

        return messageMapper.messageToDetailedMessageDto(message);
    }
}

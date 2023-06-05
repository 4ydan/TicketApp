package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.exception.FileManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;

@Service
public class PictureFileManager {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String FOLDER_PATH = new File("src/main/resources/pictures/").getAbsolutePath() + "/";

    public byte[] getPictureByName(String name) {
        log.trace("Convert picture file with name: {} into bytes", name);
        try {
            File file = new PathResource(FOLDER_PATH + name).getFile();
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileManagerException(e.getMessage(), e);
        }
    }

    public void deletePictureByName(String name) {
        log.trace("Delete picture file with name {}", name);
        try {
            File file = new PathResource(FOLDER_PATH + name).getFile();
            Files.delete(file.toPath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileManagerException(e.getMessage(), e);
        }
    }

    public void savePictureByName(byte[] toSave, String name) {
        log.trace("Save picture file {}", name);

        try {
            File folder = new PathResource(FOLDER_PATH).getFile();
            FileOutputStream fos = new FileOutputStream(folder.getAbsolutePath() + "/" + name);
            fos.write(toSave);
            log.debug(folder.getAbsolutePath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileManagerException(e.getMessage(), e);
        }
    }
}

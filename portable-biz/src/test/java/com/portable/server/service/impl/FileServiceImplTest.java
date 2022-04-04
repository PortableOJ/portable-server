package com.portable.server.service.impl;

import com.Ostermiller.util.CircularByteBuffer;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.GridFsManagerImpl;
import com.portable.server.model.fs.FileData;
import com.portable.server.type.FileStoreType;
import com.portable.server.util.StreamUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private GridFsManagerImpl gridFsManager;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void uploadImage() throws PortableException {
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();

        fileService.uploadImage(circularByteBuffer.getInputStream(), "MOCKED_NAME", "MOCKED_TYPE");

        Mockito.verify(gridFsManager).uploadImage(circularByteBuffer.getInputStream(), "MOCKED_NAME", "MOCKED_TYPE");
    }

    @Test
    void get() throws IOException, PortableException {
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write("abc".getBytes());
        circularByteBuffer.getOutputStream().close();

        FileData fileData = FileData.builder()
                .inputStream(circularByteBuffer.getInputStream())
                .contentType("TYPE")
                .build();

        Mockito.when(gridFsManager.get("MOCKED_ID", FileStoreType.IMAGE)).thenReturn(fileData);

        CircularByteBuffer outRetVal = new CircularByteBuffer();

        String retVal = fileService.get("MOCKED_ID", FileStoreType.IMAGE, outRetVal.getOutputStream());
        outRetVal.getOutputStream().close();

        Assertions.assertEquals("TYPE", retVal);
        Assertions.assertEquals("abc", StreamUtils.read(outRetVal.getInputStream()));
    }
}

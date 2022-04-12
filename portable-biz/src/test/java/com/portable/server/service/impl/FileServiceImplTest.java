package com.portable.server.service.impl;

import com.Ostermiller.util.CircularByteBuffer;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.GridFsManagerImpl;
import com.portable.server.model.fs.FileData;
import com.portable.server.type.FileStoreType;
import com.portable.server.util.StreamUtils;
import com.portable.server.util.test.TestMockedValueMaker;
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

    private static final String MOCKED_FILE_NAME = TestMockedValueMaker.mString();
    private static final String MOCKED_FILE_TYPE = TestMockedValueMaker.mString();
    private static final String MOCKED_FILE_ID = TestMockedValueMaker.mString();
    private static final String MOCKED_FILE_DATA = TestMockedValueMaker.mString();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void uploadImage() throws PortableException {
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();

        fileService.uploadImage(circularByteBuffer.getInputStream(), MOCKED_FILE_NAME, MOCKED_FILE_TYPE);

        Mockito.verify(gridFsManager).uploadImage(circularByteBuffer.getInputStream(), MOCKED_FILE_NAME, MOCKED_FILE_TYPE);
    }

    @Test
    void get() throws IOException, PortableException {
        CircularByteBuffer circularByteBuffer = new CircularByteBuffer();
        circularByteBuffer.getOutputStream().write(MOCKED_FILE_DATA.getBytes());
        circularByteBuffer.getOutputStream().close();

        FileData fileData = FileData.builder()
                .inputStream(circularByteBuffer.getInputStream())
                .contentType(MOCKED_FILE_TYPE)
                .build();

        Mockito.when(gridFsManager.get(MOCKED_FILE_ID, FileStoreType.IMAGE)).thenReturn(fileData);

        CircularByteBuffer outRetVal = new CircularByteBuffer();

        String retVal = fileService.get(MOCKED_FILE_ID, FileStoreType.IMAGE, outRetVal.getOutputStream());
        outRetVal.getOutputStream().close();

        Assertions.assertEquals(MOCKED_FILE_TYPE, retVal);
        Assertions.assertEquals(MOCKED_FILE_DATA, StreamUtils.read(outRetVal.getInputStream()));
    }
}

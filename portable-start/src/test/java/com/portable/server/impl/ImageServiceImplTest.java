package com.portable.server.impl;

import java.io.IOException;

import com.portable.server.exception.PortableException;
import com.portable.server.manager.impl.ImageManagerImpl;
import com.portable.server.model.fs.FileData;
import com.portable.server.service.impl.ImageServiceImpl;
import com.portable.server.test.MockedValueMaker;
import com.portable.server.type.FileStoreType;
import com.portable.server.util.StreamUtils;

import com.Ostermiller.util.CircularByteBuffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @InjectMocks
    private ImageServiceImpl fileService;

    @Mock
    private ImageManagerImpl gridFsManager;

    private static final String MOCKED_FILE_NAME = MockedValueMaker.mString();
    private static final String MOCKED_FILE_TYPE = MockedValueMaker.mString();
    private static final String MOCKED_FILE_ID = MockedValueMaker.mString();
    private static final String MOCKED_FILE_DATA = MockedValueMaker.mString();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void uploadImage() {
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

        Mockito.when(gridFsManager.getImage(MOCKED_FILE_ID, FileStoreType.IMAGE)).thenReturn(fileData);

        CircularByteBuffer outRetVal = new CircularByteBuffer();

        String retVal = fileService.get(MOCKED_FILE_ID, FileStoreType.IMAGE, outRetVal.getOutputStream());
        outRetVal.getOutputStream().close();

        Assertions.assertEquals(MOCKED_FILE_TYPE, retVal);
        Assertions.assertEquals(MOCKED_FILE_DATA, StreamUtils.read(outRetVal.getInputStream()));
    }
}

package com.smartnote.server;

import org.junit.Test;

import com.smartnote.server.api.v1.Login;
import com.smartnote.server.api.v1.Upload;
import com.smartnote.server.util.MIME;
import com.smartnote.testing.BaseRoute;

/**
 * <p>Tests the system with multiple RPCs.</p>
 * 
 * @author Ethan Vrhel
 */
public class MultiRPCTest extends BaseRoute {
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testLoginAndUpload() throws Exception {
        Login login = new Login();
        Upload upload = new Upload();
        
        doApiTest(login, 200);

        // get the token from the login response
        addHeader("Authorization", responseHeader("Authorization"));
    
        setRequestBody(UploadTest.TEST_FILE_CONTENTS);
        setRequestQueryParam("name", UploadTest.TEST_FILE_NAME);
        setRequestContentType(MIME.PDF);

        doApiTest(upload, 200);
    }
}

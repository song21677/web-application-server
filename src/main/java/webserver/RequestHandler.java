package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedInputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
	    
	    InputStreamReader ir = new InputStreamReader(in);
	    BufferedReader br = new BufferedReader(ir);
	    DataOutputStream dos = new DataOutputStream(out);
 	    byte[] body;

	    String line = br.readLine();
	    String[] requestInfo = line.split(" ");
	    String path = requestInfo[1];
	
	    if (path.equals("/index.html")) {
    		body = Files.readAllBytes(Paths.get("webapp/index.html"));		    
	    } else {
	    	body = "First Update".getBytes();
	    }
	   // BufferedInputStream bf = new BufferedInputStream(in);
	   // byte[] bytes = new byte[8192];
	   // in.read(bytes, 0, bytes.length);
	   // for (int i=0; i<bytes.length; i++) {
	   // 	log.debug("{}", bytes[i]);
	   // }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

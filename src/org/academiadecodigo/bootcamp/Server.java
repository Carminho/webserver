package org.academiadecodigo.bootcamp;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.*;

public class Server {

    private final int PORT = 8080;


    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String get;


    public void run() {

        try {

            while (true) {
                connect();

            }
        } catch (IOException ex) {
            ex.getCause();
            ex.getMessage();
        }
    }


    private void connect() throws IOException {

        Socket socket = serverSocket.accept();
        Thread thread = new Thread(new Connection(socket));
        thread.start();
        System.out.println("A new connection with " + socket.getInetAddress() + " at port " + PORT + " was established.");
    }







    private class Connection implements Runnable {

        private Socket socket;
        private BufferedReader request;
        private DataOutputStream response;

        public Connection(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {
            String headerRequest = browserGetRequest(socket);

            FileInputStream fileContent = null;
            File file = null;


            try {
                if (headerRequest.equals("/")) {
                    file = new File("www/index.html");
                    fileContent = new FileInputStream(file);                                         //create stream to read the file content
                } else {
                    file = new File("www" + headerRequest);
                    fileContent = new FileInputStream(file);                                         //create stream to read the file content
                }
            } catch (FileNotFoundException ex) {
                file = new File("www/error.jpg");
                try {
                    fileContent = new FileInputStream(file);                                             //create stream to read the file content
                } catch (IOException e) {
                    System.out.println("IOexception test");
                }
            } finally {
                try {
                    System.out.println(file.getPath());
                    response = new DataOutputStream(socket.getOutputStream());
                    byte[] buffer = new byte[1024];                                                     //create a buffer to store bytes read from file
                    int bytesRead = 0;                                                                  //read file content and stores it in fileBytes array

                    setHeader(file, checkType(headerRequest));

                    while ((bytesRead = fileContent.read(buffer)) != -1) {                              //bytesRead is empty when return is -1
                        response.write(buffer, 0, bytesRead);                                      //send fileBytes by socket outputStream
                    }
                    response.flush();
                }
                catch (IOException ex){

                }

            }

            try {
                socket.close();
                System.out.println("socket closed");
            }
            catch (IOException ex){
                ex.getMessage();
            }

        }
        private String browserGetRequest(Socket socket)  {
            try {
                request = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String test = null;
            try {
                test = request.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //get = request.readLine();
            return test.split(" ")[1];

        }

        private void setHeader(File file, String type) throws IOException {

            if (file.getPath().equals("www\\error.jpg")) {
                type = "image/jpg";
            }
            response.writeBytes("HTTP/1.1 " + checkStatus(file) + " Document Follows\r\n" +
                    "Content-Type: " + type + checkCharset(type) +
                    "Content-Length: " + file.length() + "\r\n" +
                    "\r\n");
        }


        private String checkType(String get) {
            String format = get.substring(get.indexOf('.') + 1);
            String type = "";

            if (get.equals("/") || format.equals("html")) {
                return "text/html";
            }

            switch (format) {
                case "jpg":
                    type = "image/jpg";
                    break;
                case "jpeg":
                    type = "image/jpeg";
                    break;
                case "png":
                    type = "image/png";
                    break;
                case "ico":
                    type = "image/png";
                    break;
            }

            return type;
        }


        private String checkCharset(String type) {
            String charset = "";

            if (type == "text/html") {
                charset = "; charset=UTF-8\r\n";
            } else {
                charset = "\r\n";
            }
            return charset;
        }


        private int checkStatus(File file) {
            int status = 0;

            if (file.getPath().equals("www\\error.jpg")) {
                status = 404;
            } else {
                status = 200;
            }
            return status;
        }
    }

}



/*    private void setHeader(File file, String type) throws IOException {
        response.writeBytes("HTTP/1.1 200 Document Follows\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n");
    }
    */

/*
HTTP/1.0 200 Document Follows\r\n
Content-Type: text/html; charset=UTF-8\r\n
Content-Length: <file_byte_size> \r\n
\r\n
HTTP/1.0 200 Document Follows\r\n
Content-Type: image/<image_file_extension> \r\n
Content-Length: <file_byte_size> \r\n
\r\n
HTTP/1.0 404 Not Found\r\n
Content-Type: text/html; charset=UTF-8\r\n
Content-Length: <file_byte_size> \r\n
\r\n
 */
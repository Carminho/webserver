package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class test {
    private final int PORT = 8080;

    private BufferedReader request;
    private DataOutputStream response;
    private ServerSocket serverSocket;
    private Socket socket;
    private String get;


    public void run() throws IOException {

        connect();
        while(true){
            browserGetRequest();
            response();}
    }


    private void connect() throws IOException {
        serverSocket = new ServerSocket(PORT);
        socket = serverSocket.accept();
        System.out.println("A new connection with " + socket.getInetAddress() + " at port " + PORT + " was established.");
    }


    private void browserGetRequest() throws IOException {
        request = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        get = request.readLine();
        get = get.split(" ")[1];
        System.out.println("Path request: " + get);
    }


    private void response() throws IOException {

        try {
            File file = new File("www" + get);
            FileInputStream fileContent = new FileInputStream(file);                         //create stream to read the file content
            response = new DataOutputStream(socket.getOutputStream());

            byte[] buffer = new byte[1024];                                                  //create a buffer to store bytes read from file
            int bytesRead = 0;                                                               //read file content and stores it in fileBytes array

            setHeader(file, checkType());

            while ((bytesRead = fileContent.read(buffer)) != -1) {                           //bytesRead is empty when return is -1
                response.write(buffer, 0, bytesRead);                                   //send fileBytes by socket outputStream
            }
            response.flush();

        }

        catch (FileNotFoundException ex){
            File file = new File("www/error.jpg");
            FileInputStream fileContent = new FileInputStream(file);                         //create stream to read the file content
            response = new DataOutputStream(socket.getOutputStream());

            byte[] buffer = new byte[1024];                                                  //create a buffer to store bytes read from file
            int bytesRead = 0;                                                               //read file content and stores it in fileBytes array

            response.writeBytes("HTTP/1.1 404 Document Follows\r\n" +
                    "Content-Type: image/jpg\r\n" +
                    "Content-Length: " + file.length() + "\r\n" +
                    "\r\n");

            while ((bytesRead = fileContent.read(buffer)) != -1) {                           //bytesRead is empty when return is -1
                response.write(buffer, 0, bytesRead);                                   //send fileBytes by socket outputStream
            }
            response.flush();

        }

    }


    private void setHeader(File file, String type) throws IOException {

        response.writeBytes("HTTP/1.1 " + checkStatus(file) + " Document Follows\r\n" +
                "Content-Type: " + type + checkCharset(type) +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n");
    }


    private String checkType (){
        String format = get.substring(get.indexOf('.')+1);
        String type = "";

        switch (format){
            case "html": type = "text/html";break;
            case "jpg": type = "image/jpg";break;
            case "jpeg": type = "image/jpeg";break;
            case "png": type = "image/png";break;
            case "": type = "text/html";break;
            case "ico": type = "image/png";break;
            default: System.out.println("SOMETHING WENT WRONG");break;
        }

        return type;
    }


    private String checkCharset (String type){
        String charset = "";

        if (type == "text/html"){
            charset = "; charset=UTF-8\r\n";
        }
        else {
            charset = "\r\n";
        }
        return charset;
    }


    private int checkStatus (File file){
        int status = 0;

        if (file.getPath().equals("www/error.jpg")){
            status = 404;
        }
        else {
            status = 200;
        }
        return status;
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

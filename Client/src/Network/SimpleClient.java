package Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SimpleClient {

    private SocketChannel sc;
    private static Charset charset;
    private StringBuffer reqString = new StringBuffer();
    private ByteBuffer bb;
    String msg;

    public SimpleClient(String host, int port) throws IOException, InterruptedException {
        try {

            sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress(host, port));
            System.out.println("Connecting to the server...");

            while(!sc.finishConnect()) {
                System.out.println("Connection is being established...");
            }
        } catch (IOException exc) {
            System.out.println("IO exception");
            System.exit(1);
        }
        System.out.println("Connection Established!");

        makeRequest("echo Test input stream\n");
        Thread.sleep(50000);
        readRequest();
    }//constructor

    private void makeRequest(String req) throws IOException {
        System.out.println("Request: " + req);
        bb = ByteBuffer.allocate(1024);
        bb.put(req.getBytes());
        bb.rewind();
        sc.write(bb);
    }//makeRequest method

    public void readRequest() throws IOException, InterruptedException {

        reqString.setLength(0);
        bb.clear();

        try {
            readLoop:
            while (true) {
                bb.clear();
                int readBytes = sc.read(bb);
                if(readBytes == 0){
                    System.out.println("waiting for data");
                    continue;
                }
                else if(readBytes == -1) {
                    System.out.println("Server not responding");
                    break;
                }
                else {
                    bb.flip();
                    CharBuffer cbuf = charset.decode(bb);
                    while(cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        reqString.append(c);
                    }
                }
            }//while loop
            System.out.println(reqString.toString());

        } catch( Exception exc) {//while loop
            exc.printStackTrace();
        }
    }//readRequest method

    public static void main(String[] args)  {
        try {
            new SimpleClient("localhost", 9998);
        }catch (IOException exc) {
            exc.printStackTrace();
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }//main method
}//class
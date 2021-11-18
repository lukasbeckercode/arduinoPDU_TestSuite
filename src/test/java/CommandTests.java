import com.fazecast.jSerialComm.SerialPort;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.PrintWriter;

@SuppressWarnings({"StatementWithEmptyBody", "LoopConditionNotUpdatedInsideLoop"})
public class CommandTests {
    SerialPort port;
    InputStream in;
    PrintWriter out;
    Services services;
    @BeforeClass
    public void setUpClass() throws InterruptedException {
        port = SerialPort.getCommPorts()[2];
        port.setBaudRate(9600);

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        Assert.assertTrue(port.openPort());

        Thread.sleep(5000);
        System.out.println("port used: " + port.getSystemPortName());

        in = port.getInputStream();

        out = new PrintWriter(port.getOutputStream());

        services = new Services(in,out);
    }
    @org.testng.annotations.BeforeMethod
    public void setUp() throws InterruptedException {
        Thread.sleep(1000);
        services.readResponse(port.bytesAvailable());
    }

    @SuppressWarnings("BusyWait")
    @Test(testName = "HelloWorldTest")
    public void t0100_HelloWorld() throws InterruptedException {
        RAPDU rapdu = new RAPDU() ;
        Thread writeThread = new Thread(()-> services.sendCommand("0100"));

        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(16)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        System.out.println(rapdu.getResp());
        Assert.assertEquals(rapdu.getSw(),"9000");

    }

    @SuppressWarnings("BusyWait")
    @Test(testName = "Echo Test")
    public void t0101_Echo() throws InterruptedException {
        RAPDU rapdu = new RAPDU();
        Thread writeThread = new Thread(()->services.sendCommand("010100041234"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(8)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        System.out.println(rapdu.getResp());
        Assert.assertEquals(rapdu.getResp(),"1234");
        Assert.assertEquals(rapdu.getSw(),"9000");
    }


    @SuppressWarnings("BusyWait")
    @Test(testName = "Echo Test with wrong data")
    public void t0101_Echo_2() throws InterruptedException {
        RAPDU rapdu = new RAPDU();
        Thread writeThread = new Thread(()->services.sendCommand("01010003123"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        System.out.println(rapdu.getResp());
        Assert.assertEquals(rapdu.getSw(),"6700");
    }

    @Test(testName = "Store data Tests")
    public void t0200_StoreData() throws InterruptedException {
        RAPDU rapdu = new RAPDU();
        Thread writeThread = new Thread(()->services.sendCommand("020002041234"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");

    }

    @Test(testName = "getData")
    public void t0201_getData() throws InterruptedException {
        RAPDU rapdu = new RAPDU();
        Thread writeThread = new Thread(()->services.sendCommand("020102"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(8)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getResp(),"1234");
        Assert.assertEquals(rapdu.getSw(),"9000");
    }

    @Test(testName = "set pin to high")
    public void t0300_pinHigh() throws InterruptedException {
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("030110"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"6986");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("031110"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");


        writeThread = new Thread(()->services.sendCommand("030110"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");
    }


    @Test(testName = "pin status test")
    public void t0301_TryPinReset() throws InterruptedException {
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("031110"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("031010"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"6985");


        writeThread = new Thread(()->services.sendCommand("0399"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");

        writeThread = new Thread(()->services.sendCommand("031010"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");
    }

    @Test(testName = "test specific pin reset")
    public void t0390_pinHigh() throws InterruptedException {
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("031110"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("031010"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"6985");


        writeThread = new Thread(()->services.sendCommand("039910"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");

        writeThread = new Thread(()->services.sendCommand("031010"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");
    }

    @Test(testName = "test analogOut::BestCase::BC_WrongDataLength")
    public void t0303_analogOut() throws InterruptedException {
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("031110"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("030310040512"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");


        writeThread = new Thread(()->services.sendCommand("0303100212"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"6700");

    }

    @Test(testName = "test analogOut with wrong pin status::BC_pinUnavailable")
    public void t0303_analogOut_2() throws InterruptedException {
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("031010"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("030310040512"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"6986");


        writeThread = new Thread(()->services.sendCommand("039910"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");

        writeThread = new Thread(()->services.sendCommand("031110"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");

        writeThread = new Thread(()->services.sendCommand("030310040512"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");

    }

    @Test(testName = "test analogOut on non-pwm pin::BC_???")
    public void t0303_analogOut_3() throws InterruptedException {
        //TODO: not yet implemented on arduino side
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("031108"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("030308040512"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"6986");
    }

    @Test(testName = "test analogIn::BestCase")
    public void t0304_analogIn() throws InterruptedException {
        //TODO: not yet implemented on arduino side
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("03106500"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
        System.out.println(rapdu.getSw());

        writeThread = new Thread(()->services.sendCommand("03046500"));
        writeThread.start();

        readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }
        System.out.println(rapdu.getSw());
        Assert.assertEquals(rapdu.getSw(),"9000");
    }









/*--------------------------------------------------------------------------------------------------------------------*/

    @AfterMethod
    public void reset() throws InterruptedException {
        RAPDU rapdu = new RAPDU();

        Thread writeThread = new Thread(()->services.sendCommand("0399"));
        writeThread.start();

        Thread readThread = new Thread(()-> rapdu.setRapdu(services.readResponse(4)));

        while (port.bytesAvailable() == 0){
            //noinspection BusyWait
            Thread.sleep(20);
        }

        readThread.start();

        while (readThread.isAlive()){
            //Empty Body: Wait for other thread to finish
        }

        Assert.assertEquals(rapdu.getSw(),"9000");
    }

    @org.testng.annotations.AfterClass
    public void tearDown() {
        System.out.println("Port closed");
        port.closePort();
    }
}
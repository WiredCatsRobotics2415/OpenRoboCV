
package org.usfirst.frc.team2415.robot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Robot extends SampleRobot {
	
	USBCamera niCam;
	NIVision.Image frame;
	NIVision.RawData colorTable;
	
	final int PORT = 2415;
	
	ServerSocket imgServerSocket;
	OutputStream out;
	
    public void robotInit() {
    	
    	niCam = new USBCamera("cam0");
    	frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
    	colorTable = new NIVision.RawData();
    	
    	niCam.openCamera();
		niCam.startCapture();
		niCam.setSize(640, 480);
		niCam.setExposureManual(25);
		niCam.setExposureHoldCurrent();
		niCam.setWhiteBalanceManual(USBCamera.WhiteBalance.kFixedFlourescent2);
		niCam.setWhiteBalanceHoldCurrent();
		
		try {
			imgServerSocket = new ServerSocket(PORT);
			imgServerSocket.setSoTimeout(10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    public void disabled(){
    	while(isDisabled()){
    		niCam.getImage(frame);
    		CameraServer.getInstance().setImage(frame);
    	}
    	
    }
    
    public void autonomous() {
    	long start = System.currentTimeMillis();
    	int secs = 3, lastSec = -1;
    	
    	System.out.print("Taking capture in ");
    	while((System.currentTimeMillis() - start)/1000.0 <= secs){
    		if((System.currentTimeMillis() - start)/1000 != lastSec){
    			System.out.println((secs - ((System.currentTimeMillis() - start)/1000)) + "!");
    			lastSec = (int)(System.currentTimeMillis() - start)/1000;
    		}
    	}
    	
    	niCam.getImage(frame);
    	NIVision.imaqWriteJPEGFile(frame, "/home/lvuser/img.jpg", 2000, colorTable);
    	
    	try {
			Socket server = imgServerSocket.accept();
			out = server.getOutputStream();
			File load = new File("/home/lvuser/img.jpg");
			byte[] buffer = new byte[(int)load.length()];
			
			int count;
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(load));
			
			while((count = in.read(buffer)) > 0){
				  out.write(buffer, 0, count);
				  out.flush();
			}
			
			server.close();
			System.out.println("Image sent!");
		} catch (IOException e){
			e.printStackTrace();
		}
    }
    
    public void operatorControl() {
    }

    public void test() {
    }
}

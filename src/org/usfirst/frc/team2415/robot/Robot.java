
package org.usfirst.frc.team2415.robot;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Robot extends SampleRobot {
	static{
		try{
			System.load("/home/lvuser/lib_OpenCV/java/libopencv_java2410.so");
		}catch(UnsatisfiedLinkError e){
			e.printStackTrace();
		}
	}
	
	final int HUE_L = 0, SAT_L = 217, VIS_L = 204,
			  HUE_H = 20, SAT_H = 255, VIS_H = 255;
	
	final Scalar low = new Scalar(HUE_L, SAT_L, VIS_L);
	final Scalar high = new Scalar(HUE_H, SAT_H, VIS_H);
	
	Mat src, bin, morph;
	Mat struct;
	
	USBCamera niCam;
	NIVision.Image frame;
	NIVision.RawData colorTable;
    
    public void robotInit() {
    	src = bin = morph = new Mat();
    	struct = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
    	
    	
    	niCam = new USBCamera("cam0");
    	frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_HSL, 0);
    	colorTable = new NIVision.RawData();
    	
    	niCam.openCamera();
		niCam.startCapture();
		niCam.setSize(640, 480);
		niCam.setExposureManual(25);
		niCam.setExposureHoldCurrent();
		niCam.setWhiteBalanceManual(USBCamera.WhiteBalance.kFixedFlourescent2);
		niCam.setWhiteBalanceHoldCurrent();
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
    	while((System.currentTimeMillis() - start)/1000.0 <= secs){
    		if((System.currentTimeMillis() - start)/1000 != lastSec)
				System.out.println((secs - (System.currentTimeMillis() - start)/1000) + "!");
    			lastSec = (int)(System.currentTimeMillis() - start)/1000;
    	}
    	niCam.getImage(frame);
    	NIVision.imaqWriteJPEGFile(frame, "/home/lvuser/img.jpg", 2000, colorTable);
    	
    	src = Highgui.imread("/home/lvuser/img.jpg");
    	Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
    	Core.inRange(src, low, high, bin);
    	Highgui.imwrite("/home/lvuser/bin.jpg", bin);
    	
    	Imgproc.dilate(bin, morph, struct);
    	Highgui.imwrite("/home/lvuser/morph.jpg", morph);
    }
    
    public void operatorControl() {
    }

    public void test() {
    }
}

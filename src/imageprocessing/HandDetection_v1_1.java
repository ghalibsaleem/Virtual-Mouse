/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessing;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_imgproc.*; 
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_CAP_ANY;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateCameraCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvNamedWindow;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import helper.HelperClass;
import helper.ImageUtils;

/**
 *
 * @author ghalib
 */
public class HandDetection_v1_1 {
    public void detect(){
        CvCapture cvCapture = cvCreateCameraCapture(CV_CAP_ANY);
        int c = 0;
       
        
        cvNamedWindow("Virtual Mouse",CV_WINDOW_AUTOSIZE);
        
        if(cvCapture == null || cvQueryFrame(cvCapture).isNull()){
            System.out.println("Video capture failed, please check the camera.");
            return;
        }
        else{
            System.out.println("Video camera capture status: OK");
        }
        ImageUtils.init(cvCapture);
     
        CvScalar  hsv_min = cvScalar(0, 100, 100, 0);

	CvScalar  hsv_max = cvScalar(179, 255, 255, 0);
        while (c!=27) {            
            ImageUtils.image = cvQueryFrame(cvCapture);
            
            cvNamedWindow( "src",1); 
            cvFlip(ImageUtils.image,ImageUtils.image, 1);
            //cvShowImage( "src", ImageUtils.image);
            cvSmooth(ImageUtils.image, ImageUtils.image, CV_MEDIAN, 9, 9, 2, 2);
            cvSmooth(ImageUtils.image, ImageUtils.image, CV_GAUSSIAN, 9, 9, 2, 2);
            //cvShowImage( "src", ImageUtils.image);
            
            
            cvCvtColor(ImageUtils.image, ImageUtils.temp_image3, CV_BGR2HSV);
            //cvNamedWindow( "hsv-img",1); 
            //cvShowImage( "hsv-img", ImageUtils.temp_image3);
            
            cvInRangeS (ImageUtils.temp_image3, hsv_min, hsv_max, ImageUtils.thr_image);
            cvNamedWindow( "hsv-msk",1); 
            HelperClass.findContour();
            HelperClass.findConvexHull();
            HelperClass.findFingers();
            HelperClass.display();
            cvShowImage( "src", ImageUtils.image);
            
            cvShowImage( "hsv-msk", ImageUtils.thr_image);
            c = (char) opencv_highgui.cvWaitKey(30);
        }
        opencv_highgui.cvReleaseCapture(cvCapture);
        opencv_highgui.cvDestroyAllWindows();
    }
    
    private void setScaleFactor(){
    
    
    }
}
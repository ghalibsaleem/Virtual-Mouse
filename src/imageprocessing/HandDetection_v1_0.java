/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessing;

import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_video.BackgroundSubtractorMOG2;

/**
 *
 * @author ghalib
 */
public class HandDetection_v1_0 {
     BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(30, 16, true);
    public void detect(){
        CvCapture cvCapture = cvCreateCameraCapture(CV_CAP_ANY);
        
        int c = 0;
        
        cvNamedWindow("Virtual Mouse",CV_WINDOW_AUTOSIZE);
        
        if(cvQueryFrame(cvCapture).isNull()){
            System.out.println("Video capture failed, please check the camera.");
            return;
        }
        else{
            System.out.println("Video camera capture status: OK");
        }
        
        
        CvSize sz = cvGetSize(cvQueryFrame( cvCapture));
        //sz.height(200);
        //sz.width(400);
        IplImage src = cvCreateImage( sz, 8, 3 );

	IplImage hsv_image = cvCreateImage( sz, 8, 3);

	IplImage hsv_mask = cvCreateImage( sz, 8, 1);
        IplImage hsv_mask_prev = cvCreateImage( sz, 8, 1);
        IplImage prev = cvCreateImage( sz, 8, 3);
        
        prev =  cvQueryFrame(cvCapture);
        
	CvScalar  hsv_min = cvScalar(0, 30, 80, 0);

	opencv_core.CvScalar  hsv_max = cvScalar(20, 150, 255, 0);
        while (c!=27) {            
            src = cvQueryFrame(cvCapture);
            
            //src.width(400);
            //src.height(200);
            cvNamedWindow( "src",1); 
            
            //cvDilate(src, src, null, 2);
            cvSmooth(src, src, CV_MEDIAN, 9, 9, 1, 0);
            cvSmooth(src, src, CV_GAUSSIAN, 9, 9, 1, 0);
            cvShowImage( "src", src);
            
            
            cvCvtColor(src, hsv_image, CV_BGR2HSV);
            cvNamedWindow( "hsv-img",1); 
            cvShowImage( "hsv-img", hsv_image);
            
            cvInRangeS (hsv_image, hsv_min, hsv_max, hsv_mask);
            cvNamedWindow( "hsv-msk",1); 
            IplConvKernel convKernel = cvCreateStructuringElementEx(3,3,0,0,CV_SHAPE_RECT  ,null);
            
            //cvMorphologyEx(hsv_mask, hsv_mask, hsv_mask_prev, convKernel, CV_MOP_DILATE, 5);
            //cvMorphologyEx(hsv_mask, hsv_mask, hsv_mask_prev, cvCreateStructuringElementEx(10,10,0,0,CV_SHAPE_ELLIPSE  ,null), CV_MOP_ERODE, 3);
            //cvMorphologyEx(hsv_mask, hsv_mask, hsv_mask_prev, convKernel, CV_MOP_OPEN, 1);
            //cvMorphologyEx(hsv_mask, hsv_mask, hsv_mask_prev, convKernel, CV_MOP_TOPHAT, 5)
            //cvMorphologyEx(hsv_mask, hsv_mask, hsv_mask_prev, convKernel, CV_MOP_DILATE, 1);
            
            //helper.HelperClass.findCenter(hsv_mask);
            cvShowImage( "hsv-msk", hsv_mask);
            hsv_mask_prev = hsv_mask;
            c = (char) opencv_highgui.cvWaitKey(30);
            
        }
        opencv_highgui.cvReleaseCapture(cvCapture);
        opencv_highgui.cvDestroyAllWindows();
    }
}

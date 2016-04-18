/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessing;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_imgproc;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_GRADIENT;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_RECT;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateStructuringElementEx;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;
import com.googlecode.javacv.cpp.opencv_legacy.*;
import com.googlecode.javacv.cpp.opencv_video.*;

/**
 *
 * @author ghalib
 */
public class BackgroundProcessor {
    BackgroundSubtractorMOG2 backgroundSubtractorMOG2;
     
    
    
    public BackgroundProcessor() {
       backgroundSubtractorMOG2 = new BackgroundSubtractorMOG2(10, 16, false);
       
    }
    public void doSubstitution(){
        int c =0; 
        CvCapture cvCapture = cvCreateCameraCapture(CV_CAP_ANY);
        cvNamedWindow("Virtual Mouse",CV_WINDOW_AUTOSIZE);
        if(cvQueryFrame(cvCapture).isNull()){
            System.out.println("Video capture failed, please check the camera.");
            return;
        }
        else{
            System.out.println("Video camera capture status: OK");
        }
        CvSize sz = cvGetSize(cvQueryFrame( cvCapture));
        IplImage src = cvCreateImage( sz, 8, 3 );

	IplImage hsv_image = cvCreateImage( sz, 8, 3);

	IplImage hsv_mask = cvCreateImage( sz, 8, 1);
        IplImage hsv_mask_prev = cvCreateImage( sz, 8, 1);
        IplImage prev = cvCreateImage( sz, 8, 3);
        
        prev =  cvQueryFrame(cvCapture);
        while (c!=27) {            
            src = cvQueryFrame(cvCapture);
            backgroundSubtractorMOG2.apply(src, prev, 10);
            detect(src, prev);
            cvNamedWindow( "src",1); 
            cvShowImage( "src", src);
            cvNamedWindow( "prev",1); 
            cvShowImage( "prev", prev);
            opencv_imgproc.IplConvKernel convKernel = cvCreateStructuringElementEx(3,3,0,0,CV_SHAPE_RECT  ,null);
            
            
            
            c = (char) opencv_highgui.cvWaitKey(30);
        }
        opencv_highgui.cvReleaseCapture(cvCapture);
        opencv_highgui.cvDestroyAllWindows();
    }
    
    
    private void detect(IplImage orignal,IplImage masked){
       
    }
    
}

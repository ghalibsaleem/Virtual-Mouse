/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_RECT;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateStructuringElementEx;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

/**
 *
 * @author ghalib
 */
public class ImageUtils {
    
    public static CvCapture capture;
    
    public static IplImage image;
    public static IplImage thr_image;
    public static IplImage temp_image1;
    public static IplImage temp_image3;
    
    public static CvSeq contour;	
    public static CvSeq hull;
    
    public static CvPoint hand_center;
    public static ArrayList<CvPoint> fingers;	/* Detected fingers positions */
    public static ArrayList<CvPoint> defects;
    
    public static CvMemStorage hull_st;
    public static CvMemStorage contour_st;
    public static CvMemStorage temp_st;
    public static CvMemStorage defects_st;
    
    public static IplConvKernel kernel;
    
    public static int num_fingers=0;
    public static int hand_radius=0;
    public static int num_defects=0;
    
    public static int rightClickCount = 0;
    public static int leftClickCount = 0;
    
    public static double scaleFactorX = 1;
    public static double scaleFactorY = 1;
    
    public ImageUtils(CvCapture captureloc){
       capture = captureloc;
       CvSize sz = cvGetSize(cvQueryFrame( capture));
       thr_image = cvCreateImage(sz, 8, 1);
       image = cvCreateImage(sz, 8, 3);
       temp_image1 = cvCreateImage(sz, 8, 1);
       temp_image3 = cvCreateImage(sz, 8, 3);
       kernel = cvCreateStructuringElementEx(9, 9, 4, 4, CV_SHAPE_RECT, null);
       contour_st = cvCreateMemStorage(0);
       hull_st = cvCreateMemStorage(0);
       temp_st = cvCreateMemStorage(0);
       fingers = new ArrayList<>(6);
       defects = new ArrayList<>(8);
    }
    public static void init(CvCapture captureloc){
       capture = captureloc;
       CvSize sz = cvGetSize(cvQueryFrame( capture));
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       
       scaleFactorX = screenSize.width/ sz.width();
       scaleFactorX*=1.1;
       scaleFactorY*=1.1;
       scaleFactorY = screenSize.height/sz.height();
       
       thr_image = cvCreateImage(sz, 8, 1);
       image = cvCreateImage(sz, 8, 3);
       hand_center = new CvPoint();
       
       temp_image1 = cvCreateImage(sz, 8, 1);
       temp_image3 = cvCreateImage(sz, 8, 3);
       kernel = cvCreateStructuringElementEx(9, 9, 4, 4, CV_SHAPE_RECT, null);
       contour_st = cvCreateMemStorage(0);
       defects_st = cvCreateMemStorage(0);
       hull_st = cvCreateMemStorage(0);
       temp_st = cvCreateMemStorage(0);
       fingers = new ArrayList<>(6);
       defects = new ArrayList<>(8);
    }
}

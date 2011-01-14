package fr.lium.experimental.video;

/*import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;
import static name.audet.samuel.javacv.jna.highgui.*;
import static name.audet.samuel.javacv.jna.cvaux.*;
*/

class CamShiftDetector {
	/*
	// Parameters
	protected int   nHistBins;                 // number of histogram bins
	protected float rangesArr[];          // histogram range
	protected int vmin, vmax, smin; // limits for calculating hue

	// File-level variables
	protected IplImage pHSVImg; // the input image converted to HSV color mode
	protected IplImage pHueImg; // the Hue channel of the HSV image
	protected IplImage pMask; // this image is used for masking pixels
	protected IplImage pProbImg; // the face probability estimates for each pixel
	protected CvHistogram pHist; // histogram of hue in the original face image

	protected CvRect prevFaceRect;  // location of face in previous frame
	protected CvBox2D faceBox;      // current face-location estimate

	int nFrames;

	void updateHueImage(IplImage pImg){
		// Convert to HSV color model
		//cerr << "updateHueImage 1" << endl;
		cvCvtColor( pImg, pHSVImg, CV_BGR2HSV );
		//cerr << "updateHueImage 2" << endl;

		// Mask out-of-range values
		cvInRangeS( pHSVImg, cvScalar(0, smin, Math.min(vmin,vmax), 0),
				cvScalar(180, 256, Math.max(vmin,vmax) ,0), pMask );
		//cerr << "updateHueImage 3" << endl;

		// Extract the hue channel
		cvSplit( pHSVImg, pHueImg, 0, 0, 0 );
		//cerr << "updateHueImage 4" << endl;
	}

	void copyImage(CamShiftDetector camShift){
		release();
		if (camShift.pHSVImg != null) {
			pHSVImg  = cvCloneImage(camShift.pHSVImg);; // the input image converted to HSV color mode
		}
		if (camShift.pHueImg != null) {
			pHueImg  = cvCloneImage(camShift.pHueImg);; // the input image converted to HSV color mode
		}
		if (camShift.pMask != null) {
			pMask  = cvCloneImage(camShift.pMask);; // the input image converted to HSV color mode
		}
		if (camShift.pProbImg != null) {
			pProbImg  = cvCloneImage(camShift.pProbImg);; // the input image converted to HSV color mode
		}
		if (camShift.pHist != null) {
			cvCopyHist(camShift.pHist, pHist);
		}
	}

	public
	CamShiftDetector(){
		//cerr << "CamShiftDetector alloc 1" << endl;
		nHistBins = 30;                 // number of histogram bins
		rangesArr[0] = 0;
		rangesArr[1] = 255;          // histogram range
		vmin = 60;
		vmax = 256;
		smin = 50; // limits for calculating hue
		// File-level variables
		pHSVImg  = null; // the input image converted to HSV color mode
		pHueImg  = null; // the Hue channel of the HSV image
		pMask    = null; // this image is used for masking pixels
		pProbImg = null; // the face probability estimates for each pixel
		pHist = null; // histogram of hue in the original face image
		nFrames = 0;
	}

	CamShiftDetector(CamShiftDetector camShift){
		//cerr << "CamShiftDetector alloc copy" << endl;
		nHistBins = camShift.nHistBins;                 // number of histogram bins
		rangesArr[0] = camShift.rangesArr[0];
		rangesArr[1] = camShift.rangesArr[1];          // histogram range
		vmin = camShift.vmin;
		vmax = camShift.vmax;
		smin = camShift.smin; // limits for calculating hue
		// File-level variables
		pHSVImg  = null; // the input image converted to HSV color mode
		pHueImg  = null; // the Hue channel of the HSV image
		pMask    = null; // this image is used for masking pixels
		pProbImg = null; // the face probability estimates for each pixel
		pHist = null; // histogram of hue in the original face image
		nFrames = camShift.nFrames;
		prevFaceRect = camShift.prevFaceRect;  // location of face in previous frame
		faceBox = camShift.faceBox;      // current face-location estimate

		copyImage(camShift);

	}


	@Override protected void finalize() {
		try {
			release();
		} catch (Exception ex) { }
	}

	void release(){
		if (pHSVImg != null) {
			cvReleaseImage(pHSVImg);
		}
		if (pHueImg != null) cvReleaseImage(pHueImg );
		if (pMask != null) cvReleaseImage(pMask );
		if (pProbImg != null) cvReleaseImage(pProbImg );
		if (pHist != null) cvReleaseHist(pHist );
	}

	// Main Control functions
	void createTracker( IplImage pImg){
		release();
		// Allocate the main data structures ahead of time
		float pRanges = rangesArr;
		pHSVImg  = cvCreateImage( cvGetSize(pImg), 8, 3 );
		pHueImg  = cvCreateImage( cvGetSize(pImg), 8, 1 );
		pMask    = cvCreateImage( cvGetSize(pImg), 8, 1 );
		pProbImg = cvCreateImage( cvGetSize(pImg), 8, 1 );

		pHist = cvCreateHist( 1, nHistBins, CV_HIST_ARRAY, pRanges, 1 );
	}

	void startTracking(IplImage pImg, CvRect pFaceRect) {
		float maxVal = 0.f;

		// Make sure internal data structures have been allocated
		if( pHist == null) {
			createTracker(pImg);
			//cerr << "createTracking" << endl;
		}

		// Create a new hue image
		updateHueImage(pImg);

		// Create a histogram representation for the face
		cvSetImageROI( pHueImg, pFaceRect );
		cvSetImageROI( pMask, pFaceRect );
		cvCalcHist( pHueImg, pHist, 0, pMask );
		cvGetMinMaxHistValue( pHist, 0, maxVal, 0, 0 );
		cvConvertScale( pHist.bins, pHist.bins, maxVal ? 255.0/maxVal : 0, 0 );
		cvResetImageROI( pHueImg );
		cvResetImageROI( pMask );

		// Store the previous face location
		prevFaceRect = pFaceRect;
		//cerr << "startTracking prevFaceRect="<< prevFaceRect.width << " " << prevFaceRect.height << endl;
		//cerr << "startTracking pFaceRect="<< pFaceRect->width << " " << pFaceRect->height << endl;
	}

	CvRect track(IplImage pImg){
		CvConnectedComp components;
		if (pImg == null) {
			throw new NullPointerException( "CamShiftDetector::track() (0)");
		}
		// Create a new hue image
		updateHueImage(pImg);

		// Create a probability image based on the face histogram
		cvCalcBackProject( pHueImg, pProbImg, pHist );
		cvAnd( pProbImg, pMask, pProbImg, 0 );

		// Use CamShift to find the center of the new face probability
		cvCamShift( pProbImg, prevFaceRect,
				cvTermCriteria( CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1 ),
				components, faceBox );

		// Update face location and angle
		prevFaceRect = components.rect;
		faceBox.angle = -faceBox.angle;

		return prevFaceRect;
	}

	// Parameter settings
	void setVmin(int _vmin)
	{ vmin = _vmin; }


	void setSmin(int _smin)
	{ smin = _smin; }

	CvRect getPreviousRect() {
		return prevFaceRect;
	}*/
}

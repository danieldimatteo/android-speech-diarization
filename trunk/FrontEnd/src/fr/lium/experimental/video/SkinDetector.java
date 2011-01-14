package fr.lium.experimental.video;

/*import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;
import static name.audet.samuel.javacv.jna.highgui.*;
import static name.audet.samuel.javacv.jna.cvaux.*;
*/
class SkinDetector {
	/*protected
		IplImage filterMask;
		CvAdaptiveSkinDetector filter;
		CvSize size;
	public
		SkinDetector(CvSize _size){
			CvAdaptiveSkinDetector filter(1, CvAdaptiveSkinDetector.MORPHING_METHOD_ERODE_ERODE);
			size = _size;
			filterMask = cvCreateImage( size, IPL_DEPTH_8U, 1);
		}
		~SkinDetector(){
			if(filterMask != NULL) {
				cvReleaseImage(filterMask);
			}
		}
		
		void detect(IplImage * image){
			filter.process(image, filterMask);
		}
		
		double getSkinRatio(CvRect & rect){
			int nbTrue = 0;
			int nbFalse = 0;
			//int nWidth = filterMask->width;
			//int nHeight = filterMask->height;
			
			for(int x = rect.x; x < rect.x+rect.width; x++) {
				for(int y = rect.y; y < rect.y+rect.height; y++) {
					uchar c = ((uchar*)(filterMask->imageData + filterMask->width*y))[x];
					if (c) {
						nbTrue++;
					} else {
						nbFalse++;
					}
				}
			}
			double v = (double)nbTrue / (double)(nbTrue+nbFalse);
			//cerr << "skin:" << v << " " << nWidth << "=" << size.width<<endl;
			return v;
		}

	#define ASD_RGB_SET_PIXEL(pointer, r, g, b)	{ (*pointer) = (unsigned char)b; (*(pointer+1)) = (unsigned char)g;	(*(pointer+2)) = (unsigned char)r; }
		
	#define ASD_RGB_GET_PIXEL(pointer, r, g, b) {b = (unsigned char)(*(pointer)); g = (unsigned char)(*(pointer+1)); r = (unsigned char)(*(pointer+2));}
		
		void displayBuffer(IplImage *rgbDestImage)
		{
			int rValue = 0;
			int gValue = 255;
			int bValue = 0;
			int x, y, nWidth, nHeight;
			double destX, destY, dx, dy;
			uchar c;
			unsigned char *pSrc;
			
			nWidth = filterMask->width;
			nHeight = filterMask->height;
			
			dx = double(rgbDestImage->width)/double(nWidth);
			dy = double(rgbDestImage->height)/double(nHeight);
			
			destX = 0;
			for (x = 0; x < nWidth; x++)
			{
				destY = 0;
				for (y = 0; y < nHeight; y++)
				{
					c = ((uchar*)(filterMask->imageData + filterMask->widthStep*y))[x];
					
					if (c)
					{
						pSrc = (unsigned char *)rgbDestImage->imageData + rgbDestImage->widthStep*int(destY) + (int(destX)*rgbDestImage->nChannels);
						ASD_RGB_SET_PIXEL(pSrc, rValue, gValue, bValue);
					}
					destY += dy;
				}
				destY = 0;
				destX += dx;
			}
		}
		*/
	}

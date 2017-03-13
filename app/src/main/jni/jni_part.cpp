#include <jni.h>
#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <malloc.h>
#include <time.h>
#include <math.h>
#include <ctype.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>

#define LOG_TAG "camera/jni_part"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace cv;
using namespace std;

const double MHI_DURATION = 0.5;
const double MAX_TIME_DELTA = 0.5;
const double MIN_TIME_DELTA = 0.05;
const int N = 3;
const int CONTOUR_MAX_AERA = 200;
IplImage **buf = 0;
int last = 0;
IplImage *mhi = 0;
CvConnectedComp *cur_comp, min_comp;
CvConnectedComp comp;
CvMemStorage *storage;
CvPoint pt[4];
int people = 0;
int stream = 0;
int addP = 1;

void update_mhi(IplImage* m_rgb, IplImage* m_dst, int diff_threshold) {
	double timestamp = clock() / 100.;
	CvSize size = cvSize(m_rgb->width, m_rgb->height);
	int i, j, idx1, idx2;
	IplImage* silh;
	uchar val;
	float temp;
	IplImage* pyr = cvCreateImage(
			cvSize((size.width & -2) / 2, (size.height & -2) / 2), 8, 1);
	CvMemStorage *stor;
	CvSeq *cont, *result, *squares;
	CvSeqReader reader;

	if (!mhi || mhi->width != size.width || mhi->height != size.height) {
		if (buf == 0) {
			int length = N * sizeof(buf[0]);
			buf = (IplImage**) malloc(length);
			memset(buf, 0, length);
		}

		for (i = 0; i < N; i++) {
			cvReleaseImage(&buf[i]);
			buf[i] = cvCreateImage(size, IPL_DEPTH_8U, 1);
			cvZero(buf[i]);
		}
		cvReleaseImage(&mhi);
		mhi = cvCreateImage(size, IPL_DEPTH_32F, 1);
		cvZero(mhi);
	}

	cvCvtColor(m_rgb, buf[last], CV_BGR2GRAY);

	idx1 = last;
	idx2 = (last + 1) % N;
	last = idx2;

	// 做帧差
	silh = buf[idx2];
	cvAbsDiff(buf[idx1], buf[idx2], silh);

	// 对差图像做二值化
	cvThreshold(silh, silh, 30, 255, CV_THRESH_BINARY);

	cvUpdateMotionHistory(silh, mhi, timestamp, MHI_DURATION);
	cvCvtScale(mhi, m_dst, 255. / MHI_DURATION,
			(MHI_DURATION - timestamp) * 255. / MHI_DURATION);
	cvCvtScale(mhi, m_dst, 255. / MHI_DURATION, 0);

	// 中值滤波，消除小的噪声
	cvSmooth(m_dst, m_dst, CV_MEDIAN, 3, 0, 0, 0);

	// 向下采样，去掉噪声
	cvPyrDown(m_dst, pyr, 7);
	cvDilate(pyr, pyr, 0, 1); // 做膨胀操作，消除目标的不连续空洞
	cvPyrUp(pyr, m_dst, 7);

	// 下面的程序段用来找到轮廓
	int size1 = sizeof(CvSeq);
	int size2 = sizeof(CvPoint);
	stor = cvCreateMemStorage(0);
	cont = cvCreateSeq(CV_SEQ_ELTYPE_POINT, size1, size2, stor);

	// 找到所有轮廓
	int size3 = sizeof(CvContour);
	cvFindContours(m_dst, stor, &cont, size3, CV_RETR_LIST,
			CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

	//绘制轮廓
//	for (; cont; cont = cont->h_next) {
//		if (cont->total < 6)
//			continue;
//		cvDrawContours(m_rgb, cont, CV_RGB(255,0,0), CV_RGB(255,0,0), 0, 1, 8,
//				cvPoint(0, 0));
//	}

	// 直接使用CONTOUR中的矩形来画轮廓
	for (; cont; cont = cont->h_next) {
		CvRect r = ((CvContour*) cont)->rect;
		CvRect r2;
		CvSeq *cont_ne = cont->h_next;
		if (cont_ne)
			r2 = ((CvContour*) cont_ne)->rect;
		if ((r.height * r.width > CONTOUR_MAX_AERA)
				&& (r.height + r.width > diff_threshold)) // 面积小的方形抛弃掉
				{
			cvRectangle(m_rgb, cvPoint(r.x, r.y),
					cvPoint(r.x + r.width, r.y + r.height), CV_RGB(255,0,0), 1,
					CV_AA, 0);
			//统计车辆和人的数量
			if (r.x >= 10 && r.x < 40 && addP == 1) {
				if (r.height / r.width > 1)
					people++;
				if (r.height / r.width < 1)
					stream++;
				addP = -1;
			}
		}
		if ((r2.height * r2.width > CONTOUR_MAX_AERA)
				&& (r2.height + r2.width > diff_threshold)) // 面积小的方形抛弃掉
				{
			cvRectangle(m_rgb, cvPoint(r2.x, r2.y),
					cvPoint(r2.x + r2.width, r2.y + r2.height), CV_RGB(255,0,0),
					1, CV_AA, 0);
			//统计车辆和人的数量
			if (r.x >= 10 && r.x < 40 && addP == 1) {
				if (r.height / r.width > 1)
					people++;
				if (r.height / r.width < 1)
					stream++;
				addP = -1;
			}
		}
	}
	cvLine(m_rgb, Point(10, 0), Point(10, 500), CV_RGB(50,0,250), 1, CV_AA, 0);
	cvLine(m_rgb, Point(40, 0), Point(40, 500), CV_RGB(50,0,250), 1, CV_AA, 0);
	//绘制统计结果
	char* text = (char*) malloc(100);
	sprintf(text, "%s%d%s%d%s", "people : ", people / 5, " ; car : ", stream / 5," ; traffic : normal");
	CvPoint point = Point(10, 25);
	CvFont font;
	cvInitFont(&font, CV_FONT_HERSHEY_COMPLEX, 1, 1, 1, 2, 8);
	cvPutText(m_rgb, text, point, &font, CV_RGB(255,0,0));
	//释放内存
	cvReleaseMemStorage(&stor);
	cvReleaseImage(&pyr);
}

extern "C" {
JNIEXPORT void JNICALL Java_com_example_camera_Camera_FindFeatures(JNIEnv*,
		jobject, jlong addrGray, jlong addrRgba, jlong addrScr, jint width,
		jint height, jint diff);

JNIEXPORT void JNICALL Java_com_example_camera_Camera_FindFeatures(JNIEnv*,
		jobject, jlong addrGray, jlong addrRgba, jlong addrScr, jint width,
		jint height, jint diff) {
	if (addrRgba == -100 && addrScr == -100) {
//		退出初始化参数
		people = 0;
		stream = 0;
	} else {
		Mat& mRgb = *(Mat*) addrRgba;
//		Mat& mGr = *(Mat*) addrGray;
//		Mat& mScr = *(Mat*) addrScr;

		IplImage* m_scr = cvCreateImage(cvSize(mRgb.cols, mRgb.rows), 8, 3);
		IplImage* m_rgb = cvCreateImage(cvSize(mRgb.cols, mRgb.rows), 8, 4);
		m_rgb->imageData = (char*) mRgb.data;
//		IplImage* m_gray = cvCreateImage(cvSize(mGr.cols, mGr.rows), 8, 1);
//		m_gray->imageData = (char*) mGr.data;

		IplImage* m_dst = 0;
		if (!m_dst) {
			m_dst = cvCreateImage(cvSize(m_scr->width, m_scr->height), 8, 1);
			cvZero(m_dst);
			m_dst->origin = m_scr->origin;
		}

//		cvCanny(m_gray, m_scr, 80, 100);
//		cvCvtColor(m_scr, m_rgb, COLOR_GRAY2RGBA);

//		转换三维通道
		cvCvtColor(m_rgb, m_scr, CV_RGBA2RGB);
		addP = 1;
//		检测主方法
		update_mhi(m_scr, m_dst, 50 + diff);
//		处理过后的图像,显示到前台
		cvCvtColor(m_scr, m_rgb, CV_RGB2RGBA);
//		free memory
		cvReleaseImage(&m_scr);
		cvReleaseImage(&m_rgb);
	}
}
}

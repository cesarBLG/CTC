#include "CTCThread.h"
void CTCThread::run()
{
	while(true)
	{
		function<void()> r = nullptr;
		unique_lock<mutex> lck(mtx);
		if(tasks.empty()) cv.wait(lck);
		else
		{
			r = tasks.front();
			tasks.pop_front();
		}
		lck.unlock();
		if(r!=nullptr) r();
	}
}
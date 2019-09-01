/*******************************************************************************
 * Copyright (C) 2017-2018 César Benito Lamata
 * 
 * This file is part of SCRT.
 * 
 * SCRT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SCRT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SCRT.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
#pragma once
#include <functional>
#include <thread>
#include <mutex>
#include "Loader.h"
class CTCTimer
{
	public:
	int delay;
	bool running=false;
	function<void()> fun;
	CTCTimer(int delay, function<void()> fun) : delay(delay), fun(fun)
	{
	}
	void stop()
	{
		running = false;
	}
	void start()
	{
		running = true;
		thread thr([this]{
			this_thread::sleep_for(chrono::milliseconds(delay));
			if(running)
			{
				unique_lock<mutex> lck(Loader::ctcThread.mtx);
				Loader::ctcThread.tasks.push_back(fun);
				lck.unlock();
				Loader::ctcThread.cv.notify_one();
			}
			running = false;
		});
		thr.detach();
	}
};

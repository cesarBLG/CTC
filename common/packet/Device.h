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
#include <vector>
#include <thread>
using namespace std;
class Device
{
	public:
	virtual ~Device() {}
	virtual void write(int c) {}
	virtual void write(vector<char> data) {}
	virtual int read() {return 0;}
	virtual void read(char *buff, int size){}
	virtual void parse() = 0;
	void start()
	{
		thread thr([this]()
		{
			while(1)
			{
				parse();
			}
		});
		thr.detach();
	}
};

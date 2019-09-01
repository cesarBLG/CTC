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
#include "com/COM.h"
#include "packet/Device.h"
#ifdef WIN32
#include <winsock2.h>
#else
#include <unistd.h>
#include <arpa/inet.h>
#endif
#include <thread>
#include <sstream>
using namespace std;

class ClientListener : public Device
{
	int fd;
	public:
	ClientListener(int sockfd) : fd(sockfd)
	{
		start();
	}
	void write(int data)
	{
		::send(fd, (char*)&data, 1, 0);
	}
	void write(vector<char> data)
	{
		::send(fd, &data[0], data.size(), 0);
	}
	int read()
	{
		int c;
		::recv(fd, (char*)&c, 1, 0);
		return c;
	}
	void read(char *buff, int count)
	{
		::recv(fd, buff, count, 0);
	}
	void parse() override
	{
		COM::parse(this);
	}
};

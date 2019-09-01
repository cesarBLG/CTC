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
#include <fstream>
#include "CTCThread.h"
#include "packet/Packet.h"
#include "com/File.h"
using namespace std;
class TrackItem;
class Signal;
class AxleCounter;
class Station;
class Train;
class Loader {
	public:
	static vector<TrackItem*> items;
	static vector<Signal*> signals;
	static vector<AxleCounter*> counters;
	//static vector<Itinerary*> itineraries;
	static vector<Station*> stations;
	static vector<Train*> trains;
	static CTCThread ctcThread;
	static void load(string file)
	{
		Packet::start_factory();
		load(parseLayoutFile(file));
	}
	static vector<Packet*> parseLayoutFile(string file)
	{
		vector<Packet*> packets;
		ifstream layout;
		layout.open(file, ios::binary);
		while(!layout.eof())
		{
			Packet *p = Packet::construct(new DevStream(layout));
			if(p!=nullptr) packets.push_back(p);
			layout.peek();
		}
		layout.close();
		//cout<<"Packets read from layout binary file"<<endl;
		return packets;
	}
	static void load(vector<Packet*> packets);
	static void resolveLinks();
	/*static string ReadParameter(string data)
	{
		String s = null;
		int End = 0;
		for(int i=0; i<data.length(); i++)
		{
			if(data.charAt(i)=='#')
			{
				End = i;
				break;
			}
			if(i+1==data.length())
			{
				End = i + 1;
				break;
			}
		}
		s = data.substring(data.charAt(0)=='$' || data.charAt(0)=='!' ? 1 : 0, End);
		return s;
	}*/
};

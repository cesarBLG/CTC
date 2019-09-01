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
#include "Orientation.h"
#include "ID.h"
#include <vector>
#include <string>
#include "Aspect.h"
#include "SignalType.h"
using namespace std;
class SignalID : public ID
{
	public:
	SignalType Class;
	int Number;
	int Track;
	Orientation Direction;
	string Name;
	SignalID() : ID(ElementType::Signal) {}
	SignalID(char *&data) : ID(data)
	{
		Class = (SignalType)*data++;
		Number = *data++;
		Track = *data++;
		//cout<<Number<<Track<<(int)Class<<endl;
		Direction = ((Number % 2) == 0) ? Orientation::Even : Orientation::Odd;
		Name = string(SignalType_String[(int)Class]) + to_string(Number) + (Track!=0 ? (string("/") + to_string(Track)) : "");
		//cout<<"exit"<<endl;
	}
	SignalID(string name, int station)
	{
		Name = name;
		if(Name[0]=='S') Class = SignalType::Exit;
		else if((Name[0]=='E' && Name[1]!='\'') || Name[0] == 'F') Class = SignalType::Entry;
		else if(Name[0] == 'E' && Name[1] == '\'') Class = SignalType::Advanced;
		else if(Name[0]=='M') Class = SignalType::Shunting;
		else if(Name[0] == 'I' && Name[1] == 'S') Class = SignalType::Exit_Indicator;
		else Class = SignalType::Block;
		int start1 = -1;
		int end1 = 0;
		int start2 = 0;
		for(int i=0; i<Name.length();i++)
		{
			if(Name[i]<='9'&&Name[i]>='0'&&start1==-1) start1 = i;
			if(Name[i]=='/') end1 = i;
			if(Name[i]<='9'&&Name[i]>='0'&&end1!=0&&start2==0) start2 = i;
		}
		if(end1==0) end1 = Name.length();
		Number = stoi(Name.substr(start1, end1));
		if(start2!=0) Track = stoi(Name.substr(start2));
		else Track = 0;
		Direction = Number%2 == 0 ? Orientation::Even : Orientation::Odd;
		stationNumber = station;
		Name = SignalType_String[(int)Class] + to_string(Number) + (Track!=0 ? "/" + to_string(Track) : "");
	}
	vector<char> getId() override
	{
		vector<char> l = ID::getId();
		l.push_back((char)Class);
		l.push_back(Number);
		l.push_back(Track);
		return l;
	}
	ID* byState(char *&data) override {return new SignalID(data);}
};

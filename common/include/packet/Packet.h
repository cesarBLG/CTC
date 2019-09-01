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
#include <algorithm>
#include <iostream>
#include <sstream>
#include <map>
#include <set>
using namespace std;
class Device;
enum struct PacketType
{
	EmptyPacket,
	LinkPacket,
	SignalData,
	SignalRegister,
	TrackData,
	TrackRegister,
	JunctionData,
	JunctionSwitch,
	JunctionRegister,
	JunctionLock,
	ItineraryStablisher,
	ACData,
	RequestPacket,
	StationRegister,
	ClearOrder,
	AutomaticOrder,
	JunctionPositionSwitch,
	ConfigPacket,
	ExternalPacket
};
enum struct PacketGroup
{
	None,
	Register,
	Data,
	Action,
	Link,
    Order,
};
char getControl(const char* data, int length, int offset);
inline int booleanConvert(bool b0=false, bool b1=false, bool b2=false, bool b3 = false)
{
	return (b3<<3) + (b2<<2) + (b1<<1) + b0;
}
inline int booleanRead(int byte, int pos)
{
	return (byte>>pos) & 1;
}
class Packet
{
	public:
	PacketType type;
	PacketGroup group;
	bool statePacket = false;
	virtual vector<char> getListState() = 0;
	Packet(PacketType t, PacketGroup g);
	virtual ~Packet(){}
	virtual Packet* byState(char *&data) = 0;
    static map<PacketType, Packet*> factory;
    static void start_factory();
	static Packet *construct(Device *dev);
	vector<char> getState();
	static vector<char> toList(string s);
	static string toString(char *&data);
	void send(set<Device*> devs);
};
